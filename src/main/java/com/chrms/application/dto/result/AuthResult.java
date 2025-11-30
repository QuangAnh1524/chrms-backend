package com.chrms.application.dto.result;

import com.chrms.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
}
