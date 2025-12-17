# CHRMS Backend

Centralized Health Record Management System (MVP) cho nền tảng y tế số Hà Nội. Dịch vụ cung cấp REST API cho bệnh nhân, bác sĩ, quản trị viên để đặt lịch, khám bệnh, lưu trữ hồ sơ và thanh toán.

- **Demo nhanh:** chạy bằng Docker với dữ liệu seed (bệnh viện, khoa, thuốc, user mẫu).
- **Dành cho FE:** mục "API chính" và "Chuỗi workflow mẫu" tóm tắt endpoint, trường bắt buộc, giúp dựng màn hình nhanh.

## 🧩 Thành phần & công nghệ
- **Backend:** Spring Boot 3, Java 17, Maven.
- **Database:** PostgreSQL + Flyway migration/seed tự chạy khi khởi động.
- **Cache:** Redis (cache danh mục, token blacklist nếu cần).
- **Bảo mật:** Spring Security + JWT; phân quyền PATIENT/DOCTOR/ADMIN.
- **File:** lưu metadata qua REST (storage triển khai phụ thuộc môi trường: local/Docker volume).
- **Docs:** Swagger UI, Postman collection, bảng API tóm tắt.

## ✨ Khối chức năng chính
- **Quản lý người dùng & phân quyền:** đăng ký/đăng nhập JWT, phân vai trò; reset mật khẩu (qua flow email giả lập trong môi trường dev).
- **Đặt lịch khám:** bác sĩ tạo lịch làm việc, bệnh nhân xem slot trống, đặt lịch, quản trị viên kiểm soát dữ liệu danh mục (bệnh viện, khoa, bác sĩ).
- **Khám & hồ sơ bệnh án:** bác sĩ tạo hồ sơ, upload file cận lâm sàng, duyệt hồ sơ và phát hành đơn thuốc.
- **Thanh toán:** tạo giao dịch, hoàn tất thanh toán theo appointment (mô phỏng, không tích hợp cổng thật trong repo này).
- **Chat & phản hồi:** chat theo appointment, bệnh nhân gửi đánh giá bác sĩ.
- **Hạ tầng:** Clean Architecture; Flyway migration/seed; Redis cache; global exception handler và logging.

## 🗂 Cấu trúc dự án (Clean Architecture)
```
src/main/java/com/chrms/
├─ domain/                     # Entity, Value Object, DomainEvent, exception, port (repository/cache/email/file)
│  ├─ model/                   # Patient, Doctor, Appointment, Schedule, MedicalRecord, Prescription...
│  ├─ exception/               # DomainException, NotFoundException, BusinessValidationException...
│  └─ port/                    # Repository/Cache/Notifier abstractions (không phụ thuộc framework)
│
├─ usecase/                    # Application service (orchestrate logic, transaction boundary)
│  ├─ auth/                    # Register/Login, token refresh, password handling
│  ├─ patient/                 # Appointment, payment, feedback flow của bệnh nhân
│  ├─ doctor/                  # Lịch làm việc, medical record, prescription, chat
│  ├─ admin/                   # Quản trị danh mục bệnh viện/khoa/bác sĩ
│  └─ shared/                  # Base use case, mapper hỗ trợ nhiều module
│
├─ adapter/                    # Triển khai port (REST, persistence, cache, email, file)
│  ├─ in/web/                  # Controller + request/response DTO + validation
│  ├─ out/persistence/         # JPA entity, repository impl, mapper entity ↔ domain
│  ├─ out/cache/               # Redis cache adapter
│  ├─ out/notification/        # Email/SMS adapter (stub), token blacklisting nếu bật
│  └─ out/storage/             # Lưu file cận lâm sàng, phục vụ download
│
└─ infrastructure/             # Spring config, security, exception, util
   ├─ config/                  # Bean config, OpenAPI, WebConfig, Flyway
   ├─ security/                # JWT filter, authentication/authorization, password encoder
   └─ exception/               # GlobalExceptionHandler, error response schema

src/main/resources/
├─ application*.yml            # Profile: default, dev, docker (env override)
└─ db/migration/               # Script Flyway + seed data (V*__*.sql)
```
**Nguyên tắc:**
- Domain không phụ thuộc Spring/JPA; adapter chỉ giao tiếp qua port.
- Use case gọi port (repository/cache/notification) và trả domain model/DTO mapper; không gọi trực tiếp framework.
- Adapter in (REST) chỉ validate + mapping → gọi use case; adapter out triển khai repository/cache/notification.
- Infrastructure chứa wiring/config/security; tránh đưa business logic vào đây.

## 📁 Cây thư mục repo
```
.
├─ src/                             # Mã nguồn Spring Boot
│  ├─ main/java/com/chrms           # Domain, usecase, adapter, infrastructure
│  └─ main/resources                # Cấu hình, Flyway migration, seed data
├─ scripts/                         # Script chạy full API flow, tiện ích CI/local
├─ docker-compose.yml               # Stack app + PostgreSQL + Redis
├─ Dockerfile                       # Build image ứng dụng
├─ QUICKSTART.md                    # Hướng dẫn chạy nhanh, cURL mẫu
├─ API_SUMMARY.md                   # Bảng chi tiết endpoint
├─ POSTMAN_API_EXAMPLES.md          # Hướng dẫn dùng Postman collection
├─ CHRMS_Postman_Collection.json    # Collection request mẫu
├─ ARCHITECTURE.md                  # Giải thích Clean Architecture
└─ README.md                        # Tài liệu tổng quan dự án (file này)
```

**Biến môi trường quan trọng** (có giá trị mẫu trong `application-docker.yml`):
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `SPRING_REDIS_HOST`, `SPRING_REDIS_PORT`
- `JWT_SECRET`, `JWT_EXPIRATION_MINUTES`
- `FILE_STORAGE_PATH` (thư mục mount trong Docker volume khi lưu file)

## 🚀 Khởi chạy nhanh
### Chạy full stack bằng Docker (khuyến nghị)
```bash
# Clone repo
$ git clone <your-repo>
$ cd chrms-backend

# Backend + PostgreSQL + Redis
$ docker-compose up -d

# Theo dõi log ứng dụng
$ docker-compose logs -f app
```
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- One-liner: `./start.sh` (bao `docker-compose up -d`).

### Chạy local (hot reload)
```bash
# Khởi động DB/cache
$ docker-compose up -d postgres redis

# Chạy Spring Boot profile dev
$ mvn spring-boot:run -Dspring.profiles.active=dev
```
Dùng IntelliJ IDEA để debug và chạy test.

### Dừng dịch vụ
```bash
$ docker-compose down        # dừng
$ docker-compose down -v     # dừng + xoá volume (reset DB)
```

## 🔐 Tài khoản mặc định
```
Patient:  patient1@test.com / password123
Doctor:   doctor1@test.com / password123
Admin:    admin@chrms.vn    / password123
```

## 📡 API chính (đủ cho FE dựng màn)
**Base URL:** `http://localhost:8080/api/v1`

**Quy ước chung**
- Auth: cần header `Authorization: Bearer <token>` cho mọi endpoint trừ `/auth/register` và `/auth/login`.
- Ngày/giờ: `YYYY-MM-DD` và `HH:mm:ss` (UTC+7 mặc định khi seed).
- Paging: `page` (bắt đầu từ 0), `size`; nhiều API trả `content`, `totalElements`, `totalPages`.
- Lỗi chuẩn: `{ "status": 400|401|403|404|409|500, "error": "<code>", "message": "<detail>" }` qua GlobalExceptionHandler.

| Nhóm | Endpoint | Mô tả nhanh | Body/params tối thiểu |
| --- | --- | --- | --- |
| Auth | POST `/auth/register` | Đăng ký user (role = PATIENT/DOCTOR/ADMIN) | `{ "email", "password", "role", "fullName" }` |
|  | POST `/auth/login` | Lấy JWT | `{ "email", "password" }` → trả token |
| Hospital/Doctor | GET `/hospitals` | Danh sách bệnh viện | — |
|  | GET `/doctors` | Danh sách bác sĩ | Query: `page`, `size` |
|  | GET `/doctors/department/{departmentId}` | Bác sĩ theo khoa | Path: `departmentId` |
|  | GET `/doctors/hospital/{hospitalId}` | Bác sĩ theo bệnh viện | Path: `hospitalId` |
| Schedule | POST `/doctors/schedules` | Bác sĩ tạo lịch làm việc | `{ "doctorId", "date" (YYYY-MM-DD), "startTime", "endTime" }` |
|  | GET `/doctors/{doctorId}/available-slots` | Slot trống cho đặt lịch | Query: `date=YYYY-MM-DD` |
| Appointment | POST `/patients/appointments` | Bệnh nhân đặt lịch | `{ "patientId", "doctorId", "scheduleId", "appointmentDate" }` |
| Payment | POST `/payments` | Tạo giao dịch | `{ "appointmentId", "paymentMethod" }` |
|  | POST `/payments/{transactionRef}/complete` | Hoàn tất giao dịch | Path: `transactionRef` |
| Medical Record | POST `/medical-records` | Bác sĩ tạo hồ sơ | `{ "appointmentId", "diagnosis", "notes" }` |
|  | POST `/medical-records/{id}/approve` | Duyệt hồ sơ | Path: `id` |
| File | POST `/medical-records/files/upload` | Upload file hồ sơ | multipart: `medicalRecordId`, `file`, `fileType` |
| Prescription | POST `/prescriptions` | Tạo đơn thuốc | `{ "medicalRecordId", "medicines"[] }` |
| Chat | POST `/chat/appointments/{appointmentId}/messages` | Gửi chat | `{ "senderId", "content" }` |
| Feedback | POST `/feedback` | Bệnh nhân gửi đánh giá | `{ "appointmentId", "rating", "comment" }` |

> Đầy đủ 29 endpoint: xem [API_SUMMARY.md](API_SUMMARY.md) hoặc Swagger UI.

### 🔄 Chuỗi workflow mẫu
1) **Bệnh nhân đặt lịch + thanh toán:** Login → lấy `available-slots` → `POST /patients/appointments` → `POST /payments` → `POST /payments/{ref}/complete`.
2) **Bác sĩ khám & ra đơn:** Login bác sĩ → `POST /doctors/schedules` → sau khi có appointment → `POST /medical-records` → upload file → `POST /medical-records/{id}/approve` → `POST /prescriptions`.
3) **Chat:** Hai phía gửi `POST /chat/appointments/{id}/messages`; FE poll `GET /chat/appointments/{id}/messages?after=<time>` hoặc `GET .../unread`.
4) **Feedback:** Patient sau khám → `POST /feedback` → hiển thị `GET /feedback/doctor/{doctorId}` và `.../average-rating`.

### 🎨 Gợi ý cho FE
- **Trang đặt lịch:** dùng `/hospitals`, `/doctors/department/{id}`, `/doctors/{doctorId}/available-slots`; submit `/patients/appointments`.
- **Trang lịch làm việc bác sĩ:** `/doctors/schedules` (POST) để tạo, `/doctors/{doctorId}/available-slots` để xem.
- **Trang hồ sơ khám:** tạo hồ sơ `/medical-records`, upload `/medical-records/files/upload`, duyệt `/medical-records/{id}/approve`, kê đơn `/prescriptions`.
- **Trang chat:** poll `/chat/appointments/{id}/messages?after=...` hoặc `/unread` và gửi `/chat/appointments/{id}/messages`.
- **Trang đánh giá:** gửi `/feedback`, show `/feedback/doctor/{doctorId}` + `/average-rating`.

## 🧪 Script luồng đầy đủ (không cần Postman)
`scripts/run_full_api_flow.sh` chạy trên Bash/Git Bash/WSL/macOS/Linux (hoặc bên trong container Docker) và log ra file `.txt`.

Những gì script thực hiện:
- Đăng nhập đủ 3 vai trò (Admin/Patient/Doctor) và xác nhận token.
- Admin: rà soát danh mục bệnh viện/bác sĩ.
- Doctor: cập nhật lịch làm việc (theo `dayOfWeek`, giờ bắt đầu/kết thúc chuẩn `HH:mm:ss`).
- Patient: xem slot trống, đặt lịch (bắt buộc `departmentId`, giờ chuẩn `HH:mm`), xem lịch sắp tới.
- Payment: tạo giao dịch, đánh dấu hoàn tất.
- Medical record/prescription: bác sĩ tạo hồ sơ, duyệt và kê đơn.
- Chat & feedback: bệnh nhân gửi tin nhắn, bác sĩ xem tin chưa đọc, bệnh nhân gửi đánh giá.

Chạy script:
```bash
# Biến môi trường tuỳ chỉnh (đã có mặc định seed):
# BASE_URL=http://localhost:8080/api/v1
# ADMIN_EMAIL=admin@chrms.vn ADMIN_PASSWORD=password123
# PATIENT_EMAIL=patient1@test.com PATIENT_PASSWORD=password123
# DOCTOR_EMAIL=doctor1@test.com DOCTOR_PASSWORD=password123
# HOSPITAL_ID=1 DEPARTMENT_ID=1 DOCTOR_ID=1
# APPOINTMENT_DATE=2025-01-01 APPOINTMENT_TIME=09:00

$ bash scripts/run_full_api_flow.sh          # chạy mặc định
$ bash scripts/run_full_api_flow.sh --help   # xem hướng dẫn
```

## 🗃 Cơ sở dữ liệu
- Host: `localhost:5432`
- DB: `chrms_db`
- User: `chrms_user`
- Password: `chrms_pass123`
- Flyway chạy migration khi app khởi động.

## 📚 Tài liệu liên quan
- [Quick Start](QUICKSTART.md): cURL mẫu, troubleshooting, seed data
- [Architecture](ARCHITECTURE.md): giải thích Clean Architecture
- [API Summary](API_SUMMARY.md): danh sách endpoint chi tiết
- [Postman Collection](CHRMS_Postman_Collection.json): request sẵn

## 🐛 Lỗi hay gặp
- **401 Unauthorized:** token hết hạn/thiếu → login lại.
- **404 Not Found:** sai ID → kiểm tra tham số.
- **400 Bad Request:** validate fail → xem trường bắt buộc/format.
- **409 Conflict:** sai logic nghiệp vụ (ví dụ trùng appointment).

## 🧑‍💻 Phát triển & kiểm thử
- **Code style:** tuân thủ phân tầng Clean Architecture; adapter không gọi ngược usecase/domain.
- **Chạy test:** `mvn test` hoặc module cụ thể `mvn -Dtest=... test`.
- **Seed dữ liệu tay:** thêm file Flyway mới trong `src/main/resources/db/migration` (không sửa file cũ).
- **Debug:** chạy profile `dev` để bật log SQL tối giản và hot reload bằng `spring-boot-devtools` (nếu IDE bật).

## ✅ Checklist trước khi commit
- Chạy test/build: `mvn test` (hoặc module liên quan).
- Tuân thủ phân tầng Clean Architecture (domain không phụ thuộc framework).
- Cập nhật tài liệu khi thêm/đổi tính năng.
