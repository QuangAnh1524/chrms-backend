#!/usr/bin/env bash
set -euo pipefail

# Cross-platform python
PYTHON_BIN=${PYTHON_BIN:-python3}
if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
  PYTHON_BIN=python
fi

# Defaults (override via env var)
BASE_URL=${BASE_URL:-"http://localhost:8080/api/v1"}
LOG_FILE=${LOG_FILE:-"./artifacts/api-flow-$(date +%Y%m%d-%H%M%S).log"}
AUTO_REGISTER_PATIENT=${AUTO_REGISTER_PATIENT:-"true"}

ADMIN_EMAIL=${ADMIN_EMAIL:-"admintest@test.com"}
ADMIN_PASSWORD=${ADMIN_PASSWORD:-"password123"}

PATIENT_EMAIL=${PATIENT_EMAIL:-"nqanh1524@gmail.com"}
PATIENT_PASSWORD=${PATIENT_PASSWORD:-"password123"}
PATIENT_FULL_NAME=${PATIENT_FULL_NAME:-"API Flow Patient"}
PATIENT_DOB=${PATIENT_DOB:-"2004-02-15"}
PATIENT_GENDER=${PATIENT_GENDER:-"MALE"}

DOCTOR_EMAIL=${DOCTOR_EMAIL:-"doctortest@test.com"}
DOCTOR_PASSWORD=${DOCTOR_PASSWORD:-"password123"}

HOSPITAL_ID=${HOSPITAL_ID:-1}
DOCTOR_ID=${DOCTOR_ID:-1}
DEPARTMENT_ID=${DEPARTMENT_ID:-1}

APPOINTMENT_DATE=${APPOINTMENT_DATE:-$($PYTHON_BIN - <<'PY'
from datetime import datetime, timedelta
print((datetime.now() + timedelta(days=3)).strftime("%Y-%m-%d"))
PY
)}

# IMPORTANT: must match "%H:%M"
APPOINTMENT_TIME_INPUT=${APPOINTMENT_TIME:-"02:00"}

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

CHAT_MESSAGE=${CHAT_MESSAGE:-"Xin chao doctor"}

# Global last call result (avoid subshell issues)
LAST_STATUS_CODE=""
LAST_PAYLOAD=""

usage() {
  cat <<USAGE
Usage: BASE_URL=http://localhost:8080/api/v1 LOG_FILE=./artifacts/flow.log \\
       AUTO_REGISTER_PATIENT=true \\
       ADMIN_EMAIL=admintest@test.com ADMIN_PASSWORD=password123 \\
       PATIENT_EMAIL=ninoel2004@gmail.com PATIENT_PASSWORD=password123 \\
       PATIENT_FULL_NAME="API Flow Patient" PATIENT_DOB=2004-02-15 PATIENT_GENDER=MALE \\
       DOCTOR_EMAIL=doctor@test.com DOCTOR_PASSWORD=password123 \\
       HOSPITAL_ID=1 DEPARTMENT_ID=1 DOCTOR_ID=1 \\
       APPOINTMENT_DATE=2025-01-01 APPOINTMENT_TIME=09:00 \\
       bash scripts/run_full_api_flow.sh

Dependencies: curl, jq, and python (for cross-platform date/time).
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

info() { echo "$*" | tee -a "$LOG_FILE" >&2; }
hr() { info "============================================================"; }

title() {
  hr
  info "$1"
  hr
}

fail() {
  local actor="$1" status="$2" expected="$3" path="$4" body="$5"
  info "[ERROR] [$actor] $path (expected $expected, got $status)"
  info "$body"
  exit 1
}

call_api() {
  local actor="$1" method="$2" path="$3" body="${4:-}" token="${5:-}" expected_raw="${6:-200}"
  IFS=',' read -r -a expected_statuses <<< "$expected_raw"

  info ""
  info "---- [$actor] $method $path"
  [[ -n "$body" ]] && info "Request: $body"

  local headers=(-H "Content-Type: application/json")
  [[ -n "$token" ]] && headers+=( -H "Authorization: Bearer $token" )

  local response status_code payload curl_status
  if ! response=$(curl -sS -w "\n%{http_code}" -X "$method" "${BASE_URL}${path}" "${headers[@]}" ${body:+-d "$body"}); then
    curl_status=$?
    info "[ERROR] [$actor] curl failed with exit code $curl_status. Check BASE_URL ($BASE_URL)."
    exit 1
  fi

  status_code=$(echo "$response" | tail -n1)
  payload=$(echo "$response" | sed '$d')

  if [[ "$status_code" == "000" ]]; then
    info "[ERROR] [$actor] Unable to reach API (status 000). Ensure backend is running at $BASE_URL."
    exit 1
  fi

  info "Status: $status_code"
  info "Response: $payload"

  local matched=false
  for code in "${expected_statuses[@]}"; do
    if [[ "$status_code" == "$code" ]]; then
      matched=true
      break
    fi
  done

  [[ "$matched" == true ]] || fail "$actor" "$status_code" "$expected_raw" "$path" "$payload"

  LAST_STATUS_CODE="$status_code"
  LAST_PAYLOAD="$payload"
}

extract_or_fail() {
  local actor="$1" json="$2" jq_expr="$3" label="$4"
  local value
  if ! value=$(echo "$json" | jq -r "$jq_expr // empty"); then
    info "[ERROR] [$actor] Response is not valid JSON while extracting $label"
    info "$json"
    exit 1
  fi
  if [[ -z "$value" || "$value" == "null" ]]; then
    info "[ERROR] [$actor] Cannot extract $label"
    info "$json"
    exit 1
  fi
  echo "$value"
}

title "CHRMS end-to-end API flow"
info "Base URL         : $BASE_URL"
info "Log file         : $LOG_FILE"
info "Admin account    : $ADMIN_EMAIL"
info "Patient auto-reg : $AUTO_REGISTER_PATIENT"
info "Patient account  : $PATIENT_EMAIL"
info "Doctor account   : $DOCTOR_EMAIL"
info "Hospital ID      : $HOSPITAL_ID"
info "Department ID    : $DEPARTMENT_ID"
info "Doctor ID        : $DOCTOR_ID"
info "Appointment      : $APPOINTMENT_DATE $APPOINTMENT_TIME"

# 0) Optional register patient
if [[ "$AUTO_REGISTER_PATIENT" == "true" ]]; then
  title "Register patient for email delivery (if missing)"
  register_body=$(cat <<JSON
{
  "email": "$PATIENT_EMAIL",
  "password": "$PATIENT_PASSWORD",
  "fullName": "$PATIENT_FULL_NAME",
  "role": "PATIENT",
  "dateOfBirth": "$PATIENT_DOB",
  "gender": "$PATIENT_GENDER"
}
JSON
  )

  call_api "Public" "POST" "/auth/register" "$register_body" "" "201,400,409"
  if [[ "$LAST_STATUS_CODE" == "201" ]]; then
    info "Registered new patient account for $PATIENT_EMAIL"
  else
    info "Patient registration skipped (status $LAST_STATUS_CODE) - assuming account already exists"
  fi
fi

# 1) Authenticate all roles
call_api "Admin" "POST" "/auth/login" "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASSWORD\"}" "" "200"
ADMIN_TOKEN=$(extract_or_fail "Admin" "$LAST_PAYLOAD" '.data.token' 'admin token')

call_api "Patient" "POST" "/auth/login" "{\"email\":\"$PATIENT_EMAIL\",\"password\":\"$PATIENT_PASSWORD\"}" "" "200"
PATIENT_TOKEN=$(extract_or_fail "Patient" "$LAST_PAYLOAD" '.data.token' 'patient token')
PATIENT_USER_ID=$(extract_or_fail "Patient" "$LAST_PAYLOAD" '.data.userId' 'patient user id')

call_api "Doctor" "POST" "/auth/login" "{\"email\":\"$DOCTOR_EMAIL\",\"password\":\"$DOCTOR_PASSWORD\"}" "" "200"
DOCTOR_TOKEN=$(extract_or_fail "Doctor" "$LAST_PAYLOAD" '.data.token' 'doctor token')

# 2) Admin checks catalog data
call_api "Admin" "GET" "/hospitals" "" "$ADMIN_TOKEN" "200"
call_api "Admin" "GET" "/doctors" "" "$ADMIN_TOKEN" "200"

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
call_api "Doctor" "POST" "/doctors/schedules" "$schedule_body" "$DOCTOR_TOKEN" "201"

# 4) Patient inspects available slots
call_api "Patient" "GET" "/doctors/${DOCTOR_ID}/available-slots?date=${APPOINTMENT_DATE}" "" "$PATIENT_TOKEN" "200"

# 5) Patient books appointment
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
call_api "Patient" "POST" "/patients/appointments" "$appointment_body" "$PATIENT_TOKEN" "201"
APPOINTMENT_ID=$(extract_or_fail "Patient" "$LAST_PAYLOAD" '.data.id' 'appointment id')
APPOINTMENT_PATIENT_ID=$(extract_or_fail "Patient" "$LAST_PAYLOAD" '.data.patientId // .data.patient.id' 'appointment patient id')

# 6) Patient reviews upcoming appointments
call_api "Patient" "GET" "/patients/appointments/upcoming" "" "$PATIENT_TOKEN" "200"

# 7) Payment flow
payment_body=$(cat <<JSON
{
  "appointmentId": $APPOINTMENT_ID,
  "paymentMethod": "CASH"
}
JSON
)
call_api "Patient" "POST" "/payments" "$payment_body" "$PATIENT_TOKEN" "201"
TRANSACTION_REF=$(extract_or_fail "Patient" "$LAST_PAYLOAD" '.data.transactionRef // .data.transactionReference' 'payment transactionRef')
call_api "Patient" "POST" "/payments/${TRANSACTION_REF}/complete" "" "$PATIENT_TOKEN" "200"

# 8) Doctor writes and approves medical record
record_body=$(cat <<JSON
{
  "appointmentId": $APPOINTMENT_ID,
  "symptoms": "DAU DAU SOT NHE",
  "diagnosis": "viem phe quan",
  "treatment": "nghi ngoi, uong nhieu nuoc, dung thuoc khang sinh",
  "notes": "theo doi nhiet do moi 6h"
}
JSON
)
call_api "Doctor" "POST" "/medical-records" "$record_body" "$DOCTOR_TOKEN" "201"
RECORD_ID=$(extract_or_fail "Doctor" "$LAST_PAYLOAD" '.data.id' 'medical record id')
call_api "Doctor" "POST" "/medical-records/${RECORD_ID}/approve" "" "$DOCTOR_TOKEN" "200"

# 9) Doctor issues prescription
prescription_body=$(cat <<JSON
{
  "medicalRecordId": $RECORD_ID,
  "items": [
    {"medicineId": 1, "dosage": "500mg", "frequency": "2 lan/ngay", "duration": "7 ngay", "quantity": 14, "instructions": "Uong sau khi an"},
    {"medicineId": 2, "dosage": "200mg", "frequency": "3 lan/ngay", "duration": "5 ngay", "quantity": 15, "instructions": "Uong truoc khi an"}
  ]
}
JSON
)
call_api "Doctor" "POST" "/prescriptions" "$prescription_body" "$DOCTOR_TOKEN" "201"

# 10) Chat + feedback
call_api "Patient" "POST" "/chat/appointments/${APPOINTMENT_ID}/messages" "{\"message\":\"$CHAT_MESSAGE\"}" "$PATIENT_TOKEN" "201"
call_api "Doctor" "GET" "/chat/appointments/${APPOINTMENT_ID}/messages/unread" "" "$DOCTOR_TOKEN" "200"

feedback_body=$(cat <<JSON
{
  "appointmentId": $APPOINTMENT_ID,
  "doctorId": $DOCTOR_ID,
  "rating": 5,
  "comment": "tu van tan tinh - script auto test"
}
JSON
)
call_api "Patient" "POST" "/feedback" "$feedback_body" "$PATIENT_TOKEN" "201"

# 11) Patient fetches medical records
call_api "Patient" "GET" "/medical-records/patient/${APPOINTMENT_PATIENT_ID}" "" "$PATIENT_TOKEN" "200"

# 12) Doctor schedules read-back
call_api "Doctor" "GET" "/doctors/${DOCTOR_ID}/schedules" "" "$DOCTOR_TOKEN" "200"

# 13) Admin filters
call_api "Admin" "GET" "/doctors/department/${DEPARTMENT_ID}" "" "$ADMIN_TOKEN" "200"
call_api "Admin" "GET" "/doctors/hospital/${HOSPITAL_ID}" "" "$ADMIN_TOKEN" "200"

# 14) Payments read-back by appointment
call_api "Patient" "GET" "/payments/appointment/${APPOINTMENT_ID}" "" "$PATIENT_TOKEN" "200"

# 15) Medical record read-back by id
call_api "Patient" "GET" "/medical-records/${RECORD_ID}" "" "$PATIENT_TOKEN" "200"

# 16) Prescription read-back by medical record
call_api "Patient" "GET" "/prescriptions/medical-record/${RECORD_ID}" "" "$PATIENT_TOKEN" "200"

# 17) Chat reply + polling
call_api "Doctor" "POST" "/chat/appointments/${APPOINTMENT_ID}/messages" "{\"message\":\"Da nhan duoc tin nhan, moi benh nhan toi kham.\"}" "$DOCTOR_TOKEN" "201"
LAST_MSG_TIME=$(echo "$LAST_PAYLOAD" | jq -r '.data.createdAt')
call_api "Patient" "GET" "/chat/appointments/${APPOINTMENT_ID}/messages?after=${LAST_MSG_TIME}" "" "$PATIENT_TOKEN" "200"

# 18) Feedback read-back
call_api "Patient" "GET" "/feedback/doctor/${DOCTOR_ID}" "" "$PATIENT_TOKEN" "200"
call_api "Patient" "GET" "/feedback/doctor/${DOCTOR_ID}/average-rating" "" "$PATIENT_TOKEN" "200"

# 19) Medical record files (multipart upload + list + download)
TMP_FILE="./artifacts/tmp_medical_record_note.txt"
mkdir -p "$(dirname "$TMP_FILE")"
echo "CHRMS auto-test file upload - $(date)" > "$TMP_FILE"

info ""
info "---- [Doctor] POST /medical-records/files/upload (multipart)"
upload_resp=$(curl -sS -w "\n%{http_code}" -X POST "${BASE_URL}/medical-records/files/upload" \
  -H "Authorization: Bearer $DOCTOR_TOKEN" \
  -F "medicalRecordId=${RECORD_ID}" \
  -F "fileType=OTHER" \
  -F "file=@${TMP_FILE}")

upload_status=$(echo "$upload_resp" | tail -n1)
upload_payload=$(echo "$upload_resp" | sed '$d')
info "Status: $upload_status"
info "Response: $upload_payload"
[[ "$upload_status" == "201" ]] || fail "Doctor" "$upload_status" "201" "/medical-records/files/upload" "$upload_payload"

FILE_ID=$(extract_or_fail "Doctor" "$upload_payload" '.data.id' 'uploaded file id')

call_api "Patient" "GET" "/medical-records/files/medical-record/${RECORD_ID}" "" "$PATIENT_TOKEN" "200"

info ""
info "---- [Patient] GET /medical-records/files/${FILE_ID}/download"
dl_status=$(curl -sS -o /dev/null -w "%{http_code}" -X GET "${BASE_URL}/medical-records/files/${FILE_ID}/download" \
  -H "Authorization: Bearer $PATIENT_TOKEN")
info "Status: $dl_status"
[[ "$dl_status" == "200" ]] || fail "Patient" "$dl_status" "200" "/medical-records/files/${FILE_ID}/download" "(binary body omitted)"

# 20) Appointment history
call_api "Patient" "GET" "/patients/appointments/history" "" "$PATIENT_TOKEN" "200"

info ""
info "[OK] Flow finished. See log: $LOG_FILE"
