# CHRMS Clean Architecture Structure

## 📐 Layer Organization

```
src/main/java/com/chrms/
│
├── domain/                           # LAYER 1: Domain (Core Business Logic)
│   ├── entity/                       # Business entities
│   │   ├── User.java
│   │   ├── Patient.java
│   │   ├── Doctor.java
│   │   ├── Hospital.java
│   │   ├── Department.java
│   │   ├── MedicalRecord.java
│   │   ├── Appointment.java
│   │   ├── Prescription.java
│   │   ├── Medicine.java
│   │   ├── Schedule.java
│   │   ├── ChatMessage.java
│   │   ├── RecordShare.java
│   │   └── Feedback.java
│   │
│   ├── valueobject/                  # Value objects (immutable)
│   │   ├── Email.java
│   │   ├── PhoneNumber.java
│   │   ├── Address.java
│   │   ├── DateRange.java
│   │   └── Money.java
│   │
│   ├── enums/                        # Domain enums
│   │   ├── Role.java                 # PATIENT, DOCTOR, ADMIN
│   │   ├── AppointmentStatus.java    # PENDING, CONFIRMED, COMPLETED, CANCELLED
│   │   ├── RecordStatus.java         # DRAFT, PENDING, APPROVED, SHARED
│   │   └── PaymentStatus.java
│   │
│   ├── repository/                   # Repository interfaces (Ports)
│   │   ├── UserRepository.java
│   │   ├── PatientRepository.java
│   │   ├── DoctorRepository.java
│   │   ├── HospitalRepository.java
│   │   ├── MedicalRecordRepository.java
│   │   ├── AppointmentRepository.java
│   │   ├── PrescriptionRepository.java
│   │   ├── RecordShareRepository.java
│   │   └── ChatMessageRepository.java
│   │
│   └── exception/                    # Domain exceptions
│       ├── DomainException.java
│       ├── EntityNotFoundException.java
│       ├── BusinessRuleViolationException.java
│       └── UnauthorizedException.java
│
├── application/                      # LAYER 2: Application (Use Cases)
│   ├── usecase/                      # Use case implementations
│   │   ├── auth/
│   │   │   ├── RegisterUseCase.java
│   │   │   ├── LoginUseCase.java
│   │   │   └── LogoutUseCase.java
│   │   │
│   │   ├── patient/
│   │   │   ├── BookAppointmentUseCase.java
│   │   │   ├── ViewMyRecordsUseCase.java
│   │   │   ├── SubmitFeedbackUseCase.java
│   │   │   └── UpdateProfileUseCase.java
│   │   │
│   │   ├── doctor/
│   │   │   ├── ManageScheduleUseCase.java
│   │   │   ├── ApproveRecordUseCase.java
│   │   │   ├── PrescribeMedicineUseCase.java
│   │   │   ├── ShareRecordUseCase.java
│   │   │   └── SearchRecordsUseCase.java
│   │   │
│   │   ├── admin/
│   │   │   ├── GenerateAnalyticsUseCase.java
│   │   │   ├── ManageHospitalsUseCase.java
│   │   │   ├── ManageUsersUseCase.java
│   │   │   └── ExportReportUseCase.java
│   │   │
│   │   └── shared/
│   │       ├── SendChatMessageUseCase.java
│   │       ├── GetChatMessagesUseCase.java
│   │       └── UploadFileUseCase.java
│   │
│   ├── port/                         # Application ports (interfaces)
│   │   ├── in/                       # Input ports (use case interfaces)
│   │   │   ├── auth/
│   │   │   │   ├── RegisterInputPort.java
│   │   │   │   └── LoginInputPort.java
│   │   │   ├── patient/
│   │   │   │   └── BookAppointmentInputPort.java
│   │   │   └── doctor/
│   │   │       └── ApproveRecordInputPort.java
│   │   │
│   │   └── out/                      # Output ports (service interfaces)
│   │       ├── EmailService.java
│   │       ├── FileStorageService.java
│   │       ├── CacheService.java
│   │       ├── NotificationService.java
│   │       └── PdfExportService.java
│   │
│   └── dto/                          # Application DTOs (use case level)
│       ├── command/                  # Commands (input)
│       │   ├── RegisterCommand.java
│       │   ├── BookAppointmentCommand.java
│       │   └── ApproveRecordCommand.java
│       │
│       └── result/                   # Results (output)
│           ├── AuthResult.java
│           ├── AppointmentResult.java
│           └── RecordResult.java
│
├── presentation/                     # LAYER 3: Presentation (Controllers & DTOs)
│   ├── controller/                   # REST Controllers
│   │   ├── AuthController.java
│   │   ├── PatientController.java
│   │   ├── DoctorController.java
│   │   ├── AdminController.java
│   │   ├── AppointmentController.java
│   │   ├── MedicalRecordController.java
│   │   ├── ChatController.java
│   │   └── FileController.java
│   │
│   ├── dto/                          # API DTOs (presentation level)
│   │   ├── request/                  # API Request DTOs
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── BookAppointmentRequest.java
│   │   │   ├── UpdateProfileRequest.java
│   │   │   ├── ApproveRecordRequest.java
│   │   │   ├── PrescribeRequest.java
│   │   │   └── SendMessageRequest.java
│   │   │
│   │   └── response/                 # API Response DTOs
│   │       ├── ApiResponse.java      # Generic wrapper
│   │       ├── ErrorResponse.java
│   │       ├── AuthResponse.java     # { token, user }
│   │       ├── PatientResponse.java
│   │       ├── DoctorResponse.java
│   │       ├── AppointmentResponse.java
│   │       ├── RecordResponse.java
│   │       ├── ChatMessageResponse.java
│   │       └── AnalyticsResponse.java
│   │
│   ├── mapper/                       # Mappers (Request/Response ↔ Command/Result)
│   │   ├── AuthMapper.java
│   │   ├── PatientMapper.java
│   │   ├── DoctorMapper.java
│   │   └── RecordMapper.java
│   │
│   └── validation/                   # Custom validators for requests
│       ├── EmailValidator.java
│       ├── PhoneValidator.java
│       └── DateRangeValidator.java
│
└── infrastructure/                   # LAYER 4: Infrastructure (Frameworks)
    ├── persistence/                  # JPA implementations
    │   ├── entity/                   # JPA entities (@Entity)
    │   │   ├── UserJpaEntity.java
    │   │   ├── PatientJpaEntity.java
    │   │   ├── DoctorJpaEntity.java
    │   │   ├── MedicalRecordJpaEntity.java
    │   │   └── AppointmentJpaEntity.java
    │   │
    │   ├── repository/               # Spring Data JPA repositories
    │   │   ├── UserJpaRepository.java
    │   │   ├── PatientJpaRepository.java
    │   │   └── RecordJpaRepository.java
    │   │
    │   └── adapter/                  # Repository adapters (implement domain repositories)
    │       ├── UserRepositoryAdapter.java
    │       ├── PatientRepositoryAdapter.java
    │       └── RecordRepositoryAdapter.java
    │
    ├── cache/                        # Redis implementation
    │   ├── RedisCacheService.java
    │   └── RedisConfig.java
    │
    ├── email/                        # Email service implementation
    │   └── SpringMailService.java
    │
    ├── file/                         # File storage implementation
    │   └── LocalFileStorageService.java
    │
    ├── security/                     # Security infrastructure
    │   ├── jwt/
    │   │   ├── JwtTokenProvider.java
    │   │   ├── JwtAuthenticationFilter.java
    │   │   └── JwtAuthenticationEntryPoint.java
    │   │
    │   └── config/
    │       └── SecurityConfig.java
    │
    ├── config/                       # Spring configurations
    │   ├── DatabaseConfig.java
    │   ├── RedisConfig.java
    │   ├── SwaggerConfig.java
    │   ├── AsyncConfig.java
    │   └── WebConfig.java
    │
    └── exception/                    # Global exception handling
        ├── GlobalExceptionHandler.java
        └── ErrorCode.java
```

## 🔄 Dependency Flow (Clean Architecture Rules)

```
Presentation Layer ──→ Application Layer ──→ Domain Layer
       ↓                      ↓                    ↑
Infrastructure Layer ─────────────────────────────┘
```

**Rules:**
1. **Domain** không phụ thuộc vào layer nào (pure business logic)
2. **Application** chỉ phụ thuộc vào Domain
3. **Presentation** phụ thuộc vào Application (qua input ports)
4. **Infrastructure** implement các interfaces từ Domain & Application

## 📦 Package Dependencies

```java
// ✅ ALLOWED
domain/ → (no dependencies)
application/ → domain/
presentation/ → application/, domain/
infrastructure/ → domain/, application/

// ❌ NOT ALLOWED
domain/ → application/    // Domain không biết Application
domain/ → infrastructure/ // Domain không biết DB/Framework
application/ → infrastructure/ // Application không biết implementation details
```

## 🔌 How Layers Communicate

### Example: Book Appointment Flow

```
1. PRESENTATION (Controller)
   ↓ BookAppointmentRequest (DTO)
   ↓ AuthMapper.toCommand()
   
2. APPLICATION (Use Case)
   ↓ BookAppointmentCommand
   ↓ BookAppointmentUseCase.execute()
   ↓ Call domain repository (interface)
   
3. INFRASTRUCTURE (Adapter)
   ↓ AppointmentRepositoryAdapter (implements domain interface)
   ↓ Spring Data JPA
   ↓ PostgreSQL
   
4. Return flow (reverse)
   ↑ Domain Entity
   ↑ AppointmentResult (application DTO)
   ↑ AppointmentMapper.toResponse()
   ↑ AppointmentResponse (presentation DTO)
   ↑ JSON to FE
```

## 🎯 Key Benefits

1. **Testability**: Mock repositories dễ dàng
2. **Independence**: Domain logic không bị ảnh hưởng bởi framework
3. **Maintainability**: Thay đổi DB/cache không ảnh hưởng business logic
4. **Clear boundaries**: Mỗi layer có trách nhiệm rõ ràng

## 📝 Naming Conventions

- **Domain Entities**: `MedicalRecord`, `Appointment` (pure business)
- **JPA Entities**: `MedicalRecordJpaEntity` (infrastructure)
- **Request DTOs**: `BookAppointmentRequest` (presentation)
- **Command DTOs**: `BookAppointmentCommand` (application)
- **Response DTOs**: `AppointmentResponse` (presentation)
- **Use Cases**: `BookAppointmentUseCase` (application)
- **Input Ports**: `BookAppointmentInputPort` (application interface)
- **Adapters**: `AppointmentRepositoryAdapter` (infrastructure)
