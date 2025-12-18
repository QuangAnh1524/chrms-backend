package com.chrms.application.usecase.doctor;

import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.RecordStatus;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMedicalRecordUseCase {

    private final MedicalRecordRepository recordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public MedicalRecord execute(Long recordId, MedicalRecord updates, Long actorUserId) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord", recordId));

        validateOwnership(record, actorUserId);

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

    private void validateOwnership(MedicalRecord record, Long actorUserId) {
        if (record.getStatus() != RecordStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only draft medical records can be updated");
        }

        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", actorUserId));

        if (actor.getRole() != com.chrms.domain.enums.Role.DOCTOR) {
            throw new UnauthorizedException("Only doctors can update medical records");
        }

        Long doctorId = doctorRepository.findByUserId(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + actorUserId))
                .getId();

        appointmentRepository.findById(record.getAppointmentId())
                .filter(appt -> appt.getDoctorId().equals(doctorId))
                .orElseThrow(() -> new UnauthorizedException("Doctor is not assigned to this appointment"));
    }
}
