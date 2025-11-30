package com.chrms.presentation.controller;

import com.chrms.application.dto.result.AuthResult;
import com.chrms.application.usecase.auth.LoginUseCase;
import com.chrms.application.usecase.auth.RegisterUseCase;
import com.chrms.infrastructure.security.jwt.JwtTokenProvider;
import com.chrms.presentation.dto.request.LoginRequest;
import com.chrms.presentation.dto.request.RegisterRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.AuthResponse;
import com.chrms.presentation.mapper.AuthMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthMapper authMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register new user", description = "Register a new user account")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = registerUseCase.execute(authMapper.toCommand(request));
        String token = jwtTokenProvider.generateToken(
                result.getUserId(),
                result.getEmail(),
                result.getRole().name()
        );
        return ApiResponse.success("Registration successful", authMapper.toResponse(result, token));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = loginUseCase.execute(authMapper.toCommand(request));
        String token = jwtTokenProvider.generateToken(
                result.getUserId(),
                result.getEmail(),
                result.getRole().name()
        );
        return ApiResponse.success("Login successful", authMapper.toResponse(result, token));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user and invalidate token")
    public ApiResponse<Void> logout() {
        // TODO: Implement JWT blacklist with Redis
        return ApiResponse.success("Logout successful", null);
    }
}
