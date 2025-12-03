package com.chrms.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrescriptionCommand {
    private Long medicalRecordId;
    private List<PrescriptionItemDto> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionItemDto {
        private Long medicineId;
        private String dosage;
        private String frequency;
        private String duration;
        private Integer quantity;
        private String instructions;
    }
}

