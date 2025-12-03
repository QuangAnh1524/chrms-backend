package com.chrms.domain.repository;

import com.chrms.domain.entity.DoctorSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository {
    DoctorSchedule save(DoctorSchedule schedule);
    Optional<DoctorSchedule> findById(Long id);
    List<DoctorSchedule> findByDoctorId(Long doctorId);
    List<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, Integer dayOfWeek);
    List<DoctorSchedule> findByDoctorIdAndIsAvailable(Long doctorId, Boolean isAvailable);
    void deleteById(Long id);
    
    // Check if doctor is available at specific date and time
    boolean isDoctorAvailableAt(Long doctorId, LocalDate date, LocalTime time);
}

