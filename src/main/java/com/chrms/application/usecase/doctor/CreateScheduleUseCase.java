package com.chrms.application.usecase.doctor;

import com.chrms.application.dto.command.CreateScheduleCommand;
import com.chrms.application.dto.result.ScheduleResult;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.DoctorSchedule;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateScheduleUseCase {
    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public ScheduleResult execute(CreateScheduleCommand command) {
        // Validate doctor exists
        doctorRepository.findById(command.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor", command.getDoctorId()));

        // Validate day of week (1-7)
        if (command.getDayOfWeek() < 1 || command.getDayOfWeek() > 7) {
            throw new BusinessRuleViolationException("Day of week must be between 1 (Monday) and 7 (Sunday)");
        }

        // Validate time range
        if (command.getStartTime().isAfter(command.getEndTime()) || 
            command.getStartTime().equals(command.getEndTime())) {
            throw new BusinessRuleViolationException("Start time must be before end time");
        }

        // Check for overlapping schedules
        List<DoctorSchedule> existingSchedules = scheduleRepository
                .findByDoctorIdAndDayOfWeek(command.getDoctorId(), command.getDayOfWeek());
        
        boolean hasOverlap = existingSchedules.stream()
                .anyMatch(schedule -> {
                    if (!schedule.getIsAvailable()) return false;
                    return (command.getStartTime().isBefore(schedule.getEndTime()) &&
                            command.getEndTime().isAfter(schedule.getStartTime()));
                });

        if (hasOverlap) {
            throw new BusinessRuleViolationException("Schedule overlaps with existing schedule");
        }

        // Create schedule
        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctorId(command.getDoctorId())
                .dayOfWeek(command.getDayOfWeek())
                .startTime(command.getStartTime())
                .endTime(command.getEndTime())
                .isAvailable(command.getIsAvailable() != null ? command.getIsAvailable() : true)
                .build();

        DoctorSchedule saved = scheduleRepository.save(schedule);

        return ScheduleResult.builder()
                .id(saved.getId())
                .doctorId(saved.getDoctorId())
                .dayOfWeek(saved.getDayOfWeek())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .isAvailable(saved.getIsAvailable())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}

