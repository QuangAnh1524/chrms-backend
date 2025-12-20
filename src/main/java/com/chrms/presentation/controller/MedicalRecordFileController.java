package com.chrms.presentation.controller;

import com.chrms.application.usecase.doctor.UploadMedicalRecordFileUseCase;
import com.chrms.domain.entity.MedicalRecordFile;
import com.chrms.domain.enums.FileType;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.MedicalRecordFileRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.RecordShareRepository;
import com.chrms.infrastructure.cache.RedisCacheService;
import com.chrms.infrastructure.security.SecurityUtils;
import com.chrms.presentation.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medical-records/files")
@RequiredArgsConstructor
@Tag(name = "Medical Record Files", description = "Medical Record File APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class MedicalRecordFileController {

    private final UploadMedicalRecordFileUseCase uploadFileUseCase;
    private final MedicalRecordFileRepository fileRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RecordShareRepository recordShareRepository;
    private final RedisCacheService cacheService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload file", description = "Upload a file for a medical record")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<FileResponse> uploadFile(
            @RequestParam("medicalRecordId") Long medicalRecordId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") FileType fileType,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        
        MedicalRecordFile savedFile = uploadFileUseCase.execute(medicalRecordId, file, fileType, userId);
        
        FileResponse response = FileResponse.builder()
                .id(savedFile.getId())
                .medicalRecordId(savedFile.getMedicalRecordId())
                .fileName(savedFile.getFileName())
                .fileType(savedFile.getFileType())
                .fileSize(savedFile.getFileSize())
                .uploadedBy(savedFile.getUploadedBy())
                .createdAt(savedFile.getCreatedAt())
                .build();
        
        return ApiResponse.success("File uploaded successfully", response);
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    @Operation(summary = "Get files by medical record", description = "Get all files for a medical record")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ApiResponse<List<FileResponse>> getFilesByMedicalRecord(@PathVariable Long medicalRecordId, HttpServletRequest httpRequest) {
        Long userId = SecurityUtils.getUserId(httpRequest);
        Role role = SecurityUtils.getUserRole(httpRequest);

        verifyAccess(medicalRecordId, userId, role);
        List<MedicalRecordFile> files = fileRepository.findByMedicalRecordId(medicalRecordId);
        
        List<FileResponse> response = files.stream()
                .map(file -> FileResponse.builder()
                        .id(file.getId())
                        .medicalRecordId(file.getMedicalRecordId())
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .fileSize(file.getFileSize())
                        .uploadedBy(file.getUploadedBy())
                        .createdAt(file.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download file", description = "Download a medical record file")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = SecurityUtils.getUserId(httpRequest);
        Role role = SecurityUtils.getUserRole(httpRequest);

        MedicalRecordFile file = fileRepository.findById(id)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("File", id));

        verifyAccess(file.getMedicalRecordId(), userId, role);
        
        File fileResource = new File(file.getFilePath());
        if (!fileResource.exists()) {
            throw new RuntimeException("File not found on disk");
        }
        
        Resource resource = new FileSystemResource(fileResource);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private void verifyAccess(Long medicalRecordId, Long userId, Role role) {
        var record = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("MedicalRecord", medicalRecordId));

        if (role == Role.ADMIN) {
            return;
        }

        if (role == Role.PATIENT) {
            Long patientId = patientRepository.findByUserId(userId)
                    .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("Patient profile not found for user: " + userId))
                    .getId();
            if (!record.getPatientId().equals(patientId)) {
                throw new UnauthorizedException("Bạn chỉ được tải xuống tệp của hồ sơ mình");
            }
            return;
        }

        if (role == Role.DOCTOR) {
            var doctor = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("Doctor profile not found for user: " + userId));
            if (record.getDoctorId().equals(doctor.getId())) {
                return;
            }

            Long hospitalId = doctor.getHospitalId();
            String accessKey = RedisCacheService.CACHE_RECORD_SHARE_ACCESS_PREFIX + hospitalId + ":" + medicalRecordId;
            Object cachedAccess = cacheService.get(accessKey);
            if (cachedAccess instanceof Boolean cached && cached) {
                return;
            }

            boolean hasShareAccess = recordShareRepository.findByMedicalRecordId(medicalRecordId).stream()
                    .anyMatch(share -> share.getToHospitalId().equals(hospitalId)
                            && (share.getExpiryDate() == null || share.getExpiryDate().isAfter(java.time.LocalDateTime.now())));
            if (hasShareAccess) {
                cacheService.set(accessKey, true, 5, java.util.concurrent.TimeUnit.MINUTES);
                return;
            }

            throw new UnauthorizedException("Bác sĩ không có quyền truy cập hồ sơ này");
        }

        throw new UnauthorizedException("Không có quyền truy cập tệp hồ sơ này");
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class FileResponse {
        private Long id;
        private Long medicalRecordId;
        private String fileName;
        private FileType fileType;
        private Long fileSize;
        private Long uploadedBy;
        private java.time.LocalDateTime createdAt;
    }
}
