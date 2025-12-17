#!/usr/bin/env bash
set -euo pipefail

# Cross-platform date handling (works on Git Bash/WSL/macOS/Linux)
PYTHON_BIN=${PYTHON_BIN:-python3}
if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
  PYTHON_BIN=python
fi

# Defaults (override via env var)
BASE_URL=${BASE_URL:-"http://localhost:8080/api/v1"}
LOG_FILE=${LOG_FILE:-"./artifacts/api-flow-$(date +%Y%m%d-%H%M%S).log"}
ADMIN_EMAIL=${ADMIN_EMAIL:-"admin@chrms.vn"}
ADMIN_PASSWORD=${ADMIN_PASSWORD:-"password123"}
PATIENT_EMAIL=${PATIENT_EMAIL:-"patient1@test.com"}
PATIENT_PASSWORD=${PATIENT_PASSWORD:-"password123"}
DOCTOR_EMAIL=${DOCTOR_EMAIL:-"doctor1@test.com"}
DOCTOR_PASSWORD=${DOCTOR_PASSWORD:-"password123"}
HOSPITAL_ID=${HOSPITAL_ID:-1}
DOCTOR_ID=${DOCTOR_ID:-1}
DEPARTMENT_ID=${DEPARTMENT_ID:-1}
APPOINTMENT_DATE=${APPOINTMENT_DATE:-$($PYTHON_BIN - <<'PY'
from datetime import datetime, timedelta
print((datetime.now() + timedelta(days=1)).strftime("%Y-%m-%d"))
PY
)}
APPOINTMENT_TIME_INPUT=${APPOINTMENT_TIME:-"09:00"}
APPOINTMENT_TIME=$($PYTHON_BIN - <<PY
from datetime import datetime
print(datetime.strptime("${APPOINTMENT_TIME_INPUT}", "%H:%M").strftime("%H:%M"))
PY
)
SCHEDULE_START_TIME=$($PYTHON_BIN - <<PY
from datetime import datetime
print(datetime.strptime("${APPOINTMENT_TIME_INPUT}", "%H:%M").strftime("%H:%M:%S"))
PY
)
SCHEDULE_END_TIME=$($PYTHON_BIN - <<PY
from datetime import datetime, timedelta
print((datetime.strptime("${APPOINTMENT_TIME_INPUT}", "%H:%M") + timedelta(hours=1)).strftime("%H:%M:%S"))
PY
)
CHAT_MESSAGE=${CHAT_MESSAGE:-"Xin chào bác sĩ"}

usage() {
  cat <<USAGE
Usage: BASE_URL=http://localhost:8080/api/v1 LOG_FILE=./artifacts/flow.log \\
       ADMIN_EMAIL=admin@chrms.vn ADMIN_PASSWORD=password123 \\
       PATIENT_EMAIL=patient1@test.com PATIENT_PASSWORD=password123 \\
       DOCTOR_EMAIL=doctor1@test.com DOCTOR_PASSWORD=password123 \\
       HOSPITAL_ID=1 DEPARTMENT_ID=1 DOCTOR_ID=1 \\
       APPOINTMENT_DATE=2025-01-01 APPOINTMENT_TIME=09:00 \\
       bash scripts/run_full_api_flow.sh

Dependencies: curl, jq, and python (for cross-platform date/time). Run inside Docker or any shell with Bash (Git Bash/WSL/macOS/Linux).
USAGE
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

require() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "[FATAL] Missing dependency: $1" >&2
    exit 1
  fi
}

require "curl"
require "jq"
require "$PYTHON_BIN"

mkdir -p "$(dirname "$LOG_FILE")"
: > "$LOG_FILE"

info() { echo "$*" | tee -a "$LOG_FILE"; }

hr() { info "============================================================"; }

title() {
  hr
  info "$1"
  hr
}

fail() {
  local actor="$1" status="$2" expected="$3" path="$4" body="$5"
  echo "[ERROR] [$actor] $path (expected $expected, got $status)" | tee -a "$LOG_FILE" >&2
  echo "$body" | tee -a "$LOG_FILE" >&2
  exit 1
}

call_api() {
  local actor="$1" method="$2" path="$3" body="$4" token="$5" expected="${6:-200}"

  info "\n---- [$actor] $method $path"
  [[ -n "$body" ]] && info "Request: $body"

  local headers=(-H "Content-Type: application/json")
  [[ -n "$token" ]] && headers+=( -H "Authorization: Bearer $token" )

  local response status_code payload curl_status
  if ! response=$(curl -sS -w "\n%{http_code}" -X "$method" "${BASE_URL}${path}" "${headers[@]}" ${body:+-d "$body"}); then
    curl_status=$?
    echo "[ERROR] [$actor] curl failed with exit code $curl_status. Check BASE_URL ($BASE_URL) and network connectivity." | tee -a "$LOG_FILE" >&2
    exit 1
  fi

  status_code=$(echo "$response" | tail -n1)
  payload=$(echo "$response" | sed '$d')

  if [[ "$status_code" == "000" ]]; then
    echo "[ERROR] [$actor] Unable to reach API (status 000). Ensure the backend is running and accessible at $BASE_URL. On Windows, make sure Docker/WSL exposes the port." | tee -a "$LOG_FILE" >&2
    exit 1
  fi

  info "Status: $status_code"
  info "Response: $payload"

  [[ "$status_code" == "$expected" ]] || fail "$actor" "$status_code" "$expected" "$path" "$payload"

  echo "$payload"
}

extract_or_fail() {
  local actor="$1" json="$2" jq_expr="$3" label="$4"
  local value
  if ! value=$(echo "$json" | jq -r "$jq_expr // empty"); then
    echo "[ERROR] [$actor] Response is not valid JSON while extracting $label" | tee -a "$LOG_FILE" >&2
    echo "$json" | tee -a "$LOG_FILE" >&2
    exit 1
  fi
  if [[ -z "$value" || "$value" == "null" ]]; then
    echo "[ERROR] [$actor] Cannot extract $label" | tee -a "$LOG_FILE" >&2
    echo "$json" | tee -a "$LOG_FILE" >&2
    exit 1
  fi
  echo "$value"
}

title "CHRMS end-to-end API flow"
info "Base URL         : $BASE_URL"
info "Log file         : $LOG_FILE"
info "Admin account    : $ADMIN_EMAIL"
info "Patient account  : $PATIENT_EMAIL"
info "Doctor account   : $DOCTOR_EMAIL"
info "Hospital ID      : $HOSPITAL_ID"
info "Department ID    : $DEPARTMENT_ID"
info "Doctor ID        : $DOCTOR_ID"
info "Appointment      : $APPOINTMENT_DATE $APPOINTMENT_TIME"

# 1) Authenticate all roles
admin_login=$(call_api "Admin" "POST" "/auth/login" "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASSWORD\"}" "" 200)
ADMIN_TOKEN=$(extract_or_fail "Admin" "$admin_login" '.data.token' 'admin token')

patient_login=$(call_api "Patient" "POST" "/auth/login" "{\"email\":\"$PATIENT_EMAIL\",\"password\":\"$PATIENT_PASSWORD\"}" "" 200)
PATIENT_TOKEN=$(extract_or_fail "Patient" "$patient_login" '.data.token' 'patient token')
PATIENT_ID=$(extract_or_fail "Patient" "$patient_login" '.data.userId' 'patient user id')

doctor_login=$(call_api "Doctor" "POST" "/auth/login" "{\"email\":\"$DOCTOR_EMAIL\",\"password\":\"$DOCTOR_PASSWORD\"}" "" 200)
DOCTOR_TOKEN=$(extract_or_fail "Doctor" "$doctor_login" '.data.token' 'doctor token')

# 2) Admin checks catalog data
call_api "Admin" "GET" "/hospitals" "" "$ADMIN_TOKEN" 200 >/dev/null
call_api "Admin" "GET" "/doctors" "" "$ADMIN_TOKEN" 200 >/dev/null

# 3) Doctor updates schedule for the appointment weekday
DAY_OF_WEEK=$($PYTHON_BIN - <<PY
from datetime import datetime
print(datetime.strptime("${APPOINTMENT_DATE}", "%Y-%m-%d").isoweekday())
PY
)
schedule_body=$(cat <<JSON
{
  "doctorId": $DOCTOR_ID,
  "dayOfWeek": $DAY_OF_WEEK,
  "startTime": "${SCHEDULE_START_TIME}",
  "endTime": "${SCHEDULE_END_TIME}",
  "isAvailable": true
}
JSON
)
call_api "Doctor" "POST" "/doctors/schedules" "$schedule_body" "$DOCTOR_TOKEN" 200 >/dev/null

# 4) Patient inspects available slots
call_api "Patient" "GET" "/doctors/${DOCTOR_ID}/available-slots?date=${APPOINTMENT_DATE}" "" "$PATIENT_TOKEN" 200 >/dev/null

# 5) Patient books an appointment (HH:mm, department required)
appointment_body=$(cat <<JSON
{
  "doctorId": $DOCTOR_ID,
  "hospitalId": $HOSPITAL_ID,
  "departmentId": $DEPARTMENT_ID,
  "appointmentDate": "${APPOINTMENT_DATE}",
  "appointmentTime": "${APPOINTMENT_TIME}",
  "notes": "Scripted end-to-end test"
}
JSON
)
appointment_resp=$(call_api "Patient" "POST" "/patients/appointments" "$appointment_body" "$PATIENT_TOKEN" 201)
APPOINTMENT_ID=$(extract_or_fail "Patient" "$appointment_resp" '.data.id' 'appointment id')

# 6) Patient reviews upcoming appointments
call_api "Patient" "GET" "/patients/appointments/upcoming" "" "$PATIENT_TOKEN" 200 >/dev/null

# 7) Payment flow
payment_body=$(cat <<JSON
{
  "appointmentId": $APPOINTMENT_ID,
  "paymentMethod": "CASH"
}
JSON
)
payment_resp=$(call_api "Patient" "POST" "/payments" "$payment_body" "$PATIENT_TOKEN" 201)
TRANSACTION_REF=$(extract_or_fail "Patient" "$payment_resp" '.data.transactionRef // .data.transactionReference' 'payment transactionRef')
call_api "Patient" "POST" "/payments/${TRANSACTION_REF}/complete" "" "$PATIENT_TOKEN" 200 >/dev/null

# 8) Doctor writes and approves medical record
record_body=$(cat <<JSON
{
  "appointmentId": $APPOINTMENT_ID,
  "symptoms": "Đau đầu, sốt nhẹ",
  "diagnosis": "Viêm phế quản cấp",
  "treatment": "Nghỉ ngơi, uống nhiều nước, dùng thuốc kháng sinh",
  "notes": "Theo dõi nhiệt độ mỗi 6 giờ"
}
JSON
)
record_resp=$(call_api "Doctor" "POST" "/medical-records" "$record_body" "$DOCTOR_TOKEN" 201)
RECORD_ID=$(extract_or_fail "Doctor" "$record_resp" '.data.id' 'medical record id')
call_api "Doctor" "POST" "/medical-records/${RECORD_ID}/approve" "" "$DOCTOR_TOKEN" 200 >/dev/null

# 9) Doctor issues a prescription
prescription_body=$(cat <<JSON
{
  "medicalRecordId": $RECORD_ID,
  "items": [
    {"medicineId": 1, "dosage": "500mg", "frequency": "2 lần/ngày", "duration": "7 ngày", "quantity": 14, "instructions": "Uống sau khi ăn"},
    {"medicineId": 2, "dosage": "200mg", "frequency": "3 lần/ngày", "duration": "5 ngày", "quantity": 15, "instructions": "Uống trước khi ăn"}
  ]
}
JSON
)
call_api "Doctor" "POST" "/prescriptions" "$prescription_body" "$DOCTOR_TOKEN" 201 >/dev/null

# 10) Chat and feedback
call_api "Patient" "POST" "/chat/appointments/${APPOINTMENT_ID}/messages" "{\"content\":\"$CHAT_MESSAGE\"}" "$PATIENT_TOKEN" 200 >/dev/null
call_api "Doctor" "GET" "/chat/appointments/${APPOINTMENT_ID}/messages/unread" "" "$DOCTOR_TOKEN" 200 >/dev/null
feedback_body=$(cat <<JSON
{
  "doctorId": $DOCTOR_ID,
  "rating": 5,
  "comment": "Tư vấn tận tình - script auto test"
}
JSON
)
call_api "Patient" "POST" "/feedback" "$feedback_body" "$PATIENT_TOKEN" 201 >/dev/null

# 11) Patient fetches medical records
call_api "Patient" "GET" "/medical-records/patient/${PATIENT_ID}" "" "$PATIENT_TOKEN" 200 >/dev/null

info "\n[OK] Flow finished. See log: $LOG_FILE"
