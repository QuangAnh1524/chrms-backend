# CHRMS API Summary - All Endpoints

Base URL: `http://localhost:8080/api/v1`

## 📋 Complete API List

### 🔐 Authentication (3 endpoints)
1. `POST /auth/register` - Register new user (Patient/Doctor/Admin)
2. `POST /auth/login` - Login and get JWT token
3. `POST /auth/logout` - Logout (TODO: JWT blacklist)

### 🏥 Hospitals & Doctors (4 endpoints)
4. `GET /hospitals` - Get all hospitals
5. `GET /doctors` - Get all doctors
6. `GET /doctors/department/{departmentId}` - Get doctors by department
7. `GET /doctors/hospital/{hospitalId}` - Get doctors by hospital

### 📅 Doctor Schedules (3 endpoints)
8. `POST /doctors/schedules` - Create doctor schedule
9. `GET /doctors/{doctorId}/schedules` - Get doctor schedules
10. `GET /doctors/{doctorId}/available-slots?date={date}` - Get available time slots

### 📋 Appointments (1 endpoint)
11. `POST /patients/appointments` - Book appointment

### 💊 Prescriptions (2 endpoints)
12. `POST /prescriptions` - Create prescription
13. `GET /prescriptions/medical-record/{medicalRecordId}` - Get prescription by medical record

### 📎 Medical Record Files (3 endpoints)
14. `POST /medical-records/files/upload` - Upload file (multipart/form-data)
15. `GET /medical-records/files/medical-record/{medicalRecordId}` - Get files by medical record
16. `GET /medical-records/files/{id}/download` - Download file

### 💳 Payments (3 endpoints)
17. `POST /payments` - Create payment transaction
18. `GET /payments/appointment/{appointmentId}` - Get payments by appointment
19. `POST /payments/{transactionRef}/complete` - Complete payment

### 📝 Medical Records (4 endpoints)
20. `POST /medical-records` - Create medical record
21. `POST /medical-records/{id}/approve` - Approve medical record
22. `GET /medical-records/patient/{patientId}` - Get records by patient
23. `GET /medical-records/{id}` - Get record by ID

### 💬 Chat Messages (3 endpoints - Polling-based)
24. `POST /chat/appointments/{appointmentId}/messages` - Send message
25. `GET /chat/appointments/{appointmentId}/messages?after={datetime}` - Get messages (polling)
26. `GET /chat/appointments/{appointmentId}/messages/unread` - Get unread messages (polling)

### ⭐ Feedback (3 endpoints)
27. `POST /feedback` - Submit feedback
28. `GET /feedback/doctor/{doctorId}` - Get feedback by doctor
29. `GET /feedback/doctor/{doctorId}/average-rating` - Get average rating

---

## 🎯 Quick Test Scenarios

### Scenario 1: Patient Books Appointment
1. Register Patient → Get token
2. Get Hospitals
3. Get Doctors
4. Get Available Slots
5. Book Appointment
6. Create Payment
7. Complete Payment

### Scenario 2: Doctor Creates Record
1. Register Doctor → Get token
2. Create Doctor Schedule
3. Create Medical Record (after appointment exists)
4. Upload File
5. Approve Medical Record
6. Create Prescription

### Scenario 3: Chat Conversation
1. Patient sends message
2. Doctor sends reply
3. Poll for new messages (every 10s)
4. Get unread messages

### Scenario 4: Patient Feedback
1. Patient submits feedback after appointment
2. View doctor's average rating
3. View all feedback for doctor

---

## 📝 Important Notes

1. **JWT Token**: Required for all endpoints except `/auth/register` and `/auth/login`
   - Header: `Authorization: Bearer {token}`

2. **File Upload**: Use `multipart/form-data` with fields:
   - `medicalRecordId` (number)
   - `file` (file)
   - `fileType` (enum: XRAY, LAB_RESULT, SCAN, OTHER)

3. **Polling**: For chat messages, poll every 10 seconds:
   - Use `after` parameter to get only new messages
   - Or use `/unread` endpoint

4. **Date/Time Formats**:
   - Date: `YYYY-MM-DD` (e.g., `2025-12-10`)
   - Time: `HH:mm:ss` (e.g., `09:00:00`)
   - DateTime: `YYYY-MM-DDTHH:mm:ss` (e.g., `2025-12-03T10:00:00`)

5. **Enums**:
   - `role`: PATIENT, DOCTOR, ADMIN
   - `gender`: MALE, FEMALE, OTHER
   - `fileType`: XRAY, LAB_RESULT, SCAN, OTHER
   - `paymentMethod`: VNPAY, CASH, CARD
   - `paymentStatus`: PENDING, COMPLETED, FAILED
   - `recordStatus`: DRAFT, PENDING, APPROVED, SHARED
   - `appointmentStatus`: PENDING, CONFIRMED, COMPLETED, CANCELLED

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

**Total: 29 API Endpoints** ✅

