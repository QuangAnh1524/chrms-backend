package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.PrescriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionJpaRepository extends JpaRepository<PrescriptionJpaEntity, Long> {
    Optional<PrescriptionJpaEntity> findByMedicalRecordId(Long medicalRecordId);
}

