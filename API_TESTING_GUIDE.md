# Hướng dẫn test API (đầy đủ 42 endpoint)

- Base URL: `http://localhost:8080/api/v1`
- Header chung (sau khi đăng nhập): `Authorization: Bearer <jwt>`.
- Tài khoản seed: patient1@test.com / password123, doctor1@test.com / password123, admin@chrms.vn / password123.
- Dùng Postman/cURL đều được; bảng dưới đây mô tả mục đích, input JSON và kỳ vọng nhanh cho từng endpoint.

## 1. Auth
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 1 | POST `/auth/register` | Đăng ký tài khoản mới | — | `{ "email": "new@test.com", "password": "pass123", "fullName": "New User", "role": "PATIENT" }` | 201, trả token + userId + role |
| 2 | POST `/auth/login` | Lấy JWT | — | `{ "email": "patient1@test.com", "password": "password123" }` | 200, `token` trong response |
| 3 | POST `/auth/logout` | Đăng xuất/blacklist token | Bearer token hiện tại | — | 200, message "Logout successful" |
| 4 | POST `/auth/refresh` | Đổi token mới khi còn hạn | Bearer token hiện tại | — | 200, token mới + user info |

## 2. Danh mục bệnh viện/bác sĩ
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 5 | GET `/hospitals` | Lấy tất cả bệnh viện | Bearer | — | 200, danh sách id/name/address/phone/type |
| 6 | GET `/hospitals/{id}` | Chi tiết bệnh viện | Bearer | — | 200, thông tin theo id |
| 7 | GET `/doctors` | Lấy toàn bộ bác sĩ | Bearer | — | 200, danh sách bác sĩ (có consultationFee, specialty) |
| 8 | GET `/doctors/{id}` | Chi tiết bác sĩ | Bearer | — | 200, trả doctor + thông tin user |
| 9 | GET `/doctors/hospital/{hospitalId}` | Lọc bác sĩ theo bệnh viện | Bearer | — | 200, danh sách lọc |
| 10 | GET `/doctors/department/{departmentId}` | Lọc bác sĩ theo khoa | Bearer | — | 200, danh sách lọc |

## 3. Lịch làm việc bác sĩ
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 11 | POST `/doctors/schedules` | Tạo ca làm việc | Bearer (doctor) | `{ "doctorId": 1, "dayOfWeek": 2, "startTime": "08:00:00", "endTime": "11:30:00", "isAvailable": true }` | 201, trả schedule id |
| 12 | GET `/doctors/{doctorId}/schedules` | Xem lịch làm việc đã khai báo | Bearer | — | 200, danh sách schedule |
| 13 | GET `/doctors/{doctorId}/available-slots?date=2025-12-10` | Tính slot trống theo ngày | Bearer | — | 200, mảng time slot có `start`/`end` |

## 4. Hồ sơ bệnh nhân
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 14 | GET `/patients/me` | Lấy hồ sơ cá nhân | Bearer (patient) | — | 200, thông tin profile |
| 15 | PATCH `/patients/me` | Cập nhật hồ sơ | Bearer (patient) | `{ "fullName": "Updated Name", "phone": "0900000000", "dob": "1990-01-01" }` | 200, profile mới |

## 5. Appointment
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 16 | POST `/patients/appointments` | Bệnh nhân đặt lịch | Bearer (patient) | `{ "doctorId": 1, "hospitalId": 1, "departmentId": 1, "appointmentDate": "2025-12-10", "appointmentTime": "09:00", "notes": "Ho nhẹ" }` | 201, có queueNumber, status=PENDING |
| 17 | GET `/patients/appointments/upcoming` | Lịch hẹn sắp tới | Bearer (patient) | — | 200, danh sách upcoming |
| 18 | GET `/patients/appointments/history` | Lịch sử khám | Bearer (patient) | — | 200, danh sách history |
| 19 | GET `/appointments/{id}` | Chi tiết 1 lịch hẹn | Bearer | — | 200, appointment detail |
| 20 | POST `/appointments/{id}/confirm` | Bác sĩ/Admin xác nhận | Bearer (doctor/admin) | — | 200, status=CONFIRMED |
| 21 | POST `/appointments/{id}/complete` | Hoàn tất khám | Bearer (doctor/admin) | — | 200, status=COMPLETED |
| 22 | POST `/appointments/{id}/cancel` | Huỷ lịch | Bearer (patient/doctor/admin) | `{ "reason": "Bận đột xuất" }` | 200, status=CANCELLED |

## 6. Thanh toán
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 23 | POST `/payments` | Tạo giao dịch | Bearer (patient/admin) | `{ "appointmentId": 1, "paymentMethod": "VNPAY", "transactionRef": "TEST-001", "returnUrl": "https://example.com" }` | 201, trả transactionRef, paymentUrl (nếu VNPAY/CARD) |
| 24 | GET `/payments/appointment/{appointmentId}` | Xem giao dịch của lịch hẹn | Bearer (patient/admin) | — | 200, danh sách transaction |
| 25 | POST `/payments/{transactionRef}/complete` | Đánh dấu thanh toán hoàn tất | Bearer (patient/admin) | — | 200, status=COMPLETED |

## 7. Hồ sơ y tế
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 26 | POST `/medical-records` | Tạo hồ sơ (doctor) | Bearer (doctor) | `{ "appointmentId": 1, "symptoms": "Sốt", "diagnosis": "Cảm cúm", "treatment": "Uống thuốc", "notes": "Nghỉ ngơi" }` | 201, status=DRAFT |
| 27 | POST `/medical-records/{id}/approve` | Duyệt hồ sơ | Bearer (doctor) | — | 200, status=APPROVED |
| 28 | PATCH `/medical-records/{id}` | Sửa hồ sơ khi còn DRAFT | Bearer (doctor) | `{ "diagnosis": "Viêm họng", "treatment": "Kháng sinh" }` | 200, bản cập nhật |
| 29 | GET `/medical-records/patient/{patientId}` | Xem hồ sơ của bệnh nhân | Bearer (patient/doctor/admin) | — | 200, danh sách lọc theo quyền truy cập |
| 30 | GET `/medical-records/{id}` | Xem chi tiết hồ sơ | Bearer (patient/doctor/admin) | — | 200, thông tin hồ sơ |

## 8. File hồ sơ y tế
| # | Endpoint | Mục đích | Header | Body (multipart) | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 31 | POST `/medical-records/files/upload` | Upload file cận lâm sàng | Bearer (doctor/admin) | `medicalRecordId=<id>`, `file=@scan.pdf`, `fileType=LAB_RESULT` | 201, metadata file lưu DB |
| 32 | GET `/medical-records/files/medical-record/{medicalRecordId}` | Danh sách file đính kèm | Bearer | — | 200, danh sách file |
| 33 | GET `/medical-records/files/{id}/download` | Tải file | Bearer | — | 200, file stream với header Content-Disposition |

## 9. Đơn thuốc
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 34 | POST `/prescriptions` | Tạo đơn thuốc | Bearer (doctor) | `{ "medicalRecordId": 1, "items": [{ "medicineId": 1, "dosage": "2 viên", "frequency": "2 lần/ngày", "duration": "5 ngày", "quantity": 10, "instructions": "Sau ăn" }] }` | 201, trả danh sách thuốc đã lưu |
| 35 | GET `/prescriptions/medical-record/{medicalRecordId}` | Lấy đơn theo hồ sơ | Bearer | — | 200, prescription id/medicalRecordId |

## 10. Chat theo lịch hẹn
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 36 | POST `/chat/appointments/{appointmentId}/messages` | Gửi tin nhắn | Bearer | `{ "message": "Bác sĩ ơi..." }` | 201, tin nhắn với senderId |
| 37 | GET `/chat/appointments/{appointmentId}/messages?after=2025-12-10T10:00:00` | Poll lịch sử chat | Bearer | — | 200, danh sách tin (có lọc `after`) |
| 38 | GET `/chat/appointments/{appointmentId}/messages/unread` | Lấy tin chưa đọc | Bearer | — | 200, chỉ những tin chưa đọc của user hiện tại |
| 39 | POST `/chat/appointments/{appointmentId}/messages/read` | Đánh dấu đã đọc | Bearer | `{ "upToMessageId": 15 }` **hoặc** `{ "upToDatetime": "2025-12-10T11:00:00" }` | 200, message "Messages marked as read" |

## 11. Feedback bác sĩ
| # | Endpoint | Mục đích | Header | Body JSON mẫu | Kỳ vọng |
| --- | --- | --- | --- | --- | --- |
| 40 | POST `/feedback` | Gửi đánh giá sau khám | Bearer (patient) | `{ "appointmentId": 1, "rating": 5, "comment": "Tận tâm" }` | 201, lưu feedback |
| 41 | GET `/feedback/doctor/{doctorId}` | Danh sách feedback theo bác sĩ | Bearer | — | 200, list feedback |
| 42 | GET `/feedback/doctor/{doctorId}/average-rating` | Điểm trung bình (có cache) | Bearer | — | 200, giá trị double |

## Mẹo test nhanh
- Luôn login trước, copy token vào các request kế tiếp.
- Thứ tự hợp lệ cho flow chính: đăng nhập → tạo schedule (doctor) → patient đặt lịch → tạo payment → complete payment → doctor tạo & duyệt hồ sơ → upload file → tạo đơn thuốc → chat → gửi feedback.
- Nếu gặp 403 khi truy cập hồ sơ, kiểm tra bạn đang dùng đúng vai trò (patient chỉ xem hồ sơ của chính mình; doctor chỉ xem hồ sơ do mình tạo).
