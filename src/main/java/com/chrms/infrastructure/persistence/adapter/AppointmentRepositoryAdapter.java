package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.enums.AppointmentStatus;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.infrastructure.persistence.entity.AppointmentJpaEntity;
import com.chrms.infrastructure.persistence.repository.AppointmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AppointmentRepositoryAdapter implements AppointmentRepository {

    private final AppointmentJpaRepository jpaRepository;

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentJpaEntity entity = toEntity(appointment);
        AppointmentJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByPatientIdAndDate(Long patientId, LocalDate date) {
        return jpaRepository.findByPatientIdAndAppointmentDate(patientId, date).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByDoctorId(Long doctorId) {
        return jpaRepository.findByDoctorId(doctorId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date) {
        return jpaRepository.findByDoctorIdAndAppointmentDate(doctorId, date).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByDoctorIdAndHospitalIdAndDate(Long doctorId, Long hospitalId, LocalDate date) {
        return jpaRepository.countByDoctorIdAndHospitalIdAndAppointmentDate(doctorId, hospitalId, date);
    }

    @Override
    public List<Appointment> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Appointment toDomain(AppointmentJpaEntity entity) {
        return Appointment.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .doctorId(entity.getDoctorId())
                .hospitalId(entity.getHospitalId())
                .departmentId(entity.getDepartmentId())
                .appointmentDate(entity.getAppointmentDate())
                .appointmentTime(entity.getAppointmentTime())
                .status(entity.getStatus())
                .queueNumber(entity.getQueueNumber())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private AppointmentJpaEntity toEntity(Appointment domain) {
        return AppointmentJpaEntity.builder()
                .id(domain.getId())
                .patientId(domain.getPatientId())
                .doctorId(domain.getDoctorId())
                .hospitalId(domain.getHospitalId())
                .departmentId(domain.getDepartmentId())
                .appointmentDate(domain.getAppointmentDate())
                .appointmentTime(domain.getAppointmentTime())
                .status(domain.getStatus())
                .queueNumber(domain.getQueueNumber())
                .notes(domain.getNotes())
                .build();
    }
}