# CHRMS - Centralized Health Record Management System

## 🏥 Overview
MVP Backend cho hệ thống quản lý hồ sơ bệnh án điện tử tập trung - Hà Nội Digital Health Platform

**Tech Stack:**
- Java 17 / Spring Boot 3.2.x
- PostgreSQL 15 + Redis 7
- Clean Architecture Pattern
- JWT Authentication + RBAC
- Docker Compose

## 📐 Clean Architecture Structure

```
src/main/java/com/chrms/
├── domain/                    # Enterprise Business Rules (Core)
│   ├── entity/               # Pure business entities
│   ├── valueobject/          # Value objects (Email, Phone...)
│   ├── exception/            # Domain exceptions
│   └── repository/           # Repository interfaces (ports)
│
├── usecase/                  # Application Business Rules
│   ├── auth/                 # Login, Register use cases
│   ├── patient/              # Book appointment, view records
│   ├── doctor/               # Approve record, prescribe
│   ├── admin/                # Analytics, reports
│   └── shared/               # Shared use cases (search, notify)
│
├── adapter/                  # Interface Adapters
│   ├── in/                   # Input adapters
│   │   ├── web/              # REST Controllers
│   │   └── dto/              # Request/Response DTOs
│   ├── out/                  # Output adapters
│   │   ├── persistence/      # JPA implementations
│   │   ├── cache/            # Redis implementations
│   │   ├── email/            # Email service
│   │   └── file/             # File storage
│   └── mapper/               # Entity ↔ DTO mappers
│
└── infrastructure/           # Frameworks & Drivers
    ├── config/               # Spring configs (Security, Redis, Swagger)
    ├── security/             # JWT, filters
    └── exception/            # Global exception handler

src/main/resources/
├── application.yml           # Main config
├── application-dev.yml       # Dev profile
├── application-docker.yml    # Docker profile
└── db/migration/             # Flyway migrations
    ├── V1__init_schema.sql
    └── V2__seed_data.sql
```

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local dev)
- Maven 3.8+

### 1. Start với Docker (Recommended for FE Team)

```bash
# Clone repo
git clone <your-repo>
cd chrms-backend

# Start all services (BE + DB + Redis)
docker-compose up -d

# Check logs
docker-compose logs -f app

# API available at: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

**Default Test Accounts:**
```
Patient:  patient1@test.com / password123
Doctor:   doctor1@test.com / password123
Admin:    admin@chrms.vn / admin123
```

### 🧪 Chạy kịch bản API end-to-end (không cần Postman)

Script `scripts/run_full_api_flow.sh` tự động chạy toàn bộ luồng Patient → Doctor → Payment → Medical Record → Prescription → Chat → Feedback và ghi log chi tiết vào một file `.txt`.

```bash
# Điều chỉnh nếu cần: BASE_URL, LOG_FILE, PATIENT_EMAIL/PASSWORD, DOCTOR_EMAIL/PASSWORD, HOSPITAL_ID, DOCTOR_ID
bash scripts/run_full_api_flow.sh

# Xem hướng dẫn nhanh
bash scripts/run_full_api_flow.sh --help
```

Output mẫu được lưu ở `./api-test-run-YYYYMMDD-HHMMSS.txt`, mỗi bước đều hiển thị actor, endpoint, request body và response để tiện kiểm tra.

### 2. Local Development

```bash
# Start only DB + Redis
docker-compose up -d postgres redis

# Run Spring Boot
mvn spring-boot:run -Dspring.profiles.active=dev

# Or use IDE (IntelliJ IDEA recommended)
```

### 3. Stop Services

```bash
docker-compose down        # Stop all
docker-compose down -v     # Stop + remove volumes (clean DB)
```

## 📡 API Endpoints Overview

### Authentication
- `POST /api/v1/auth/register` - Register user
- `POST /api/v1/auth/login` - Login (returns JWT)
- `POST /api/v1/auth/logout` - Logout (blacklist JWT)

### Patient
- `GET /api/v1/patients/profile` - Get profile
- `POST /api/v1/appointments/book` - Book appointment
- `GET /api/v1/appointments/my` - My appointments
- `GET /api/v1/medical-records/my` - My records
- `POST /api/v1/chat/{apptId}/messages` - Send chat message
- `GET /api/v1/chat/{apptId}/messages` - Poll messages (every