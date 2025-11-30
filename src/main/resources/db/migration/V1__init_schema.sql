-- V1__init_schema.sql

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Hospitals Table
CREATE TABLE hospitals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- PUBLIC, PRIVATE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hospitals_type ON hospitals(type);

-- Departments Table
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    hospital_id BIGINT NOT NULL REFERENCES hospitals(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_departments_hospital ON departments(hospital_id);

-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL, -- PATIENT, DOCTOR, ADMIN
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Patients Table
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    address VARCHAR(500),
    emergency_contact VARCHAR(20),
    blood_type VARCHAR(5),
    allergies TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patients_user ON patients(user_id);

-- Doctors Table
CREATE TABLE doctors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hospital_id BIGINT NOT NULL REFERENCES hospitals(id),
    department_id BIGINT REFERENCES departments(id),
    specialty VARCHAR(255) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    experience_years INT DEFAULT 0,
    consultation_fee DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctors_user ON doctors(user_id);
CREATE INDEX idx_doctors_hospital ON doctors(hospital_id);
CREATE INDEX idx_doctors_department ON doctors(department_id);
CREATE INDEX idx_doctors_specialty ON doctors(specialty);

-- Doctor Schedules Table
CREATE TABLE doctor_schedules (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    day_of_week INT NOT NULL, -- 1=Monday, 7=Sunday
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_schedules_doctor ON doctor_schedules(doctor_id);
CREATE INDEX idx_schedules_day ON doctor_schedules(day_of_week);

-- Appointments Table
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    hospital_id BIGINT NOT NULL REFERENCES hospitals(id),
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, COMPLETED, CANCELLED
    symptoms TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_hospital ON appointments(hospital_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);

-- Medical Records Table
CREATE TABLE medical_records (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    hospital_id BIGINT NOT NULL REFERENCES hospitals(id),
    appointment_id BIGINT REFERENCES appointments(id),
    diagnosis TEXT NOT NULL,
    treatment TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- DRAFT, PENDING, APPROVED, SHARED
    record_date DATE NOT NULL,
    notes TEXT,
    symptoms_tsvector TSVECTOR,
    diagnosis_tsvector TSVECTOR,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_records_patient ON medical_records(patient_id);
CREATE INDEX idx_records_doctor ON medical_records(doctor_id);
CREATE INDEX idx_records_hospital ON medical_records(hospital_id);
CREATE INDEX idx_records_status ON medical_records(status);
CREATE INDEX idx_records_symptoms_fts ON medical_records USING GIN(symptoms_tsvector);
CREATE INDEX idx_records_diagnosis_fts ON medical_records USING GIN(diagnosis_tsvector);

-- Medicines Table
CREATE TABLE medicines (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    manufacturer VARCHAR(255),
    dosage_form VARCHAR(100), -- Tablet, Capsule, Syrup, etc.
    strength VARCHAR(50),
    description TEXT,
    unit_price DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medicines_name ON medicines(name);

-- Prescriptions Table
CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    medical_record_id BIGINT NOT NULL REFERENCES medical_records(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prescriptions_record ON prescriptions(medical_record_id);

-- Prescription Items Table
CREATE TABLE prescription_items (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medicine_id BIGINT NOT NULL REFERENCES medicines(id),
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prescription_items_prescription ON prescription_items(prescription_id);
CREATE INDEX idx_prescription_items_medicine ON prescription_items(medicine_id);

-- Medical Record Files Table
CREATE TABLE medical_record_files (
    id BIGSERIAL PRIMARY KEY,
    medical_record_id BIGINT NOT NULL REFERENCES medical_records(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL, -- XRAY, LAB_RESULT, SCAN, OTHER
    file_size BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_record_files_record ON medical_record_files(medical_record_id);
CREATE INDEX idx_record_files_type ON medical_record_files(file_type);

-- Record Shares Table
CREATE TABLE record_shares (
    id BIGSERIAL PRIMARY KEY,
    medical_record_id BIGINT NOT NULL REFERENCES medical_records(id),
    from_hospital_id BIGINT NOT NULL REFERENCES hospitals(id),
    to_hospital_id BIGINT NOT NULL REFERENCES hospitals(id),
    shared_by BIGINT NOT NULL REFERENCES users(id),
    shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

CREATE INDEX idx_shares_record ON record_shares(medical_record_id);
CREATE INDEX idx_shares_from_hospital ON record_shares(from_hospital_id);
CREATE INDEX idx_shares_to_hospital ON record_shares(to_hospital_id);

-- Chat Messages Table
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    sender_id BIGINT NOT NULL REFERENCES users(id),
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_messages_appointment ON chat_messages(appointment_id);
CREATE INDEX idx_messages_sender ON chat_messages(sender_id);
CREATE INDEX idx_messages_created ON chat_messages(created_at);

-- Feedback Table
CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_feedback_appointment ON feedback(appointment_id);
CREATE INDEX idx_feedback_doctor ON feedback(doctor_id);
CREATE INDEX idx_feedback_rating ON feedback(rating);

-- Payment Transactions Table
CREATE TABLE payment_transactions (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- VNPAY, CASH, CARD
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED
    transaction_ref VARCHAR(100),
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_appointment ON payment_transactions(appointment_id);
CREATE INDEX idx_transactions_status ON payment_transactions(status);

-- Notifications Table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- APPOINTMENT, RECORD, CHAT, SYSTEM
    is_read BOOLEAN DEFAULT FALSE,
    reference_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_type ON notifications(type);

-- Trigger for updating updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_hospitals_updated_at BEFORE UPDATE ON hospitals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_departments_updated_at BEFORE UPDATE ON departments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_patients_updated_at BEFORE UPDATE ON patients
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_doctors_updated_at BEFORE UPDATE ON doctors
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_schedules_updated_at BEFORE UPDATE ON doctor_schedules
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_appointments_updated_at BEFORE UPDATE ON appointments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_records_updated_at BEFORE UPDATE ON medical_records
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger for full-text search vectors
CREATE OR REPLACE FUNCTION update_medical_record_tsvectors()
RETURNS TRIGGER AS $$
BEGIN
    NEW.symptoms_tsvector := to_tsvector('english', COALESCE(NEW.symptoms, ''));
    NEW.diagnosis_tsvector := to_tsvector('english', COALESCE(NEW.diagnosis, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tsvector_update BEFORE INSERT OR UPDATE
    ON medical_records FOR EACH ROW
    EXECUTE FUNCTION update_medical_record_tsvectors();
