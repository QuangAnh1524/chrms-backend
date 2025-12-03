package com.chrms.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleCommand {
    private Long doctorId;
    private Integer dayOfWeek; // 1=Monday, 7=Sunday
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;
}

