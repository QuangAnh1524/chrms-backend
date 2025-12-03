package com.chrms.presentation.controller;

import com.chrms.application.usecase.shared.GetChatMessagesUseCase;
import com.chrms.application.usecase.shared.SendChatMessageUseCase;
import com.chrms.domain.entity.ChatMessage;
import com.chrms.presentation.dto.request.SendMessageRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.ChatMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat Message APIs (Polling-based)")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

    private final SendChatMessageUseCase sendMessageUseCase;
    private final GetChatMessagesUseCase getMessagesUseCase;

    @PostMapping("/appointments/{appointmentId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send message", description = "Send a chat message for an appointment")
    public ApiResponse<ChatMessageResponse> sendMessage(
            @PathVariable Long appointmentId,
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        ChatMessage message = sendMessageUseCase.execute(appointmentId, userId, request.getMessage());
        
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(message.getId())
                .appointmentId(message.getAppointmentId())
                .senderId(message.getSenderId())
                .message(message.getMessage())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
        
        return ApiResponse.success("Message sent successfully", response);
    }

    @GetMapping("/appointments/{appointmentId}/messages")
    @Operation(summary = "Get messages", description = "Get all messages for an appointment (polling endpoint)")
    public ApiResponse<List<ChatMessageResponse>> getMessages(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after) {
        
        List<ChatMessage> messages = getMessagesUseCase.execute(appointmentId, after);
        
        List<ChatMessageResponse> response = messages.stream()
                .map(message -> ChatMessageResponse.builder()
                        .id(message.getId())
                        .appointmentId(message.getAppointmentId())
                        .senderId(message.getSenderId())
                        .message(message.getMessage())
                        .isRead(message.getIsRead())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return ApiResponse.success(response);
    }

    @GetMapping("/appointments/{appointmentId}/messages/unread")
    @Operation(summary = "Get unread messages", description = "Get unread messages for an appointment (polling endpoint)")
    public ApiResponse<List<ChatMessageResponse>> getUnreadMessages(
            @PathVariable Long appointmentId,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        List<ChatMessage> messages = getMessagesUseCase.getUnreadMessages(appointmentId, userId);
        
        List<ChatMessageResponse> response = messages.stream()
                .map(message -> ChatMessageResponse.builder()
                        .id(message.getId())
                        .appointmentId(message.getAppointmentId())
                        .senderId(message.getSenderId())
                        .message(message.getMessage())
                        .isRead(message.getIsRead())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return ApiResponse.success(response);
    }
}

