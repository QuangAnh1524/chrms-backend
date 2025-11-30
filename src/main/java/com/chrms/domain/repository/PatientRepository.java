package com.chrms.domain.repository;

import com.chrms.domain.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(Long id);
    Optional<Patient> findByUserId(Long userId);
    List<Patient> findAll();
    void deleteById(Long id);
}
