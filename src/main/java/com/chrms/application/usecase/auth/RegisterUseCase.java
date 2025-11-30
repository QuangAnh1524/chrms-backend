package com.chrms.application.usecase.auth;

import com.chrms.application.dto.command.RegisterCommand;
import com.chrms.application.dto.result.AuthResult;
import com.chrms.domain.entity.User;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResult execute(RegisterCommand command) {
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessRuleViolationException("Email already exists");
        }

        User user = User.builder()
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .fullName(command.getFullName())
                .phone(command.getPhone())
                .role(command.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        return AuthResult.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .build();
    }
}
