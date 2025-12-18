package com.chrms.application.usecase.shared;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.ChatMessage;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.Role;
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

@Service
@RequiredArgsConstructor
public class SendChatMessageUseCase {
    private final ChatMessageRepository messageRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessage execute(Long appointmentId, Long senderId, String message) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

        User actor = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User", senderId));

        validateParticipant(appointment, actor);

        ChatMessage chatMessage = ChatMessage.builder()
                .appointmentId(appointmentId)
                .senderId(senderId)
                .message(message)
                .isRead(false)
                .build();

        return messageRepository.save(chatMessage);
    }

    private void validateParticipant(Appointment appointment, User actor) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }

        if (actor.getRole() == Role.PATIENT) {
            Long patientId = patientRepository.findByUserId(actor.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + actor.getId()))
                    .getId();
            if (!appointment.getPatientId().equals(patientId)) {
                throw new UnauthorizedException("Bạn không thuộc cuộc hẹn này");
            }
            return;
        }

        if (actor.getRole() == Role.DOCTOR) {
            Long doctorId = doctorRepository.findByUserId(actor.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + actor.getId()))
                    .getId();
            if (!appointment.getDoctorId().equals(doctorId)) {
                throw new UnauthorizedException("Bác sĩ không phụ trách lịch hẹn này");
            }
            return;
        }

        throw new UnauthorizedException("Không có quyền tham gia cuộc trò chuyện");
    }
}

