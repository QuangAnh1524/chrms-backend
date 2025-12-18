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
public class ApproveMedicalRecordUseCase {
    private final MedicalRecordRepository recordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public MedicalRecord execute(Long recordId, Long actorUserId) {
        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord", recordId));

        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", actorUserId));

        if (actor.getRole() != com.chrms.domain.enums.Role.DOCTOR) {
            throw new UnauthorizedException("Only doctors can approve medical records");
        }

        Long doctorId = doctorRepository.findByUserId(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + actorUserId))
                .getId();

        appointmentRepository.findById(record.getAppointmentId())
                .filter(appt -> appt.getDoctorId().equals(doctorId))
                .orElseThrow(() -> new UnauthorizedException("Doctor is not assigned to this appointment"));

        // Validate status transition
        if (record.getStatus() != RecordStatus.DRAFT) {
            throw new BusinessRuleViolationException("Only draft medical records can be approved");
        }

        // Update status
        record.setStatus(RecordStatus.APPROVED);
        return recordRepository.save(record);
    }
}

