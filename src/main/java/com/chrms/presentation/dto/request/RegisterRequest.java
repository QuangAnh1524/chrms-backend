package com.chrms.presentation.dto.request;

import com.chrms.domain.enums.Gender;
import com.chrms.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @NotNull(message = "Role is required")
    private Role role;

    // Patient-specific fields (required when role is PATIENT)
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String emergencyContact;
    private String bloodType;
    private String allergies;

    // Doctor-specific fields (required when role is DOCTOR)
    private Long hospitalId;
    private Long departmentId;
    private String specialty;
    private String licenseNumber;
    private Integer experienceYears;
    private java.math.BigDecimal consultationFee;
}
