package com.chrms.domain.entity;

import com.chrms.domain.enums.HospitalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private HospitalType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
