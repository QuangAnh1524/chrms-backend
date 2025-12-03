package com.chrms.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private Long appointmentId;
    private Long senderId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

