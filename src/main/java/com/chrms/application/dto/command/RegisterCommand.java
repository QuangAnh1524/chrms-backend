package com.chrms.application.dto.command;

import com.chrms.domain.enums.Gender;
import com.chrms.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    
    // Patient-specific fields
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String emergencyContact;
    private String bloodType;
    private String allergies;

    // Doctor-specific fields
    private Long hospitalId;
    private Long departmentId;
    private String specialty;
    private String licenseNumber;
    private Integer experienceYears;
    private java.math.BigDecimal consultationFee;
}
