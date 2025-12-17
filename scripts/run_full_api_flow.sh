#!/usr/bin/env bash
set -euo pipefail

BASE_URL=${BASE_URL:-"http://localhost:8080/api/v1"}
LOG_FILE=${LOG_FILE:-"./api-test-run-$(date +%Y%m%d-%H%M%S).txt"}

# Default credentials for seeded demo accounts (see V2__seed_data.sql)
PATIENT_EMAIL=${PATIENT_EMAIL:-"patient312@test.com"}
PATIENT_PASSWORD=${PATIENT_PASSWORD:-"password123"}
DOCTOR_EMAIL=${DOCTOR_EMAIL:-"doctor1@test.com"}
DOCTOR_PASSWORD=${DOCTOR_PASSWORD:-"password123"}

# Default resource selections
HOSPITAL_ID=${HOSPITAL_ID:-1}
DOCTOR_ID=${DOCTOR_ID:-1}
APPOINTMENT_DATE=${APPOINTMENT_DATE:-$(date -d "+1 day" +%Y-%m-%d)}
APPOINTMENT_TIME=${APPOINTMENT_TIME:-"09:00:00"}
CHAT_MESSAGE=${CHAT_MESSAGE:-"Xin chào bác sĩ"}

usage() {
  cat <<USAGE
Usage: LOG_FILE=./output.txt BASE_URL=http://localhost:8080/api/v1 \
       PATIENT_EMAIL=patient1@test.com PATIENT_PASSWORD=password123 \
       DOCTOR_EMAIL=doctor1@test.com DOCTOR_PASSWORD=password123 \
       bash scripts/run_full_api_flow.sh

Environment overrides:
  BASE_URL           API base URL (default: http://localhost:8080/api/v1)
  LOG_FILE           Where to write the consolidated log (default: ./api-test-run-<timestamp>.txt)
  PATIENT_EMAIL      Email for a PATIENT account (default: patient1@test.com)
  PATIENT_PASSWORD   Password for the patient account (default: password123)
  DOCTOR_EMAIL       Email for a DOCTOR account (default: doctor1@test.com)
  DOCTOR_PASSWORD    Password for the doctor account (default: password123)
  HOSPITAL_ID        Hospital ID to target (default: 1)
  DOCTOR_ID          Doctor ID to target (default: 1)
  APPOINTMENT_DATE   Appointment date (YYYY-MM-DD, default: tomorrow)
  APPOINTMENT_TIME   Appointment time (HH:MM:SS, default: 09:00:00)
  CHAT_MESSAGE       Sample chat content (default: "Xin chào bác sĩ")
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

title_line() {
  local title="$1"
  echo "============================================================" | tee -a "$LOG_FILE"
  echo "$title" | tee -a "$LOG_FILE"
  echo "============================================================" | tee -a "$LOG_FILE"
}

log_step() {
  local actor="$1"; shift
  local description="$*"
  echo "\n---- [$actor] $description" | tee -a "$LOG_FILE"
}

call_api() {
  local actor="$1" method="$2" path="$3" body="$4" token="${5:-}"

  log_step "$actor" "$method $path"
  if [[ -n "$body" ]]; then
    echo "Request body: $body" | tee -a "$LOG_FILE"
  fi

  local headers=(-H "Content-Type: application/json")
  if [[ -n "$token" ]]; then
    headers+=( -H "Authorization: Bearer $token" )
  fi

  local response
  response=$(curl -s -w "\n%{http_code}" -X "$method" "${BASE_URL}${path}" "${headers[@]}" -d "$body")

  local status_code body_only
  status_code=$(echo "$response" | tail -n 1)
  body_only=$(echo "$response" | sed '$d')

  echo "Status: $status_code" | tee -a "$LOG_FILE"
  echo "Response: $body_only" | tee -a "$LOG_FILE"

  echo "$body_only"
}

mkdir -p "$(dirname "$LOG_FILE")"
: > "$LOG_FILE"

title_line "CHRMS end-to-end API flow"
echo "Base URL         : $BASE_URL" | tee -a "$LOG_FILE"
echo "Log file         : $LOG_FILE" | tee -a "$LOG_FILE"
echo "Patient account  : $PATIENT_EMAIL" | tee -a "$LOG_FILE"
echo "Doctor account   : $DOCTOR_EMAIL" | tee -a "$LOG_FILE"
echo "Hospital ID      : $HOSPITAL_ID" | tee -a "$LOG_FILE"
echo "Doctor ID        : $DOCTOR_ID" | tee -a "$LOG_FILE"
echo "Appointment      : $APPOINTMENT_DATE $APPOINTMENT_TIME" | tee -a "$LOG_FILE"

# 1) Authenticate patient
patient_login=$(call_api "Patient" "POST" "/auth/login" "{\"email\":\"$PATIENT_EMAIL\",\"password\":\"$PATIENT_PASSWORD\"}" )
PATIENT_TOKEN=$(echo "$patient_login" | jq -r '.data.token // empty')
PATIENT_ID=$(echo "$patient_login" | jq -r '.data.userId // empty')

# 2) Authenticate doctor
doctor_login=$(call_api "Doctor" "POST" "/auth/login" "{\"email\":\"$DOCTOR_EMAIL\",\"password\":\"$DOCTOR_PASSWORD\"}")
DOCTOR_TOKEN=$(echo "$doctor_login" | jq -r '.data.token // empty')

# 3) Get hospitals & doctors lists
call_api "Patient" "GET" "/hospitals" "" "$PATIENT_TOKEN" >/dev/null
call_api "Patient" "GET" "/doctors" "" "$PATIENT_TOKEN" >/dev/null

# 4) Doctor creates/updates a schedule for the appointment weekday
DAY_OF_WEEK=$(date -d "$APPOINTMENT_DATE" +%u)
schedule_body=$(cat <<JSON
{
  "doctorId": $DOCTOR_ID,
  "dayOfWeek": $DAY_OF_WEEK,
  "startTime": "${APPOINTMENT_TIME}",
  "endTime": "$(date -d "${APPOINTMENT_TIME} 1 hour" +%H:%M:%S)",
  "isAvailable": true
}
JSON
)
call_api "Doctor" "POST" "/doctors/schedules" "$schedule_body" "$DOCTOR_TOKEN" >/dev/null

# 5) Check available slots for that date
call_api "Patient" "GET" "/doctors/${DOCTOR_ID}/available-slots?date=${APPOINTMENT_DATE}" "" "$PATIENT_TOKEN" >/dev/null

# 6) Patient books an appointment
appointment_body=$(cat <<JSON
{
  "doctorId": $DOCTOR_ID,
  "hospitalId": $HOSPITAL_ID,
  "appointmentDate": "${APPOINTMENT_DATE}",
  "appointmentTime": "${APPOINTMENT_TIME}",
  "symptoms": "Đau đầu, sốt nhẹ",
  "notes": "Scripted end-to-end test"
}
JSON
)
appointment_resp=$(call_api "Patient" "POST" "/patients/appointments" "$appointment_body" "$PATIENT_TOKEN")
APPOINTMENT_ID=$(echo "$appointment_resp" | jq -r '.data.id // empty')

# 7) Create & complete a payment transaction for the appointment
payment_body=$(cat <<JSON
{
  "appointmentId": $APPOINTMENT_ID,
  "paymentMethod": "CASH"
}
JSON
)
payment_resp=$(call_api "Patient" "POST" "/payments" "$payment_body" "$PATIENT_TOKEN")
TRANSACTION_REF=$(echo "$payment_resp" | jq -r '.data.transactionRef // .data.transactionReference // empty')

if [[ -n "$TRANSACTION_REF" ]]; then
  call_api "Patient" "POST" "/payments/${TRANSACTION_REF}/complete" "" "$PATIENT_TOKEN" >/dev/null
fi

# 8) Doctor creates a medical record for the appointment
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
record_resp=$(call_api "Doctor" "POST" "/medical-records" "$record_body" "$DOCTOR_TOKEN")
RECORD_ID=$(echo "$record_resp" | jq -r '.data.id // empty')

# 9) Approve the medical record
call_api "Doctor" "POST" "/medical-records/${RECORD_ID}/approve" "" "$DOCTOR_TOKEN" >/dev/null

# 10) Issue a prescription for two seeded medicines
prescription_body=$(cat <<JSON
{
  "medicalRecordId": $RECORD_ID,
  "items": [
    {
      "medicineId": 1,
      "dosage": "500mg",
      "frequency": "2 lần/ngày",
      "duration": "7 ngày",
      "quantity": 14,
      "instructions": "Uống sau khi ăn"
    },
    {
      "medicineId": 2,
      "dosage": "200mg",
      "frequency": "3 lần/ngày",
      "duration": "5 ngày",
      "quantity": 15,
      "instructions": "Uống trước khi ăn"
    }
  ]
}
JSON
)
call_api "Doctor" "POST" "/prescriptions" "$prescription_body" "$DOCTOR_TOKEN" >/dev/null

# 11) Patient sends a chat message
after_chat_resp=$(call_api "Patient" "POST" "/chat/appointments/${APPOINTMENT_ID}/messages" "{\"content\":\"$CHAT_MESSAGE\"}" "$PATIENT_TOKEN")

# 12) Fetch unread chat messages as the doctor
call_api "Doctor" "GET" "/chat/appointments/${APPOINTMENT_ID}/messages/unread" "" "$DOCTOR_TOKEN" >/dev/null

# 13) Patient submits feedback for the doctor
feedback_body=$(cat <<JSON
{
  "doctorId": $DOCTOR_ID,
  "rating": 5,
  "comment": "Tư vấn tận tình - kịch bản auto test"
}
JSON
)
call_api "Patient" "POST" "/feedback" "$feedback_body" "$PATIENT_TOKEN" >/dev/null

# 14) Retrieve latest medical records for the patient
if [[ -n "$PATIENT_ID" ]]; then
  call_api "Patient" "GET" "/medical-records/patient/${PATIENT_ID}" "" "$PATIENT_TOKEN" >/dev/null
fi

log_step "Summary" "Flow finished"
echo "Artifacts saved to: $LOG_FILE" | tee -a "$LOG_FILE"

