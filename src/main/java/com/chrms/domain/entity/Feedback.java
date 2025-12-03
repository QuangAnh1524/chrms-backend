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
public class Feedback {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private Integer rating; // 1-5
    private String comment;
    private LocalDateTime createdAt;
}

