package com.chrms.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareMedicalRecordRequest {
    @NotNull
    private Long toHospitalId;
    private String notes;
    private LocalDateTime expiryDate;
}

