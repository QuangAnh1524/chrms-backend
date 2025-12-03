package com.chrms.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalRecordCommand {
    private Long appointmentId;
    private String diagnosis;
    private String treatment;
    private String notes;
}

