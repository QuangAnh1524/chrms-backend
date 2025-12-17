package com.chrms.application.usecase.patient;

import com.chrms.application.dto.result.AppointmentResult;
import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.Department;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.Hospital;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.AppointmentStatus;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.DepartmentRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.HospitalRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPatientAppointmentsUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public List<AppointmentResult> getUpcomingAppointments(Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findByPatientId(patientId).stream()
                .filter(appointment -> isUpcoming(appointment, now))
                .sorted(Comparator
                        .comparing(Appointment::getAppointmentDate)
                        .thenComparing(Appointment::getAppointmentTime))
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    public List<AppointmentResult> getAppointmentHistory(Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findByPatientId(patientId).stream()
                .filter(appointment -> !isUpcoming(appointment, now) || isTerminalStatus(appointment.getStatus()))
                .sorted(Comparator
                        .comparing(Appointment::getAppointmentDate)
                        .thenComparing(Appointment::getAppointmentTime)
                        .reversed())
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    private boolean isUpcoming(Appointment appointment, LocalDateTime now) {
        if (isTerminalStatus(appointment.getStatus())) {
            return false;
        }
        LocalTime time = Optional.ofNullable(appointment.getAppointmentTime())
                .map(t -> t.withSecond(0).withNano(0))
                .orElse(LocalTime.MIDNIGHT);
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                Optional.ofNullable(appointment.getAppointmentDate()).orElse(LocalDate.now()),
                time
        );
        return !appointmentDateTime.isBefore(now);
    }

    private boolean isTerminalStatus(AppointmentStatus status) {
        return status == AppointmentStatus.CANCELLED || status == AppointmentStatus.COMPLETED;
    }

    private AppointmentResult toResult(Appointment appointment) {
        Patient patient = patientRepository.findById(appointment.getPatientId()).orElse(null);
        User patientUser = patient != null ? userRepository.findById(patient.getUserId()).orElse(null) : null;

        Doctor doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(null);
        User doctorUser = doctor != null ? userRepository.findById(doctor.getUserId()).orElse(null) : null;

        Hospital hospital = hospitalRepository.findById(appointment.getHospitalId()).orElse(null);
        Department department = appointment.getDepartmentId() != null
                ? departmentRepository.findById(appointment.getDepartmentId()).orElse(null)
                : null;

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
