package com.chrms.domain.entity;

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
public class Patient {
    private Long id;
    private Long userId;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String emergencyContact;
    private String bloodType;
    private String allergies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
