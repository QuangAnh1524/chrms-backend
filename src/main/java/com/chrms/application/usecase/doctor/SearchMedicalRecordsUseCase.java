package com.chrms.application.usecase.doctor;

import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.infrastructure.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchMedicalRecordsUseCase {
    private final MedicalRecordRepository recordRepository;

    @Cacheable(value = "medicalRecords", key = "#symptoms + ':' + #diagnosis")
    public List<MedicalRecord> searchBySymptoms(String symptoms) {
        return recordRepository.searchBySymptoms(symptoms);
    }

    @Cacheable(value = "medicalRecords", key = "'diagnosis:' + #diagnosis")
    public List<MedicalRecord> searchByDiagnosis(String diagnosis) {
        return recordRepository.searchByDiagnosis(diagnosis);
    }
}

