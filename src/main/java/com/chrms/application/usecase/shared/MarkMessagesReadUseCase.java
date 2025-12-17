package com.chrms.application.usecase.shared;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.ChatMessageRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MarkMessagesReadUseCase {

    private final ChatMessageRepository messageRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(Long appointmentId, Long userId, Long upToMessageId, LocalDateTime upToDatetime) {
        if (upToMessageId == null && upToDatetime == null) {
            throw new BusinessRuleViolationException("Either upToMessageId or upToDatetime must be provided");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

        User actor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if (actor.getRole() == Role.PATIENT) {
            Patient patient = patientRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + userId));
            if (!appointment.getPatientId().equals(patient.getId())) {
                throw new UnauthorizedException("Patient is not authorized for this conversation");
            }
        } else if (actor.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + userId));
            if (!appointment.getDoctorId().equals(doctor.getId())) {
                throw new UnauthorizedException("Doctor is not assigned to this appointment");
            }
        } else if (actor.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("User role is not permitted to update read status");
        }

        messageRepository.markAsReadUpTo(appointmentId, userId, upToMessageId, upToDatetime);
    }
}
