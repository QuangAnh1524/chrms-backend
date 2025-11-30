package com.chrms.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private Long id;
    private Long userId;
    private Long hospitalId;
    private Long departmentId;
    private String specialty;
    private String licenseNumber;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
