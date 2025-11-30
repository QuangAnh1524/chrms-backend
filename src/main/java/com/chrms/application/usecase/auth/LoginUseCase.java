package com.chrms.application.usecase.auth;

import com.chrms.application.dto.command.LoginCommand;
import com.chrms.application.dto.result.AuthResult;
import com.chrms.domain.entity.User;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthResult execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is inactive");
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return AuthResult.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
