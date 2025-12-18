package com.chrms.presentation.controller;

import com.chrms.application.dto.result.AuthResult;
import com.chrms.application.usecase.auth.LoginUseCase;
import com.chrms.application.usecase.auth.RegisterUseCase;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.UserRepository;
import com.chrms.infrastructure.cache.RedisCacheService;
import com.chrms.infrastructure.security.jwt.JwtTokenProvider;
import com.chrms.presentation.dto.request.LoginRequest;
import com.chrms.presentation.dto.request.RegisterRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.AuthResponse;
import com.chrms.presentation.mapper.AuthMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RedisCacheService cacheService;
    private final UserRepository userRepository;

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
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            // Get expiration time from token
            java.util.Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);
            long expirationTime = expiration.getTime();
            
            // Blacklist token until expiration
            cacheService.blacklistToken(token, expirationTime);
        }
        return ApiResponse.success("Logout successful", null);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh an existing JWT token")
    public ApiResponse<AuthResponse> refreshToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Token không hợp lệ hoặc đã hết hạn");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String email = jwtTokenProvider.getEmailFromToken(token);
        String role = jwtTokenProvider.getRoleFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Không tìm thấy thông tin người dùng"));

        String refreshedToken = jwtTokenProvider.generateToken(userId, email, role);

        AuthResult result = AuthResult.builder()
                .userId(userId)
                .email(email)
                .fullName(user.getFullName())
                .role(Role.valueOf(role))
                .build();

        return ApiResponse.success("Làm mới token thành công", authMapper.toResponse(result, refreshedToken));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
