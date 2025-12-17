# CHRMS API Summary - All Endpoints

Base URL: `http://localhost:8080/api/v1`

## 📋 Complete API List

| # | Method & Path | Vai trò sử dụng | Body/Params bắt buộc | Trả về quan trọng |
| --- | --- | --- | --- | --- |
| 1 | `POST /auth/register` | PATIENT/DOCTOR/ADMIN | `{ email, password, role, fullName, phone? }` | token, userId, role |
| 2 | `POST /auth/login` | Tất cả | `{ email, password }` | token, userId, role |
| 3 | `POST /auth/logout` | Tất cả | Header `Authorization` | 200 OK (token bị blacklist nếu bật) |

### 🏥 Hospitals & Doctors (6 endpoints)
| # | Method & Path | Vai trò | Body/params | Ghi chú |
| --- | --- | --- | --- | --- |
| 4 | `GET /hospitals` | PATIENT/DOCTOR/ADMIN | — | Danh sách bệnh viện có id/name/address/phone/type |
| 5 | `GET /hospitals/{id}` | PATIENT/DOCTOR/ADMIN | Path: `id` | Chi tiết bệnh viện |
| 6 | `GET /doctors` | PATIENT/DOCTOR/ADMIN | Query: `page`, `size`? | Trả `consultationFee`, `experienceYears`, `departmentId`, `hospitalId` |
| 7 | `GET /doctors/{id}` | PATIENT/DOCTOR/ADMIN | Path: `id` | Chi tiết bác sĩ |
| 8 | `GET /doctors/department/{departmentId}` | PATIENT/DOCTOR/ADMIN | Path: `departmentId` | Lọc bác sĩ theo khoa |
| 9 | `GET /doctors/hospital/{hospitalId}` | PATIENT/DOCTOR/ADMIN | Path: `hospitalId` | Lọc bác sĩ theo bệnh viện |

### 📅 Doctor Schedules (3 endpoints)
| # | Method & Path | Vai trò | Body/params | Ghi chú |
| --- | --- | --- | --- | --- |
| 10 | `POST /doctors/schedules` | DOCTOR | `{ doctorId, dayOfWeek (1-7), startTime (HH:mm:ss), endTime (HH:mm:ss), isAvailable? }` | Tạo/ cập nhật ca làm việc |
| 11 | `GET /doctors/{doctorId}/schedules` | DOCTOR/ADMIN | Path: `doctorId` | Hiển thị lịch đã khai báo |
| 12 | `GET /doctors/{doctorId}/available-slots?date=YYYY-MM-DD` | PATIENT | Path: `doctorId`, Query: `date` | Tính slot trống trong ngày; cần cho đặt lịch |

### 📋 Appointments (3 endpoints)
| # | Method & Path | Vai trò | Body/params | Trạng thái/ghi chú |
| --- | --- | --- | --- | --- |
| 13 | `POST /patients/appointments` | PATIENT | `{ doctorId, hospitalId, departmentId, appointmentDate (YYYY-MM-DD), appointmentTime (HH:mm), notes? }` | Trả `queueNumber`, `status=PENDING` |
| 14 | `GET /patients/appointments/upcoming` | PATIENT | — | Lịch hẹn trong tương lai của bệnh nhân (dựa trên token) |
| 15 | `GET /patients/appointments/history` | PATIENT | — | Lịch sử khám của bệnh nhân |

### 💊 Prescriptions (2 endpoints)
| # | Method & Path | Vai trò | Body/params | Ghi chú |
| --- | --- | --- | --- | --- |
| 16 | `POST /prescriptions` | DOCTOR | `{ medicalRecordId, medicines:[{ medicineId, dosage, quantity, instructions? }] }` | Liên kết hồ sơ bệnh án đã APPROVED |
| 17 | `GET /prescriptions/medical-record/{medicalRecordId}` | PATIENT/DOCTOR | Path: `medicalRecordId` | Lấy đơn thuốc theo hồ sơ |

### 📎 Medical Record Files (3 endpoints)
| # | Method & Path | Vai trò | Body/params | Định dạng |
| --- | --- | --- | --- | --- |
| 18 | `POST /medical-records/files/upload` | DOCTOR | multipart: `medicalRecordId`, `file`, `fileType` | fileType: XRAY/LAB_RESULT/SCAN/OTHER |
| 19 | `GET /medical-records/files/medical-record/{medicalRecordId}` | PATIENT/DOCTOR | Path: `medicalRecordId` | Danh sách file đính kèm |
| 20 | `GET /medical-records/files/{id}/download` | PATIENT/DOCTOR | Path: `id` | Tải file |

### 💳 Payments (3 endpoints)
| # | Method & Path | Vai trò | Body/params | Trạng thái |
| --- | --- | --- | --- | --- |
| 21 | `POST /payments` | PATIENT | `{ appointmentId, paymentMethod }` | Tạo transaction với `status=PENDING`, trả `transactionRef` |
| 22 | `GET /payments/appointment/{appointmentId}` | PATIENT/ADMIN | Path: `appointmentId` | Kiểm tra danh sách giao dịch của lịch hẹn |
| 23 | `POST /payments/{transactionRef}/complete` | PATIENT/ADMIN | Path: `transactionRef` | Đánh dấu thanh toán `COMPLETED` |

### 📝 Medical Records (4 endpoints)
| # | Method & Path | Vai trò | Body/params | Trạng thái |
| --- | --- | --- | --- | --- |
| 24 | `POST /medical-records` | DOCTOR | `{ appointmentId, diagnosis, notes }` | Khởi tạo hồ sơ, `status=DRAFT` |
| 25 | `POST /medical-records/{id}/approve` | DOCTOR | Path: `id` | Chốt hồ sơ, `status=APPROVED` |
| 26 | `GET /medical-records/patient/{patientId}` | PATIENT/DOCTOR | Path: `patientId` | Lấy tất cả hồ sơ của bệnh nhân |
| 27 | `GET /medical-records/{id}` | PATIENT/DOCTOR | Path: `id` | Chi tiết một hồ sơ |

### 💬 Chat Messages (3 endpoints - Polling-based)
| # | Method & Path | Vai trò | Body/params | Ghi chú |
| --- | --- | --- | --- | --- |
| 28 | `POST /chat/appointments/{appointmentId}/messages` | PATIENT/DOCTOR | Path: `appointmentId`, Body `{ message }` | Lưu tin nhắn gắn userId từ JWT |
| 29 | `GET /chat/appointments/{appointmentId}/messages?after={datetime}` | PATIENT/DOCTOR | Query: `after`? | Polling, trả tối đa 50 message cache |
| 30 | `GET /chat/appointments/{appointmentId}/messages/unread` | DOCTOR | — | Tin nhắn chưa đọc, phục vụ thông báo |

### ⭐ Feedback (3 endpoints)
| # | Method & Path | Vai trò | Body/params | Ghi chú |
| --- | --- | --- | --- | --- |
| 31 | `POST /feedback` | PATIENT | `{ appointmentId, rating (1-5), comment? }` | Chỉ cho phép sau khi khám hoàn tất |
| 32 | `GET /feedback/doctor/{doctorId}` | PATIENT/DOCTOR | Path: `doctorId` | Danh sách feedback theo thời gian |
| 33 | `GET /feedback/doctor/{doctorId}/average-rating` | PATIENT/DOCTOR | Path: `doctorId` | Cache trung bình rating 10 phút |

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
7. **Email thông báo**: khi bệnh nhân đặt lịch thành công và có email, backend gửi mail xác nhận với `JavaMailSender` qua `EmailService` (không có endpoint thủ công để trigger gửi mail).
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

**Total: 31 API Endpoints** ✅

