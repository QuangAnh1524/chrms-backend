package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.ChatMessage;
import com.chrms.domain.repository.ChatMessageRepository;
import com.chrms.infrastructure.persistence.entity.ChatMessageJpaEntity;
import com.chrms.infrastructure.persistence.repository.ChatMessageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatMessageRepositoryAdapter implements ChatMessageRepository {

    private final ChatMessageJpaRepository jpaRepository;

    @Override
    public ChatMessage save(ChatMessage message) {
        ChatMessageJpaEntity entity = toEntity(message);
        ChatMessageJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<ChatMessage> findByAppointmentId(Long appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findByAppointmentIdAndCreatedAtAfter(Long appointmentId, LocalDateTime after) {
        return jpaRepository.findByAppointmentIdAndCreatedAtAfter(appointmentId, after).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findUnreadByAppointmentId(Long appointmentId, Long userId) {
        return jpaRepository.findUnreadByAppointmentIdAndUserId(appointmentId, userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long messageId) {
        jpaRepository.markAsRead(messageId);
    }

    private ChatMessage toDomain(ChatMessageJpaEntity entity) {
        return ChatMessage.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointmentId())
                .senderId(entity.getSenderId())
                .message(entity.getMessage())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private ChatMessageJpaEntity toEntity(ChatMessage domain) {
        return ChatMessageJpaEntity.builder()
                .id(domain.getId())
                .appointmentId(domain.getAppointmentId())
                .senderId(domain.getSenderId())
                .message(domain.getMessage())
                .isRead(domain.getIsRead())
                .build();
    }
}

