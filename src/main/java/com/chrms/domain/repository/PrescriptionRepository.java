package com.chrms.domain.repository;

import com.chrms.domain.entity.Prescription;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(Long id);
    Optional<Prescription> findByMedicalRecordId(Long medicalRecordId);
    List<Prescription> findAll();
    void deleteById(Long id);
}

