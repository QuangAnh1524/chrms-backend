# CHRMS API Summary - All Endpoints

Base URL: `http://localhost:8080/api/v1`

## 📋 Complete API List (42 endpoint)

| # | Method & Path | Vai trò sử dụng | Body/Params bắt buộc | Trả về quan trọng |
| --- | --- | --- | --- | --- |
| 1 | `POST /auth/register` | PATIENT/DOCTOR/ADMIN | `{ email, password, role, fullName, phone? }` | token, userId, role |
| 2 | `POST /auth/login` | Tất cả | `{ email, password }` | token, userId, role |
| 3 | `POST /auth/logout` | Tất cả | Header `Authorization` | 200 OK (token bị blacklist nếu bật) |
| 4 | `POST /auth/refresh` | Tất cả | Header `Authorization` | token mới từ token còn hạn |
| 5 | `GET /hospitals` | PATIENT/DOCTOR/ADMIN | — | Danh sách bệnh viện |
| 6 | `GET /hospitals/{id}` | PATIENT/DOCTOR/ADMIN | Path: `id` | Chi tiết bệnh viện |
| 7 | `GET /doctors` | PATIENT/DOCTOR/ADMIN | — | Thông tin bác sĩ, chuyên khoa, viện, phí khám |
| 8 | `GET /doctors/{id}` | PATIENT/DOCTOR/ADMIN | Path: `id` | Chi tiết bác sĩ |
| 9 | `GET /doctors/hospital/{hospitalId}` | PATIENT/DOCTOR/ADMIN | Path: `hospitalId` | Lọc bác sĩ theo bệnh viện |
| 10 | `GET /doctors/department/{departmentId}` | PATIENT/DOCTOR/ADMIN | Path: `departmentId` | Lọc bác sĩ theo khoa |
| 11 | `POST /doctors/schedules` | DOCTOR | `{ doctorId, dayOfWeek (1-7), startTime (HH:mm:ss), endTime (HH:mm:ss), isAvailable }` | Tạo/ cập nhật ca làm việc |
| 12 | `GET /doctors/{doctorId}/schedules` | PATIENT/DOCTOR/ADMIN | Path: `doctorId` | Lịch đã khai báo |
| 13 | `GET /doctors/{doctorId}/available-slots?date=YYYY-MM-DD` | PATIENT/DOCTOR/ADMIN | Path: `doctorId`, Query: `date` | Slot trống theo ngày |
| 14 | `GET /patients/me` | PATIENT | — | Hồ sơ cá nhân từ JWT |
| 15 | `PATCH /patients/me` | PATIENT | `{ fullName?, phone?, dob?, gender?, address?, emergencyContact?, bloodType?, allergies? }` | Cập nhật hồ sơ |
| 16 | `POST /patients/appointments` | PATIENT | `{ doctorId, hospitalId, departmentId, appointmentDate, appointmentTime, notes? }` | `queueNumber`, `status=PENDING` |
| 17 | `GET /patients/appointments/upcoming` | PATIENT | — | Lịch hẹn tương lai |
| 18 | `GET /patients/appointments/history` | PATIENT | — | Lịch sử khám |
| 19 | `GET /appointments/{id}` | PATIENT/DOCTOR/ADMIN | Path: `id` | Chi tiết appointment |
| 20 | `POST /appointments/{id}/confirm` | DOCTOR/ADMIN | Path: `id` | Xác nhận từ PENDING |
| 21 | `POST /appointments/{id}/complete` | DOCTOR/ADMIN | Path: `id` | Đánh dấu COMPLETED |
| 22 | `POST /appointments/{id}/cancel` | PATIENT/DOCTOR/ADMIN | Path: `id`, body `{ reason? }` | Huỷ appointment |
| 23 | `POST /payments` | PATIENT/ADMIN | `{ appointmentId, paymentMethod, transactionRef?, returnUrl? }` | transactionRef, paymentUrl |
| 24 | `GET /payments/appointment/{appointmentId}` | PATIENT/ADMIN | Path: `appointmentId` | Danh sách giao dịch của appointment |
| 25 | `POST /payments/{transactionRef}/complete` | PATIENT/ADMIN | Path: `transactionRef` | Đánh dấu thanh toán COMPLETED |
| 26 | `POST /medical-records` | DOCTOR | `{ appointmentId, symptoms?, diagnosis?, treatment?, notes? }` | `status=DRAFT` |
| 27 | `POST /medical-records/{id}/approve` | DOCTOR | Path: `id` | `status=APPROVED` |
| 28 | `PATCH /medical-records/{id}` | DOCTOR | `{ symptoms?, diagnosis?, treatment?, notes? }` | Chỉ khi DRAFT |
| 29 | `GET /medical-records/patient/{patientId}` | PATIENT/DOCTOR/ADMIN | Path: `patientId` | Toàn bộ hồ sơ của bệnh nhân |
| 30 | `GET /medical-records/{id}` | PATIENT/DOCTOR/ADMIN | Path: `id` | Chi tiết hồ sơ |
| 31 | `POST /medical-records/files/upload` | DOCTOR/ADMIN | multipart: `medicalRecordId`, `file`, `fileType` | Lưu metadata file |
| 32 | `GET /medical-records/files/medical-record/{medicalRecordId}` | PATIENT/DOCTOR/ADMIN | Path: `medicalRecordId` | Danh sách file |
| 33 | `GET /medical-records/files/{id}/download` | PATIENT/DOCTOR/ADMIN | Path: `id` | Tải file |
| 34 | `POST /prescriptions` | DOCTOR | `{ medicalRecordId, items:[{ medicineId, dosage, frequency, duration, quantity, instructions? }] }` | Đơn thuốc + item |
| 35 | `GET /prescriptions/medical-record/{medicalRecordId}` | PATIENT/DOCTOR/ADMIN | Path: `medicalRecordId` | Đơn thuốc theo hồ sơ |
| 36 | `POST /chat/appointments/{appointmentId}/messages` | PATIENT/DOCTOR/ADMIN | Path: `appointmentId`, Body `{ message }` | Tin nhắn gắn userId |
| 37 | `GET /chat/appointments/{appointmentId}/messages?after={datetime}` | PATIENT/DOCTOR/ADMIN | Query: `after`? | Polling (có cache 50 tin) |
| 38 | `GET /chat/appointments/{appointmentId}/messages/unread` | PATIENT/DOCTOR/ADMIN | — | Tin nhắn chưa đọc theo user |
| 39 | `POST /chat/appointments/{appointmentId}/messages/read` | PATIENT/DOCTOR/ADMIN | `{ upToMessageId? | upToDatetime? }` | Đánh dấu đã đọc |
| 40 | `POST /feedback` | PATIENT | `{ appointmentId, rating (1-5), comment? }` | Feedback đã lưu |
| 41 | `GET /feedback/doctor/{doctorId}` | PATIENT/DOCTOR/ADMIN | Path: `doctorId` | Danh sách feedback |
| 42 | `GET /feedback/doctor/{doctorId}/average-rating` | PATIENT/DOCTOR/ADMIN | Path: `doctorId` | Điểm trung bình (cache 10 phút) |

---

## 🎯 Quick Test Scenarios

### Scenario 1: Patient Books Appointment (đầy đủ request)
1. **Login** → `POST /auth/login` lấy token.
2. **Chọn bác sĩ & slot** → `GET /doctors/{doctorId}/available-slots?date=YYYY-MM-DD`.
3. **Book appointment** → `POST /patients/appointments` với body mẫu:
   ```json
   {"doctorId":1,"hospitalId":1,"departmentId":1,"appointmentDate":"2025-12-10","appointmentTime":"09:00","notes":"Ho khan"}
   ```
4. **Tạo payment** → `POST /payments` `{ "appointmentId": <id>, "paymentMethod": "VNPAY" }`.
5. **Hoàn tất** → `POST /payments/{transactionRef}/complete` để chuyển `paymentStatus=COMPLETED`.
6. **Theo dõi** → `GET /patients/appointments/upcoming` hoặc `GET /payments/appointment/{appointmentId}` để kiểm tra trạng thái.

### Scenario 2: Doctor Creates Record (sau khi có appointment)
1. **Login bác sĩ** → token doctor.
2. **Khai báo lịch** → `POST /doctors/schedules` (ví dụ `{ "doctorId":1, "dayOfWeek":2, "startTime":"08:00:00", "endTime":"11:30:00" }`).
3. **Tạo hồ sơ** → `POST /medical-records` `{ "appointmentId": <id>, "diagnosis": "Viêm họng", "notes": "uống nước ấm" }` → `status=DRAFT`.
4. **Upload file** → multipart `medicalRecordId=<id>`, `file=@scan.pdf`, `fileType=LAB_RESULT`.
5. **Duyệt hồ sơ** → `POST /medical-records/{id}/approve` → `status=APPROVED` (không sửa thêm).
6. **Kê đơn** → `POST /prescriptions` `{ "medicalRecordId":<id>, "medicines":[{"medicineId":1,"dosage":"2 viên/ngày","quantity":10}] }`.

### Scenario 3: Chat Conversation
1. Patient gửi tin → `POST /chat/appointments/{id}/messages` `{ "message": "Bác sĩ ơi tôi còn ho" }`.
2. Doctor phản hồi → `POST /chat/appointments/{id}/messages` `{ "message": "Bạn nhớ uống thuốc" }`.
3. Poll tin mới → `GET /chat/appointments/{id}/messages?after=2025-12-03T10:00:00` mỗi 10 giây.
4. Lấy tin chưa đọc (cho doctor) → `GET /chat/appointments/{id}/messages/unread`.

### Scenario 4: Patient Feedback
1. Sau khám → `POST /feedback` `{ "appointmentId": <id>, "rating": 5, "comment": "Bác sĩ tận tình" }`.
2. FE hiển thị → `GET /feedback/doctor/{doctorId}` + `GET /feedback/doctor/{doctorId}/average-rating`.

---

## 📝 Important Notes

1. **JWT Token**: Required cho tất cả endpoint (trừ `/auth/register`, `/auth/login`).
   - Header: `Authorization: Bearer {token}`.
   - Token hiện được cache/blacklist qua Redis nếu logout.

2. **Thông tin bệnh nhân khi đăng ký**: Với role PATIENT, các trường ngày sinh/giới tính/địa chỉ/liên hệ khẩn cấp/nhóm máu/dị ứng có thể bỏ trống; bổ sung sau khi hoàn thiện hồ sơ.

3. **File Upload**: Use `multipart/form-data` with fields:
   - `medicalRecordId` (number)
   - `file` (file)
   - `fileType` (enum: XRAY, LAB_RESULT, SCAN, OTHER)

4. **Polling**: For chat messages, poll every 10 seconds:
   - Use `after` parameter to get only new messages
   - Or use `/unread` endpoint

5. **Date/Time Formats**:
   - Date: `YYYY-MM-DD` (ví dụ `2025-12-10`)
   - Time: `HH:mm` (ví dụ `09:00`)
   - DateTime: `YYYY-MM-DDTHH:mm:ss` (ví dụ `2025-12-03T10:00:00`)

6. **Enums**:
   - `role`: PATIENT, DOCTOR, ADMIN
   - `gender`: MALE, FEMALE, OTHER
   - `fileType`: XRAY, LAB_RESULT, SCAN, OTHER
   - `paymentMethod`: VNPAY, CASH, CARD
   - `paymentStatus`: PENDING, COMPLETED, FAILED
   - `recordStatus`: DRAFT, PENDING, APPROVED, SHARED
   - `appointmentStatus`: PENDING, CONFIRMED, COMPLETED, CANCELLED
7. **Email thông báo**: khi bệnh nhân đặt lịch thành công và có email, backend gửi mail xác nhận thật qua `JavaMailSender` (From mặc định lấy `spring.mail.username`, cần cấu hình SMTP hợp lệ; không có endpoint thủ công để trigger gửi mail).
8. **Thanh toán**: `POST /payments` tạo giao dịch trạng thái `PENDING` với số tiền mặc định `500000` VND; nếu paymentMethod khác `CASH`, hệ thống gọi `PaymentGatewayClient` để sinh `transactionRef`/`paymentUrl` rồi mới lưu.

---

## 🔄 Complete Workflow Example

```
1. Patient Registration
   POST /auth/register (Patient)
   → Save token

2. Doctor Registration  
   POST /auth/register (Doctor)
   → Save token

3. Doctor Setup Schedule
   POST /doctors/schedules (as Doctor)
   → Create working hours

4. Patient Books Appointment
   GET /doctors/{id}/available-slots?date=2025-12-10
   POST /patients/appointments
   → Appointment created

5. Payment
   POST /payments
   POST /payments/{ref}/complete
   → Payment completed

6. Doctor Creates Record
   POST /medical-records
   POST /medical-records/files/upload
   POST /medical-records/{id}/approve
   → Record approved

7. Prescription
   POST /prescriptions
   → Medicines prescribed

8. Chat
   POST /chat/appointments/{id}/messages (Patient)
   POST /chat/appointments/{id}/messages (Doctor)
   GET /chat/appointments/{id}/messages?after={time} (Polling)
   → Conversation ongoing

9. Feedback
   POST /feedback (Patient)
   GET /feedback/doctor/{id}/average-rating
   → Rating submitted
```

---

## 🐛 Common Issues

1. **401 Unauthorized**: Token expired or missing
   - Solution: Login again to get new token

2. **404 Not Found**: Entity doesn't exist
   - Solution: Check IDs in request

3. **400 Bad Request**: Validation error
   - Solution: Check required fields and formats

4. **409 Conflict**: Business rule violation
   - Solution: Check business logic (e.g., appointment already exists)

---

**Total: 42 API Endpoints** ✅

