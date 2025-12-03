package com.chrms.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrescriptionRequest {
    @NotNull(message = "Medical record ID is required")
    private Long medicalRecordId;

    @NotEmpty(message = "Prescription items are required")
    @Valid
    private List<PrescriptionItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionItemRequest {
        @NotNull(message = "Medicine ID is required")
        private Long medicineId;

        @NotNull(message = "Dosage is required")
        private String dosage;

        @NotNull(message = "Frequency is required")
        private String frequency;

        @NotNull(message = "Duration is required")
        private String duration;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private String instructions;
    }
}

