package com.chrms.application.dto.command;

import com.chrms.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientProfileCommand {
    private Long userId;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String emergencyContact;
    private String bloodType;
    private String allergies;
}
