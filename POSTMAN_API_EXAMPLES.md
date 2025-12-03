# CHRMS API Testing Guide - Postman Examples

Base URL: `http://localhost:8080/api/v1`

## 🔐 Authentication

### 1. Register Patient
**POST** `/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "patient1@test.com",
  "password": "password123",
  "fullName": "Nguyễn Văn A",
  "phone": "0912345678",
  "role": "PATIENT",
  "dateOfBirth": "1995-01-15",
  "gender": "MALE",
  "address": "123 Đường ABC, Hà Nội",
  "emergencyContact": "0987654321",
  "bloodType": "O",
  "allergies": "Không có"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "patient1@test.com",
    "fullName": "Nguyễn Văn A",
    "role": "PATIENT"
  },
  "timestamp": "2025-12-03T10:00:00"
}
```

### 2. Register Doctor
**POST** `/auth/register`

**Body (JSON):**
```json
{
  "email": "doctor1@test.com",
  "password": "password123",
  "fullName": "Bác Sĩ Nguyễn Văn B",
  "phone": "0911111111",
  "role": "DOCTOR"
}
```

### 3. Login
**POST** `/auth/login`

**Body (JSON):**
```json
{
  "email": "patient1@test.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "patient1@test.com",
    "fullName": "Nguyễn Văn A",
    "role": "PATIENT"
  }
}
```

---

## 🏥 Hospital & Doctor APIs

### 4. Get All Hospitals
**GET** `/hospitals`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Bệnh viện Bạch Mai",
      "address": "78 Giải Phóng, Hà Nội",
      "phone": "02438693731",
      "email": "contact@bachmai.gov.vn",
      "type": "PUBLIC"
    }
  ]
}
```

### 5. Get All Doctors
**GET** `/doctors`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "fullName": "Bác Sĩ Nguyễn Văn B",
      "specialty": "Tim mạch",
      "hospitalId": 1,
      "departmentId": 1,
      "experienceYears": 10,
      "consultationFee": 500000
    }
  ]
}
```

### 6. Get Doctors by Department
**GET** `/doctors/department/{departmentId}`

**Example:** `/doctors/department/1`

**Headers:**
```
Authorization: Bearer {token}
```

### 7. Get Doctors by Hospital
**GET** `/doctors/hospital/{hospitalId}`

**Example:** `/doctors/hospital/1`

---

## 📅 Doctor Schedule APIs

### 8. Create Doctor Schedule
**POST** `/doctors/schedules`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "doctorId": 1,
  "dayOfWeek": 1,
  "startTime": "08:00:00",
  "endTime": "12:00:00",
  "isAvailable": true
}
```

**Note:** `dayOfWeek`: 1=Monday, 2=Tuesday, ..., 7=Sunday

**Response:**
```json
{
  "success": true,
  "message": "Schedule created successfully",
  "data": {
    "id": 1,
    "doctorId": 1,
    "dayOfWeek": 1,
    "startTime": "08:00:00",
    "endTime": "12:00:00",
    "isAvailable": true,
    "createdAt": "2025-12-03T10:00:00",
    "updatedAt": "2025-12-03T10:00:00"
  }
}
```

### 9. Get Doctor Schedules
**GET** `/doctors/{doctorId}/schedules`

**Example:** `/doctors/1/schedules`

**Headers:**
```
Authorization: Bearer {token}
```

### 10. Get Available Time Slots
**GET** `/doctors/{doctorId}/available-slots?date=2025-12-10`

**Example:** `/doctors/1/available-slots?date=2025-12-10`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "time": "08:00:00",
      "available": true
    },
    {
      "time": "08:30:00",
      "available": true
    },
    {
      "time": "09:00:00",
      "available": false
    }
  ]
}
```

---

## 📋 Appointment APIs

### 11. Book Appointment
**POST** `/patients/appointments`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "doctorId": 1,
  "hospitalId": 1,
  "appointmentDate": "2025-12-10",
  "appointmentTime": "09:00:00",
  "symptoms": "Đau đầu, sốt nhẹ",
  "notes": "Bệnh nhân có tiền sử dị ứng thuốc"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Appointment booked successfully",
  "data": {
    "id": 1,
    "patientId": 1,
    "patientName": "Nguyễn Văn A",
    "doctorId": 1,
    "doctorName": "Bác Sĩ Nguyễn Văn B",
    "hospitalId": 1,
    "hospitalName": "Bệnh viện Bạch Mai",
    "appointmentDate": "2025-12-10",
    "appointmentTime": "09:00:00",
    "status": "PENDING",
    "symptoms": "Đau đầu, sốt nhẹ",
    "notes": "Bệnh nhân có tiền sử dị ứng thuốc",
    "createdAt": "2025-12-03T10:00:00"
  }
}
```

---

## 💊 Prescription APIs

### 12. Create Prescription
**POST** `/prescriptions`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "medicalRecordId": 1,
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
```

**Response:**
```json
{
  "success": true,
  "message": "Prescription created successfully",
  "data": {
    "id": 1,
    "medicalRecordId": 1,
    "items": [
      {
        "id": 1,
        "medicineId": 1,
        "medicineName": "Paracetamol 500mg",
        "dosage": "500mg",
        "frequency": "2 lần/ngày",
        "duration": "7 ngày",
        "quantity": 14,
        "instructions": "Uống sau khi ăn"
      }
    ],
    "createdAt": "2025-12-03T10:00:00",
    "updatedAt": "2025-12-03T10:00:00"
  }
}
```

### 13. Get Prescription by Medical Record
**GET** `/prescriptions/medical-record/{medicalRecordId}`

**Example:** `/prescriptions/medical-record/1`

**Headers:**
```
Authorization: Bearer {token}
```

---

## 📎 Medical Record Files APIs

### 14. Upload File
**POST** `/medical-records/files/upload`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Body (form-data):**
- `medicalRecordId`: `1` (number)
- `file`: (file) - Chọn file từ máy tính
- `fileType`: `XRAY` (enum: XRAY, LAB_RESULT, SCAN, OTHER)

**Response:**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "id": 1,
    "medicalRecordId": 1,
    "fileName": "xray-chest.jpg",
    "fileType": "XRAY",
    "fileSize": 1024000,
    "uploadedBy": 1,
    "createdAt": "2025-12-03T10:00:00"
  }
}
```

### 15. Get Files by Medical Record
**GET** `/medical-records/files/medical-record/{medicalRecordId}`

**Example:** `/medical-records/files/medical-record/1`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "medicalRecordId": 1,
      "fileName": "xray-chest.jpg",
      "fileType": "XRAY",
      "fileSize": 1024000,
      "uploadedBy": 1,
      "createdAt": "2025-12-03T10:00:00"
    }
  ]
}
```

### 16. Download File
**GET** `/medical-records/files/{id}/download`

**Example:** `/medical-records/files/1/download`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** File download (binary)

---

## 💳 Payment Transaction APIs

### 17. Create Payment Transaction
**POST** `/payments`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "appointmentId": 1,
  "paymentMethod": "VNPAY",
  "transactionRef": "TXN-ABC12345"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment transaction created successfully",
  "data": {
    "id": 1,
    "appointmentId": 1,
    "amount": 500000,
    "paymentMethod": "VNPAY",
    "status": "PENDING",
    "transactionRef": "TXN-ABC12345",
    "paidAt": null,
    "createdAt": "2025-12-03T10:00:00"
  }
}
```

### 18. Get Payments by Appointment
**GET** `/payments/appointment/{appointmentId}`

**Example:** `/payments/appointment/1`

**Headers:**
```
Authorization: Bearer {token}
```

### 19. Complete Payment (VNPay Callback)
**POST** `/payments/{transactionRef}/complete`

**Example:** `/payments/TXN-ABC12345/complete`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment completed successfully",
  "data": {
    "id": 1,
    "appointmentId": 1,
    "amount": 500000,
    "paymentMethod": "VNPAY",
    "status": "COMPLETED",
    "transactionRef": "TXN-ABC12345",
    "paidAt": "2025-12-03T10:05:00",
    "createdAt": "2025-12-03T10:00:00"
  }
}
```

---

## 📋 Medical Records APIs

### 20. Create Medical Record
**POST** `/medical-records`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "appointmentId": 1,
  "diagnosis": "Viêm phế quản cấp",
  "treatment": "Nghỉ ngơi, uống nhiều nước, dùng thuốc kháng sinh",
  "notes": "Bệnh nhân cần theo dõi thêm"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Medical record created successfully",
  "data": {
    "id": 1,
    "patientId": 1,
    "doctorId": 1,
    "hospitalId": 1,
    "appointmentId": 1,
    "diagnosis": "Viêm phế quản cấp",
    "treatment": "Nghỉ ngơi, uống nhiều nước, dùng thuốc kháng sinh",
    "status": "DRAFT",
    "recordDate": "2025-12-03",
    "notes": "Bệnh nhân cần theo dõi thêm",
    "createdAt": "2025-12-03T10:00:00",
    "updatedAt": "2025-12-03T10:00:00"
  }
}
```

### 21. Approve Medical Record
**POST** `/medical-records/{id}/approve`

**Example:** `/medical-records/1/approve`

**Headers:**
```
Authorization: Bearer {token}
```

### 22. Get Records by Patient
**GET** `/medical-records/patient/{patientId}`

**Example:** `/medical-records/patient/1`

### 23. Get Record by ID
**GET** `/medical-records/{id}`

**Example:** `/medical-records/1`

---

## 💬 Chat APIs (Polling-based)

### 24. Send Chat Message
**POST** `/chat/appointments/{appointmentId}/messages`

**Example:** `/chat/appointments/1/messages`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "message": "Xin chào bác sĩ, tôi muốn hỏi về tình trạng sức khỏe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Message sent successfully",
  "data": {
    "id": 1,
    "appointmentId": 1,
    "senderId": 1,
    "message": "Xin chào bác sĩ, tôi muốn hỏi về tình trạng sức khỏe",
    "isRead": false,
    "createdAt": "2025-12-03T10:00:00"
  }
}
```

### 25. Get Messages (Polling)
**GET** `/chat/appointments/{appointmentId}/messages?after=2025-12-03T09:00:00`

**Example:** `/chat/appointments/1/messages?after=2025-12-03T09:00:00`

**Headers:**
```
Authorization: Bearer {token}
```

**Note:** Parameter `after` là optional. Nếu có, chỉ lấy messages sau thời điểm đó (dùng cho polling).

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "appointmentId": 1,
      "senderId": 1,
      "message": "Xin chào bác sĩ",
      "isRead": false,
      "createdAt": "2025-12-03T10:00:00"
    }
  ]
}
```

### 26. Get Unread Messages (Polling)
**GET** `/chat/appointments/{appointmentId}/messages/unread`

**Example:** `/chat/appointments/1/messages/unread`

**Headers:**
```
Authorization: Bearer {token}
```

**Note:** Endpoint này dùng để polling unread messages mỗi 10 giây.

---

## ⭐ Feedback APIs

### 27. Submit Feedback
**POST** `/feedback`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "appointmentId": 1,
  "rating": 5,
  "comment": "Bác sĩ rất tận tâm, tư vấn rõ ràng"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Feedback submitted successfully",
  "data": {
    "id": 1,
    "appointmentId": 1,
    "patientId": 1,
    "doctorId": 1,
    "rating": 5,
    "comment": "Bác sĩ rất tận tâm, tư vấn rõ ràng",
    "createdAt": "2025-12-03T10:00:00"
  }
}
```

### 28. Get Feedback by Doctor
**GET** `/feedback/doctor/{doctorId}`

**Example:** `/feedback/doctor/1`

**Headers:**
```
Authorization: Bearer {token}
```

### 29. Get Average Rating
**GET** `/feedback/doctor/{doctorId}/average-rating`

**Example:** `/feedback/doctor/1/average-rating`

**Response:**
```json
{
  "success": true,
  "data": 4.5
}
```

---

## 📝 Complete Testing Flow

### End-to-End Workflow:

1. **Register Patient** → Get token
2. **Register Doctor** → Get token
3. **Login (Patient)** → Verify token
4. **Login (Doctor)** → Verify token
5. **Get Hospitals** → List hospitals
6. **Get Doctors** → List doctors
7. **Get Doctors by Department** → Filter by department
8. **Create Doctor Schedule** (as Doctor) → Set working hours
9. **Get Available Slots** → Check available times
10. **Book Appointment** (as Patient) → Create appointment
11. **Create Payment Transaction** → Initiate payment
12. **Complete Payment** → Mark as paid
13. **Create Medical Record** (as Doctor) → Create record after appointment
14. **Upload Medical Record File** (as Doctor) → Add X-ray/lab results
15. **Approve Medical Record** (as Doctor) → Approve the record
16. **Create Prescription** (as Doctor) → Prescribe medicines
17. **Get Prescription** → View prescription
18. **Send Chat Message** (Patient/Doctor) → Start conversation
19. **Get Chat Messages** (Polling) → Get new messages
20. **Submit Feedback** (as Patient) → Rate doctor after appointment
21. **Get Feedback** → View doctor ratings

---

## 🔑 Important Notes

1. **JWT Token**: Sau khi login/register, copy token từ response và dùng trong header:
   ```
   Authorization: Bearer {your_token_here}
   ```

2. **File Upload**: Trong Postman, chọn tab "Body" → "form-data" → Key chọn "File" type

3. **Date Format**: Sử dụng format `YYYY-MM-DD` (ví dụ: `2025-12-10`)

4. **Time Format**: Sử dụng format `HH:mm:ss` (ví dụ: `09:00:00`)

5. **Enum Values**:
   - `role`: PATIENT, DOCTOR, ADMIN
   - `gender`: MALE, FEMALE, OTHER
   - `fileType`: XRAY, LAB_RESULT, SCAN, OTHER
   - `paymentMethod`: VNPAY, CASH, CARD
   - `status`: PENDING, COMPLETED, FAILED (for payment)

6. **Error Responses**: Tất cả errors đều có format:
   ```json
   {
     "success": false,
     "message": "Error message here",
     "data": null,
     "timestamp": "2025-12-03T10:00:00"
   }
   ```

---

## 🧪 Quick Test Checklist

### Authentication
- [ ] Register Patient
- [ ] Register Doctor  
- [ ] Login (Patient)
- [ ] Login (Doctor)

### Hospitals & Doctors
- [ ] Get Hospitals
- [ ] Get Doctors
- [ ] Get Doctors by Department

### Schedules & Appointments
- [ ] Create Doctor Schedule
- [ ] Get Available Slots
- [ ] Book Appointment

### Payments
- [ ] Create Payment Transaction
- [ ] Complete Payment

### Medical Records
- [ ] Create Medical Record
- [ ] Approve Medical Record
- [ ] Get Records by Patient
- [ ] Upload File
- [ ] Get Files

### Prescriptions
- [ ] Create Prescription
- [ ] Get Prescription

### Chat
- [ ] Send Chat Message
- [ ] Get Messages (Polling)
- [ ] Get Unread Messages

### Feedback
- [ ] Submit Feedback
- [ ] Get Feedback by Doctor
- [ ] Get Average Rating

