package com.chrms.domain.repository;

import com.chrms.domain.entity.MedicalRecordFile;
import com.chrms.domain.enums.FileType;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordFileRepository {
    MedicalRecordFile save(MedicalRecordFile file);
    Optional<MedicalRecordFile> findById(Long id);
    List<MedicalRecordFile> findByMedicalRecordId(Long medicalRecordId);
    List<MedicalRecordFile> findByMedicalRecordIdAndFileType(Long medicalRecordId, FileType fileType);
    void deleteById(Long id);
}

