package com.chrms.domain.repository;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.enums.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findAll();
    void deleteById(Long id);
}
