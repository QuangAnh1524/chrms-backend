package com.chrms.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalRecordRequest {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    private String symptoms;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String treatment;
    private String notes;
}

