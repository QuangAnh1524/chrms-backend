package com.chrms.infrastructure.persistence.repository;

import com.chrms.domain.enums.FileType;
import com.chrms.infrastructure.persistence.entity.MedicalRecordFileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordFileJpaRepository extends JpaRepository<MedicalRecordFileJpaEntity, Long> {
    List<MedicalRecordFileJpaEntity> findByMedicalRecordId(Long medicalRecordId);
    List<MedicalRecordFileJpaEntity> findByMedicalRecordIdAndFileType(Long medicalRecordId, FileType fileType);
}

