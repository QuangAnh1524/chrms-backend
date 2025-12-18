package com.chrms.application.usecase.doctor;

import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.entity.MedicalRecordFile;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.FileType;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.MedicalRecordFileRepository;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadMedicalRecordFileUseCase {
    private final MedicalRecordFileRepository fileRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    public MedicalRecordFile execute(Long medicalRecordId, MultipartFile file, FileType fileType, Long uploadedBy) {
        // Validate medical record exists
        MedicalRecord record = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord", medicalRecordId));

        User actor = userRepository.findById(uploadedBy)
                .orElseThrow(() -> new EntityNotFoundException("User", uploadedBy));

        if (actor.getRole() != Role.DOCTOR && actor.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Chỉ bác sĩ mới được tải tệp hồ sơ");
        }

        if (actor.getRole() == Role.DOCTOR) {
            Long doctorId = doctorRepository.findByUserId(uploadedBy)
                    .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + uploadedBy))
                    .getId();

            appointmentRepository.findById(record.getAppointmentId())
                    .filter(appt -> appt.getDoctorId().equals(doctorId))
                    .orElseThrow(() -> new UnauthorizedException("Bác sĩ không phụ trách hồ sơ này"));
        }

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory", e);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Save file
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        // Save file record
        MedicalRecordFile recordFile = MedicalRecordFile.builder()
                .medicalRecordId(medicalRecordId)
                .fileName(originalFilename)
                .filePath(filePath.toString())
                .fileType(fileType)
                .fileSize(file.getSize())
                .uploadedBy(uploadedBy)
                .build();

        return fileRepository.save(recordFile);
    }
}

