package com.chrms.domain.repository;

import com.chrms.domain.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage message);
    List<ChatMessage> findByAppointmentId(Long appointmentId);
    List<ChatMessage> findByAppointmentIdAndCreatedAtAfter(Long appointmentId, LocalDateTime after);
    List<ChatMessage> findUnreadByAppointmentId(Long appointmentId, Long userId);
    void markAsRead(Long messageId);
}

