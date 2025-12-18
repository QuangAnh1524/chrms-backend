# Hướng dẫn luồng nghiệp vụ theo vai trò

Tài liệu này mô tả từng bước hành động điển hình cho ba vai trò chính và cách các API ghép lại thành luồng hoàn chỉnh.

## 1. Admin
- **Mục tiêu:** kiểm soát danh mục (bệnh viện/khoa/bác sĩ) và hỗ trợ xử lý lịch hẹn/thanh toán nếu cần.
- **Bước chính:**
  1. Đăng nhập `POST /auth/login` → lưu token.
  2. Tra cứu danh mục: `GET /hospitals`, `GET /doctors`, `GET /doctors/hospital/{id}`, `GET /doctors/department/{id}`.
  3. Giám sát appointment: `GET /appointments/{id}`; khi cần có thể `POST /appointments/{id}/confirm|complete|cancel`.
  4. Hỗ trợ thanh toán: `GET /payments/appointment/{appointmentId}`, `POST /payments/{transactionRef}/complete` khi cần chốt giao dịch.
  5. Kiểm tra hồ sơ/yêu cầu tải file: `GET /medical-records/patient/{patientId}`, `GET /medical-records/{id}`, download file qua `/medical-records/files/...`.
  6. Theo dõi phản hồi: `GET /feedback/doctor/{doctorId}` và `/average-rating`.

## 2. Doctor
- **Mục tiêu:** quản lý lịch làm việc, khám bệnh, cập nhật hồ sơ y tế, trò chuyện với bệnh nhân.
- **Bước chi tiết:**
  1. Đăng nhập `POST /auth/login` → token doctor.
  2. Khai báo ca làm việc: `POST /doctors/schedules` (ngày trong tuần + giờ bắt đầu/kết thúc). Kiểm tra lại bằng `GET /doctors/{doctorId}/schedules`.
  3. Tiếp nhận & quản lý appointment:
     - Xem chi tiết: `GET /appointments/{id}`.
     - Chuyển trạng thái: `POST /appointments/{id}/confirm` sau khi chấp nhận; `POST /appointments/{id}/complete` sau khi khám; nếu cần huỷ dùng `.../cancel`.
  4. Lập hồ sơ khám:
     - Tạo hồ sơ: `POST /medical-records` với chẩn đoán/triệu chứng.
     - Chỉnh sửa khi còn DRAFT: `PATCH /medical-records/{id}`.
     - Upload file cận lâm sàng: `POST /medical-records/files/upload`.
     - Duyệt hồ sơ: `POST /medical-records/{id}/approve` (khóa sửa).
     - Kê đơn thuốc: `POST /prescriptions` cho hồ sơ đã duyệt.
  5. Trao đổi với bệnh nhân: gửi/nhận tin qua `POST /chat/appointments/{id}/messages` và poll `GET /chat/appointments/{id}/messages` hoặc `.../unread`; đánh dấu đọc bằng `POST .../messages/read`.
  6. Theo dõi phản hồi: đọc rating qua `GET /feedback/doctor/{doctorId}` hoặc trung bình qua `/average-rating`.

## 3. Patient
- **Mục tiêu:** đặt lịch, thanh toán, xem hồ sơ và trao đổi với bác sĩ.
- **Bước chi tiết:**
  1. Đăng ký (nếu cần) `POST /auth/register` hoặc đăng nhập `POST /auth/login` → token patient.
  2. Cập nhật hồ sơ cá nhân nếu còn thiếu: `PATCH /patients/me`.
  3. Tìm bác sĩ phù hợp: dùng `GET /hospitals`, `GET /doctors`, hoặc lọc theo viện/khoa.
  4. Đặt lịch: `POST /patients/appointments` (chọn doctorId/hospitalId/departmentId + ngày/giờ). Xem lịch sắp tới `GET /patients/appointments/upcoming` hoặc lịch sử `GET /patients/appointments/history`.
  5. Thanh toán: `POST /payments` → nếu cần chốt `POST /payments/{transactionRef}/complete`; xem lại giao dịch bằng `GET /payments/appointment/{appointmentId}`.
  6. Xem hồ sơ khám và file đính kèm sau khi bác sĩ duyệt: `GET /medical-records/patient/{patientId}` hoặc `GET /medical-records/{id}`; tải file qua `/medical-records/files/...`.
  7. Nhận đơn thuốc: `GET /prescriptions/medical-record/{medicalRecordId}`.
  8. Chat với bác sĩ: `POST /chat/appointments/{id}/messages`, poll `GET /chat/appointments/{id}/messages` hoặc `.../unread`.
  9. Gửi feedback sau khám: `POST /feedback`; xem tổng quan rating bác sĩ `GET /feedback/doctor/{doctorId}`.

## 4. Luồng tổng hợp (từ đăng ký → khám → feedback)
1. Patient đăng ký/đăng nhập → lấy token.
2. Doctor khai báo lịch → patient xem slot rảnh (`GET /doctors/{doctorId}/available-slots`).
3. Patient đặt lịch (`POST /patients/appointments`) → nhận queueNumber.
4. Patient thanh toán (`POST /payments` + `POST /payments/{ref}/complete`).
5. Doctor xác nhận appointment → khám, tạo hồ sơ (`POST /medical-records`), upload file, duyệt hồ sơ, kê đơn (`POST /prescriptions`).
6. Hai bên chat trong suốt quá trình (`/chat/...`).
7. Patient xem hồ sơ/đơn thuốc, tải file và gửi feedback.
