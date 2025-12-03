package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.PrescriptionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionItemJpaRepository extends JpaRepository<PrescriptionItemJpaEntity, Long> {
    List<PrescriptionItemJpaEntity> findByPrescriptionId(Long prescriptionId);
    void deleteByPrescriptionId(Long prescriptionId);
}

