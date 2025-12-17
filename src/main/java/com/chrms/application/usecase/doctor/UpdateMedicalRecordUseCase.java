package com.chrms.application.usecase.doctor;

import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.enums.RecordStatus;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMedicalRecordUseCase {

    private final MedicalRecordRepository recordRepository;

    @Transactional
    public MedicalRecord execute(Long recordId, MedicalRecord updates) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord", recordId));

        if (record.getStatus() != RecordStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only draft medical records can be updated");
        }

        if (updates.getSymptoms() != null) {
            record.setSymptoms(updates.getSymptoms());
        }
        if (updates.getDiagnosis() != null) {
            record.setDiagnosis(updates.getDiagnosis());
        }
        if (updates.getTreatment() != null) {
            record.setTreatment(updates.getTreatment());
        }
        if (updates.getNotes() != null) {
            record.setNotes(updates.getNotes());
        }

        return recordRepository.save(record);
    }
}
