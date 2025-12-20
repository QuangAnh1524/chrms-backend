package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.RecordShareJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordShareJpaRepository extends JpaRepository<RecordShareJpaEntity, Long> {
    List<RecordShareJpaEntity> findByMedicalRecordId(Long medicalRecordId);
    List<RecordShareJpaEntity> findByToHospitalId(Long hospitalId);
    List<RecordShareJpaEntity> findBySharedBy(Long userId);
}

