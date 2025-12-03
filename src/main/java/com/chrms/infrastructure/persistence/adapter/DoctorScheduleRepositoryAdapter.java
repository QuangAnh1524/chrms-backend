package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.DoctorSchedule;
import com.chrms.domain.repository.DoctorScheduleRepository;
import com.chrms.infrastructure.persistence.entity.DoctorScheduleJpaEntity;
import com.chrms.infrastructure.persistence.repository.AppointmentJpaRepository;
import com.chrms.infrastructure.persistence.repository.DoctorScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DoctorScheduleRepositoryAdapter implements DoctorScheduleRepository {

    private final DoctorScheduleJpaRepository jpaRepository;
    private final AppointmentJpaRepository appointmentJpaRepository;

    @Override
    public DoctorSchedule save(DoctorSchedule schedule) {
        DoctorScheduleJpaEntity entity = toEntity(schedule);
        DoctorScheduleJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<DoctorSchedule> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<DoctorSchedule> findByDoctorId(Long doctorId) {
        return jpaRepository.findByDoctorId(doctorId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, Integer dayOfWeek) {
        return jpaRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorSchedule> findByDoctorIdAndIsAvailable(Long doctorId, Boolean isAvailable) {
        return jpaRepository.findByDoctorIdAndIsAvailable(doctorId, isAvailable).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean isDoctorAvailableAt(Long doctorId, LocalDate date, LocalTime time) {
        // Get day of week (1=Monday, 7=Sunday)
        DayOfWeek dayOfWeekEnum = date.getDayOfWeek();
        int dayOfWeek = dayOfWeekEnum.getValue(); // Monday=1, Sunday=7
        
        // Check if doctor has schedule for this day and time
        Optional<DoctorScheduleJpaEntity> schedule = jpaRepository.findAvailableSchedule(
            doctorId, dayOfWeek, time
        );
        
        if (schedule.isEmpty()) {
            return false;
        }
        
        // Check if doctor already has appointment at this time
        long existingAppointments = appointmentJpaRepository
            .findByDoctorIdAndAppointmentDate(doctorId, date)
            .stream()
            .filter(apt -> apt.getAppointmentTime().equals(time) 
                    && !apt.getStatus().name().equals("CANCELLED"))
            .count();
        
        return existingAppointments == 0;
    }

    private DoctorSchedule toDomain(DoctorScheduleJpaEntity entity) {
        return DoctorSchedule.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctorId())
                .dayOfWeek(entity.getDayOfWeek())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .isAvailable(entity.getIsAvailable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private DoctorScheduleJpaEntity toEntity(DoctorSchedule domain) {
        return DoctorScheduleJpaEntity.builder()
                .id(domain.getId())
                .doctorId(domain.getDoctorId())
                .dayOfWeek(domain.getDayOfWeek())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .isAvailable(domain.getIsAvailable())
                .build();
    }
}

