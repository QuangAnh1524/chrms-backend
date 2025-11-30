package com.chrms.application.dto.command;

import com.chrms.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCommand {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Role role;
}
