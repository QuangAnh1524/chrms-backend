-- V2__seed_data.sql

-- Insert 13 Hospitals (10 public + 3 private)
INSERT INTO hospitals (name, address, phone, email, type) VALUES
('Bệnh viện Bạch Mai', '78 Đường Giải Phóng, Đống Đa, Hà Nội', '0243.8691111', 'info@bvbachmai.vn', 'PUBLIC'),
('Bệnh viện Việt Đức', '40 Tràng Thi, Hoàn Kiếm, Hà Nội', '0243.8253531', 'benhvienvietduc@gmail.com', 'PUBLIC'),
('Bệnh viện E', '89 Trần Cung, Bắc Từ Liêm, Hà Nội', '0243.8354043', 'bve@benhviene.gov.vn', 'PUBLIC'),
('Bệnh viện 108', '1 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội', '0243.8574088', 'bv108@bv108.vn', 'PUBLIC'),
('Bệnh viện Thanh Nhàn', '42 Thanh Nhàn, Hai Bà Trưng, Hà Nội', '0243.8212167', 'bvtn@benhvienthanhnhan.vn', 'PUBLIC'),
('Bệnh viện Xanh Pôn', '12 Chu Văn An, Ba Đình, Hà Nội', '0243.8464463', 'bvxanhpon@gmail.com', 'PUBLIC'),
('Bệnh viện Phụ Sản Hà Nội', '6 Giảng Võ, Ba Đình, Hà Nội', '0243.8438831', 'bvpshni@gmail.com', 'PUBLIC'),
('Bệnh viện Nhi Trung ương', '18 Lê Lợi, Hà Đông, Hà Nội', '0243.8543165', 'bvntw@benhviennhitw.vn', 'PUBLIC'),
('Bệnh viện Đa khoa Đống Đa', '68 Nguyễn Lương Bằng, Đống Đa, Hà Nội', '0243.8578251', 'bvdongda@gmail.com', 'PUBLIC'),
('Bệnh viện Y học cổ truyền TW', '29 Nguyễn Bỉnh Khiêm, Hai Bà Trưng, Hà Nội', '0243.8231532', 'bvyhhctw@yahoo.com', 'PUBLIC'),
('Bệnh viện Vinmec Times City', '458 Minh Khai, Hai Bà Trưng, Hà Nội', '0243.9743556', 'info@vinmec.com', 'PRIVATE'),
('Bệnh viện Đa khoa Quốc tế Thu Cúc', '216 Trần Duy Hưng, Cầu Giấy, Hà Nội', '0247.3012345', 'info@benhvienthucuc.vn', 'PRIVATE'),
('Bệnh viện Đa khoa Hồng Ngọc', '55 Yên Ninh, Ba Đình, Hà Nội', '0243.8275568', 'info@hongngocdental.vn', 'PRIVATE');

-- Insert Departments (5 departments per hospital, 65 total)
INSERT INTO departments (hospital_id, name, description) VALUES
-- Bạch Mai (hospital_id = 1)
(1, 'Khoa Tim mạch', 'Chuyên khoa điều trị các bệnh lý tim mạch'),
(1, 'Khoa Thần kinh', 'Chuyên khoa điều trị các bệnh lý thần kinh'),
(1, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(1, 'Khoa Nội tổng hợp', 'Điều trị nội khoa tổng quát'),
(1, 'Khoa Chẩn đoán hình ảnh', 'X-quang, CT, MRI'),
-- Việt Đức (hospital_id = 2)
(2, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(2, 'Khoa Chấn thương chỉnh hình', 'Điều trị chấn thương xương khớp'),
(2, 'Khoa Tiêu hóa', 'Điều trị bệnh lý tiêu hóa'),
(2, 'Khoa Hồi sức cấp cứu', 'Hồi sức tích cực'),
(2, 'Khoa Ung bướu', 'Điều trị ung thư'),
-- Bệnh viện E (hospital_id = 3)
(3, 'Khoa Nội tổng hợp', 'Điều trị nội khoa tổng quát'),
(3, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(3, 'Khoa Tai Mũi Họng', 'Điều trị bệnh lý TMH'),
(3, 'Khoa Mắt', 'Điều trị bệnh lý mắt'),
(3, 'Khoa Da liễu', 'Điều trị bệnh lý da'),
-- Bệnh viện 108 (hospital_id = 4)
(4, 'Khoa Cấp cứu', 'Cấp cứu 24/7'),
(4, 'Khoa Tim mạch', 'Chuyên khoa tim mạch'),
(4, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(4, 'Khoa Hô hấp', 'Điều trị bệnh lý hô hấp'),
(4, 'Khoa Thận - Tiết niệu', 'Điều trị bệnh lý thận'),
-- Thanh Nhàn (hospital_id = 5)
(5, 'Khoa Nội tổng hợp', 'Điều trị nội khoa'),
(5, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(5, 'Khoa Sản', 'Chăm sóc thai sản'),
(5, 'Khoa Nhi', 'Điều trị bệnh nhi'),
(5, 'Khoa Răng Hàm Mặt', 'Điều trị răng hàm mặt'),
-- Xanh Pôn (hospital_id = 6)
(6, 'Khoa Nội tổng hợp', 'Điều trị nội khoa'),
(6, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(6, 'Khoa Phụ sản', 'Chăm sóc phụ sản'),
(6, 'Khoa Nhi', 'Điều trị bệnh nhi'),
(6, 'Khoa Chẩn đoán hình ảnh', 'X-quang, siêu âm'),
-- Phụ Sản HN (hospital_id = 7)
(7, 'Khoa Sản', 'Chăm sóc thai sản'),
(7, 'Khoa Phụ khoa', 'Điều trị phụ khoa'),
(7, 'Khoa Hồi sức sơ sinh', 'Chăm sóc trẻ sơ sinh'),
(7, 'Khoa Kế hoạch hóa gia đình', 'Tư vấn KHHGĐ'),
(7, 'Khoa Chẩn đoán hình ảnh', 'Siêu âm 4D'),
-- Nhi TW (hospital_id = 8)
(8, 'Khoa Nhi tổng hợp', 'Điều trị bệnh nhi'),
(8, 'Khoa Hô hấp nhi', 'Điều trị hô hấp trẻ em'),
(8, 'Khoa Tiêu hóa nhi', 'Điều trị tiêu hóa trẻ em'),
(8, 'Khoa Tim mạch nhi', 'Điều trị tim mạch trẻ em'),
(8, 'Khoa Hồi sức tích cực', 'Hồi sức nhi'),
-- Đống Đa (hospital_id = 9)
(9, 'Khoa Nội tổng hợp', 'Điều trị nội khoa'),
(9, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(9, 'Khoa Sản', 'Chăm sóc sản'),
(9, 'Khoa Nhi', 'Điều trị nhi'),
(9, 'Khoa Khám bệnh', 'Khám bệnh tổng quát'),
-- Y học cổ truyền (hospital_id = 10)
(10, 'Khoa Châm cứu', 'Điều trị châm cứu'),
(10, 'Khoa Đông y', 'Điều trị bằng đông y'),
(10, 'Khoa Phục hồi chức năng', 'Vật lý trị liệu'),
(10, 'Khoa Xoa bóp bấm huyệt', 'Massage trị liệu'),
(10, 'Khoa Nội tổng hợp', 'Kết hợp đông tây y'),
-- Vinmec (hospital_id = 11)
(11, 'Khoa Nội tổng hợp', 'Điều trị nội khoa'),
(11, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(11, 'Khoa Tim mạch', 'Chuyên khoa tim mạch'),
(11, 'Khoa Ung bướu', 'Điều trị ung thư'),
(11, 'Khoa Khám sức khỏe', 'Khám tổng quát cao cấp'),
-- Thu Cúc (hospital_id = 12)
(12, 'Khoa Nội tổng hợp', 'Điều trị nội khoa'),
(12, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(12, 'Khoa Sản', 'Chăm sóc thai sản'),
(12, 'Khoa Nhi', 'Điều trị nhi'),
(12, 'Khoa Khám bệnh', 'Khám bệnh tổng quát'),
-- Hồng Ngọc (hospital_id = 13)
(13, 'Khoa Nội tổng hợp', 'Điều trị nội khoa'),
(13, 'Khoa Ngoại tổng hợp', 'Phẫu thuật tổng quát'),
(13, 'Khoa Răng Hàm Mặt', 'Nha khoa cao cấp'),
(13, 'Khoa Da liễu', 'Điều trị da liễu thẩm mỹ'),
(13, 'Khoa Khám bệnh', 'Khám bệnh VIP');

-- Insert Admin User
INSERT INTO users (email, password, full_name, phone, role, is_active) VALUES
('admin@chrms.vn', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'Admin CHRMS', '0987654321', 'ADMIN', true);

-- Insert Test Patients
INSERT INTO users (email, password, full_name, phone, role, is_active) VALUES
('patient1@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'Nguyễn Văn An', '0912345671', 'PATIENT', true),
('patient2@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'Trần Thị Bình', '0912345672', 'PATIENT', true),
('patient3@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'Lê Văn Cường', '0912345673', 'PATIENT', true);

INSERT INTO patients (user_id, date_of_birth, gender, address, blood_type) VALUES
(2, '1990-01-15', 'MALE', '123 Hoàng Mai, Hà Nội', 'O'),
(3, '1985-05-20', 'FEMALE', '456 Cầu Giấy, Hà Nội', 'A'),
(4, '1995-08-30', 'MALE', '789 Thanh Xuân, Hà Nội', 'B');

-- Insert Test Doctors (2 per hospital)
INSERT INTO users (email, password, full_name, phone, role, is_active) VALUES
('doctor1@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'BS. Nguyễn Văn Hùng', '0981111111', 'DOCTOR', true),
('doctor2@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'BS. Trần Thị Lan', '0981111112', 'DOCTOR', true),
('doctor3@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'BS. Lê Minh Tuấn', '0981111113', 'DOCTOR', true),
('doctor4@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2', 'BS. Phạm Thị Hoa', '0981111114', 'DOCTOR', true);

INSERT INTO doctors (user_id, hospital_id, department_id, specialty, license_number, experience_years, consultation_fee) VALUES
(5, 1, 1, 'Tim mạch', 'BYT-001', 15, 500000),
(6, 1, 2, 'Thần kinh', 'BYT-002', 12, 450000),
(7, 2, 6, 'Ngoại khoa', 'BYT-003', 10, 400000),
(8, 3, 11, 'Nội khoa', 'BYT-004', 8, 350000);

-- Insert Common Medicines
INSERT INTO medicines (name, generic_name, manufacturer, dosage_form, strength, unit_price) VALUES
('Paracetamol', 'Acetaminophen', 'Traphaco', 'Viên nén', '500mg', 2000),
('Amoxicillin', 'Amoxicillin', 'Domesco', 'Viên nang', '500mg', 5000),
('Ibuprofen', 'Ibuprofen', 'Pymepharco', 'Viên nén', '400mg', 3000),
('Omeprazole', 'Omeprazole', 'DHG Pharma', 'Viên nang', '20mg', 4000),
('Aspirin', 'Acetylsalicylic acid', 'Pharbaco', 'Viên nén', '100mg', 1500),
('Metformin', 'Metformin HCl', 'Sanofi', 'Viên nén', '500mg', 3500),
('Atorvastatin', 'Atorvastatin', 'Pfizer', 'Viên nén', '10mg', 8000),
('Losartan', 'Losartan Potassium', 'Merck', 'Viên nén', '50mg', 6000),
('Cetirizine', 'Cetirizine HCl', 'Traphaco', 'Viên nén', '10mg', 2500),
('Vitamin C', 'Ascorbic acid', 'DHG Pharma', 'Viên sủi', '1000mg', 15000);

-- Note: Password for all test accounts is: password123
-- Bcrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMye/SPf8RhVnhrkkW8R0k/VCxs.E7pHqK2
