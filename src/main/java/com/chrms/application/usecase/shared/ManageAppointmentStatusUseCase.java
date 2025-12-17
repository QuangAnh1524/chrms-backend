package com.chrms.application.usecase.shared;

import com.chrms.application.dto.result.AppointmentResult;
import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.Department;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.Hospital;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.AppointmentStatus;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.DepartmentRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.HospitalRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageAppointmentStatusUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResult confirmAppointment(Long appointmentId, Long actorUserId) {
        Appointment appointment = getAppointment(appointmentId);
        User actor = getUser(actorUserId);

        validateDoctorOrAdmin(actor, appointment);

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessRuleViolationException("Only pending appointments can be confirmed");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = appointmentRepository.save(appointment);
        return toResult(saved);
    }

    @Transactional
    public AppointmentResult completeAppointment(Long appointmentId, Long actorUserId) {
        Appointment appointment = getAppointment(appointmentId);
        User actor = getUser(actorUserId);

        validateDoctorOrAdmin(actor, appointment);

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessRuleViolationException("Only confirmed appointments can be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment saved = appointmentRepository.save(appointment);
        return toResult(saved);
    }

    @Transactional
    public AppointmentResult cancelAppointment(Long appointmentId, Long actorUserId, String reason) {
        Appointment appointment = getAppointment(appointmentId);
        User actor = getUser(actorUserId);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleViolationException("Appointment is already finalized");
        }

        switch (actor.getRole()) {
            case PATIENT -> validatePatientOwnership(actor, appointment);
            case DOCTOR -> validateDoctorOwnership(actor, appointment);
            case ADMIN -> {
                // Admin can cancel any appointment
            }
            default -> throw new UnauthorizedException("User role is not permitted to cancel appointments");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        if (reason != null && !reason.isBlank()) {
            appointment.setNotes(reason);
        }

        Appointment saved = appointmentRepository.save(appointment);
        return toResult(saved);
    }

    public AppointmentResult getAppointmentDetail(Long appointmentId, Long actorUserId) {
        Appointment appointment = getAppointment(appointmentId);
        User actor = getUser(actorUserId);

        if (actor.getRole() == Role.PATIENT) {
            validatePatientOwnership(actor, appointment);
        } else if (actor.getRole() == Role.DOCTOR) {
            validateDoctorOwnership(actor, appointment);
        }

        return toResult(appointment);
    }

    private void validateDoctorOrAdmin(User actor, Appointment appointment) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }

        if (actor.getRole() != Role.DOCTOR) {
            throw new UnauthorizedException("Only doctors or admins can perform this action");
        }

        Doctor doctor = doctorRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + actor.getId()));

        if (!appointment.getDoctorId().equals(doctor.getId())) {
            throw new UnauthorizedException("Doctor is not assigned to this appointment");
        }
    }

    private void validatePatientOwnership(User actor, Appointment appointment) {
        Patient patient = patientRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + actor.getId()));

        if (!appointment.getPatientId().equals(patient.getId())) {
            throw new UnauthorizedException("Patient is not authorized for this appointment");
        }
    }

    private void validateDoctorOwnership(User actor, Appointment appointment) {
        Doctor doctor = doctorRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + actor.getId()));

        if (!appointment.getDoctorId().equals(doctor.getId())) {
            throw new UnauthorizedException("Doctor is not assigned to this appointment");
        }
    }

    private Appointment getAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    private AppointmentResult toResult(Appointment appointment) {
        Patient patient = patientRepository.findById(appointment.getPatientId()).orElse(null);
        User patientUser = patient != null ? userRepository.findById(patient.getUserId()).orElse(null) : null;

        Doctor doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(null);
        User doctorUser = doctor != null ? userRepository.findById(doctor.getUserId()).orElse(null) : null;

        Hospital hospital = hospitalRepository.findById(appointment.getHospitalId()).orElse(null);
        Department department = Optional.ofNullable(appointment.getDepartmentId())
                .flatMap(departmentRepository::findById)
                .orElse(null);

        return AppointmentResult.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(patientUser != null ? patientUser.getFullName() : "Unknown")
                .doctorId(appointment.getDoctorId())
                .doctorName(doctorUser != null ? doctorUser.getFullName() : "Unknown")
                .hospitalId(appointment.getHospitalId())
                .hospitalName(hospital != null ? hospital.getName() : "Unknown")
                .departmentId(appointment.getDepartmentId())
                .departmentName(department != null ? department.getName() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(Optional.ofNullable(appointment.getAppointmentTime())
                        .map(t -> t.withSecond(0).withNano(0))
                        .orElse(null))
                .queueNumber(appointment.getQueueNumber())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
