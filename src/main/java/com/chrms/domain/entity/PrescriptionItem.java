package com.chrms.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private Long medicineId;
    private String dosage;
    private String frequency;
    private String duration;
    private Integer quantity;
    private String instructions;
    private LocalDateTime createdAt;
    
    // Medicine details (loaded separately)
    private Medicine medicine;
}

