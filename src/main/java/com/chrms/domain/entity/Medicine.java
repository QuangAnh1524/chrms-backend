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
public class Medicine {
    private Long id;
    private String name;
    private String genericName;
    private String manufacturer;
    private String dosageForm;
    private String strength;
    private String description;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}