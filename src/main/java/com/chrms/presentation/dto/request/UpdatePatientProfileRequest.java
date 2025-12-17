package com.chrms.presentation.dto.request;

import com.chrms.domain.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientProfileRequest {
    private String fullName;
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private Gender gender;
    private String address;
    private String emergencyContact;
    private String bloodType;
    private String allergies;
}
