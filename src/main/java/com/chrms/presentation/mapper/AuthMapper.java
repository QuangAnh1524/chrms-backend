package com.chrms.presentation.mapper;

import com.chrms.application.dto.command.LoginCommand;
import com.chrms.application.dto.command.RegisterCommand;
import com.chrms.application.dto.result.AuthResult;
import com.chrms.presentation.dto.request.LoginRequest;
import com.chrms.presentation.dto.request.RegisterRequest;
import com.chrms.presentation.dto.response.AuthResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    
    public RegisterCommand toCommand(RegisterRequest request) {
        return RegisterCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
    }

    public LoginCommand toCommand(LoginRequest request) {
        return LoginCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public AuthResponse toResponse(AuthResult result, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(result.getUserId())
                .email(result.getEmail())
                .fullName(result.getFullName())
                .role(result.getRole())
                .build();
    }
}
