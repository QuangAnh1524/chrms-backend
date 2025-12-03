package com.chrms.domain.entity;

import com.chrms.domain.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordFile {
    private Long id;
    private Long medicalRecordId;
    private String fileName;
    private String filePath;
    private FileType fileType;
    private Long fileSize;
    private Long uploadedBy;
    private LocalDateTime createdAt;
}

