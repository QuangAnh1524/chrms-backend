package com.chrms.application.dto.result;

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
public class PrescriptionResult {
    private Long id;
    private Long medicalRecordId;
    private List<PrescriptionItemResult> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionItemResult {
        private Long id;
        private Long medicineId;
        private String medicineName;
        private String dosage;
        private String frequency;
        private String duration;
        private Integer quantity;
        private String instructions;
    }
}

