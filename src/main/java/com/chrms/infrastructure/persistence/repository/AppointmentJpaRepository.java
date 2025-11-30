package com.chrms.infrastructure.persistence.repository;

import com.chrms.domain.enums.AppointmentStatus;
import com.chrms.infrastructure.persistence.entity.AppointmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, Long> {
    List<AppointmentJpaEntity> findByPatientId(Long patientId);
    List<AppointmentJpaEntity> findByDoctorId(Long doctorId);
    List<AppointmentJpaEntity> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
    List<AppointmentJpaEntity> findByStatus(AppointmentStatus status);
}