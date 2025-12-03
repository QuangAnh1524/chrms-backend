package com.chrms.presentation.dto.response;

import com.chrms.domain.enums.RecordStatus;
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
public class MedicalRecordResponse {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long hospitalId;
    private Long appointmentId;
    private String diagnosis;
    private String treatment;
    private RecordStatus status;
    private LocalDate recordDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

