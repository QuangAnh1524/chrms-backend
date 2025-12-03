package com.chrms.application.usecase.shared;

import com.chrms.domain.entity.ChatMessage;
import com.chrms.domain.repository.ChatMessageRepository;
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

    public List<ChatMessage> execute(Long appointmentId, LocalDateTime after) {
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
        // Don't cache unread messages - always get fresh
        return messageRepository.findUnreadByAppointmentId(appointmentId, userId);
    }
}

