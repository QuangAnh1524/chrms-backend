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
public class ApproveMedicalRecordUseCase {
    private final MedicalRecordRepository recordRepository;

    @Transactional
    public MedicalRecord execute(Long recordId) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord", recordId));

        // Validate status transition
        if (record.getStatus() == RecordStatus.APPROVED) {
            throw new BusinessRuleViolationException("Record is already approved");
        }

        if (record.getStatus() == RecordStatus.SHARED) {
            throw new BusinessRuleViolationException("Cannot approve a shared record");
        }

        // Update status
        record.setStatus(RecordStatus.APPROVED);
        return recordRepository.save(record);
    }
}

