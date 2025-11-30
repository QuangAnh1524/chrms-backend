package com.chrms.domain.entity;

import com.chrms.domain.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long hospitalId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private String symptoms;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
