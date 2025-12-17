package com.chrms.application.dto.result;

import com.chrms.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileResult {
    private Long patientId;
    private Long userId;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String emergencyContact;
    private String bloodType;
    private String allergies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
