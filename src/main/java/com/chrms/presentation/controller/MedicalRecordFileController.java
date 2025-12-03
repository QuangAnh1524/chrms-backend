package com.chrms.presentation.controller;

import com.chrms.application.usecase.doctor.UploadMedicalRecordFileUseCase;
import com.chrms.domain.entity.MedicalRecordFile;
import com.chrms.domain.enums.FileType;
import com.chrms.domain.repository.MedicalRecordFileRepository;
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

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload file", description = "Upload a file for a medical record")
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
    public ApiResponse<List<FileResponse>> getFilesByMedicalRecord(@PathVariable Long medicalRecordId) {
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
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        MedicalRecordFile file = fileRepository.findById(id)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("File", id));
        
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

