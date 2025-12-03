package com.chrms.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

