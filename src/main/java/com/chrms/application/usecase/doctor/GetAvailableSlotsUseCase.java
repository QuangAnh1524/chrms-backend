package com.chrms.application.usecase.doctor;

import com.chrms.domain.entity.DoctorSchedule;
import com.chrms.domain.repository.DoctorScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAvailableSlotsUseCase {
    private final DoctorScheduleRepository scheduleRepository;

    public List<TimeSlotResult> execute(Long doctorId, LocalDate date) {
        DayOfWeek dayOfWeekEnum = date.getDayOfWeek();
        int dayOfWeek = dayOfWeekEnum.getValue(); // Monday=1, Sunday=7

        List<DoctorSchedule> schedules = scheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .stream()
                .filter(s -> s.getIsAvailable() != null && s.getIsAvailable())
                .collect(Collectors.toList());

        List<TimeSlotResult> slots = new ArrayList<>();
        
        // Generate 30-minute slots for each schedule
        for (DoctorSchedule schedule : schedules) {
            LocalTime current = schedule.getStartTime();
            while (current.isBefore(schedule.getEndTime())) {
                LocalTime endSlot = current.plusMinutes(30);
                if (endSlot.isAfter(schedule.getEndTime())) {
                    break;
                }
                
                // Check if doctor is available at this time
                boolean available = scheduleRepository.isDoctorAvailableAt(doctorId, date, current);
                
                slots.add(TimeSlotResult.builder()
                        .time(current)
                        .available(available)
                        .build());
                
                current = endSlot;
            }
        }

        return slots;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class TimeSlotResult {
        private LocalTime time;
        private Boolean available;
    }
}

