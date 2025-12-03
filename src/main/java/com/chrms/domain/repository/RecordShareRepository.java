package com.chrms.domain.repository;

import com.chrms.domain.entity.RecordShare;

import java.util.List;

public interface RecordShareRepository {
    RecordShare save(RecordShare share);
    List<RecordShare> findByMedicalRecordId(Long medicalRecordId);
    List<RecordShare> findByToHospitalId(Long hospitalId);
}

