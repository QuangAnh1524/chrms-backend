package com.chrms.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentCommand {
    private Long patientId;
    private Long doctorId;
    private Long hospitalId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String symptoms;
    private String notes;
}