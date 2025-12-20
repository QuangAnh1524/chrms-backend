package com.chrms.domain.repository;

import com.chrms.domain.entity.RecordShare;

import java.util.List;
import java.util.Optional;

public interface RecordShareRepository {
    RecordShare save(RecordShare share);
    Optional<RecordShare> findById(Long id);
    List<RecordShare> findByMedicalRecordId(Long medicalRecordId);
    List<RecordShare> findByToHospitalId(Long hospitalId);
    List<RecordShare> findBySharedBy(Long userId);
    void deleteById(Long id);
}
