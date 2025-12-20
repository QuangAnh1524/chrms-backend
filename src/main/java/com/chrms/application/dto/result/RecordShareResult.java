package com.chrms.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordShareResult {
    private Long id;
    private Long medicalRecordId;
    private Long fromHospitalId;
    private Long toHospitalId;
    private Long sharedBy;
    private LocalDateTime sharedAt;
    private LocalDateTime expiryDate;
    private String notes;
}

