package com.chrms.application.usecase.shared;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.ChatMessage;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SendChatMessageUseCase {
    private final ChatMessageRepository messageRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public ChatMessage execute(Long appointmentId, Long senderId, String message) {
        // Validate appointment exists
        appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

        ChatMessage chatMessage = ChatMessage.builder()
                .appointmentId(appointmentId)
                .senderId(senderId)
                .message(message)
                .isRead(false)
                .build();

        return messageRepository.save(chatMessage);
    }
}

