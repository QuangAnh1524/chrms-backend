package com.chrms.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    private Long id;
    private Long medicalRecordId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Prescription items (loaded separately)
    private List<PrescriptionItem> items;
}

