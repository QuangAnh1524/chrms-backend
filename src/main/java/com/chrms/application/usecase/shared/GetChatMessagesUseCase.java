package com.chrms.application.usecase.shared;

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
import com.chrms.infrastructure.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetChatMessagesUseCase {
    private final ChatMessageRepository messageRepository;
    private final RedisCacheService cacheService;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public List<ChatMessage> execute(Long appointmentId, LocalDateTime after, Long userId) {
        User actor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        validateAccess(appointmentId, actor);

        // Cache recent messages (last 50) for quick access
        String cacheKey = RedisCacheService.CACHE_CHAT_PREFIX + appointmentId;

        if (after != null) {
            // For polling, don't cache - always get fresh data
            return messageRepository.findByAppointmentIdAndCreatedAtAfter(appointmentId, after);
        }
        
        // Check cache first
        @SuppressWarnings("unchecked")
        List<ChatMessage> cached = (List<ChatMessage>) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // Get from DB and cache
        List<ChatMessage> messages = messageRepository.findByAppointmentId(appointmentId);
        if (messages.size() <= 50) {
            cacheService.set(cacheKey, messages, 5, java.util.concurrent.TimeUnit.MINUTES);
        }
        
        return messages;
    }

    public List<ChatMessage> getUnreadMessages(Long appointmentId, Long userId) {
        User actor = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        validateAccess(appointmentId, actor);

        // Don't cache unread messages - always get fresh
        return messageRepository.findUnreadByAppointmentId(appointmentId, userId);
    }

    private void validateAccess(Long appointmentId, User actor) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }

        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

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

        throw new UnauthorizedException("Không có quyền truy cập cuộc trò chuyện này");
    }
}

