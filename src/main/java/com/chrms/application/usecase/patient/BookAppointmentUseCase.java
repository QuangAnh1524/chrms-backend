package com.chrms.application.usecase.patient;

import com.chrms.application.dto.command.BookAppointmentCommand;
import com.chrms.application.dto.result.AppointmentResult;
import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.Department;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.Hospital;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.AppointmentStatus;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.DepartmentRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.DoctorScheduleRepository;
import com.chrms.domain.repository.HospitalRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DoctorScheduleRepository scheduleRepository;

    @Transactional
    public AppointmentResult execute(BookAppointmentCommand command) {
        // Validate patient exists
        Patient patient = patientRepository.findById(command.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient", command.getPatientId()));

        // Validate doctor exists
        Doctor doctor = doctorRepository.findById(command.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor", command.getDoctorId()));

        // Validate hospital exists
        Hospital hospital = hospitalRepository.findById(command.getHospitalId())
                .orElseThrow(() -> new EntityNotFoundException("Hospital", command.getHospitalId()));

        if (!doctor.getHospitalId().equals(hospital.getId())) {
            throw new BusinessRuleViolationException("Doctor is not assigned to the selected hospital");
        }

        // Validate department exists and matches doctor/hospital
        Department department = departmentRepository.findById(command.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department", command.getDepartmentId()));

        if (!department.getHospitalId().equals(hospital.getId())) {
            throw new BusinessRuleViolationException("Department does not belong to the selected hospital");
        }

        if (doctor.getDepartmentId() != null && !doctor.getDepartmentId().equals(department.getId())) {
            throw new BusinessRuleViolationException("Doctor is not assigned to the selected department");
        }

        LocalTime normalizedAppointmentTime = command.getAppointmentTime()
                .withSecond(0)
                .withNano(0);

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                command.getAppointmentDate(),
                normalizedAppointmentTime
        );

        // Validate appointment datetime is not in the past
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleViolationException("Appointment time must be in the future");
        }

        // Check if doctor is available at this time based on schedule
        boolean isAvailable = scheduleRepository.isDoctorAvailableAt(
                command.getDoctorId(),
                command.getAppointmentDate(),
                normalizedAppointmentTime
        );

        if (!isAvailable) {
            throw new BusinessRuleViolationException("Doctor is not available at this time. Please check available slots.");
        }

        // Double-check if doctor has appointment at the same time (additional validation)
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndDate(
                command.getDoctorId(),
                command.getAppointmentDate()
        );

        boolean hasConflict = existingAppointments.stream()
                .anyMatch(apt -> apt.getAppointmentTime().equals(normalizedAppointmentTime)
                        && apt.getStatus() != AppointmentStatus.CANCELLED);

        if (hasConflict) {
            throw new BusinessRuleViolationException("Doctor already has an appointment at this time");
        }

        // Prevent patient from booking multiple appointments at the same time slot
        List<Appointment> patientAppointmentsSameDay = appointmentRepository.findByPatientIdAndDate(
                command.getPatientId(),
                command.getAppointmentDate()
        );

        boolean patientHasConflict = patientAppointmentsSameDay.stream()
                .filter(apt -> apt.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(apt -> normalizedAppointmentTime.equals(apt.getAppointmentTime()));

        if (patientHasConflict) {
            throw new BusinessRuleViolationException("You already have an appointment at this time");
        }

        int queueNumber = (int) appointmentRepository.countByDoctorIdAndHospitalIdAndDate(
                command.getDoctorId(),
                command.getHospitalId(),
                command.getAppointmentDate()
        ) + 1;

        // Create appointment
        Appointment appointment = Appointment.builder()
                .patientId(command.getPatientId())
                .doctorId(command.getDoctorId())
                .hospitalId(command.getHospitalId())
                .departmentId(department.getId())
                .appointmentDate(command.getAppointmentDate())
                .appointmentTime(normalizedAppointmentTime)
                .status(AppointmentStatus.PENDING)
                .queueNumber(queueNumber)
                .notes(command.getNotes())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Get user details for response
        User patientUser = userRepository.findById(patient.getUserId()).orElse(null);
        User doctorUser = userRepository.findById(doctor.getUserId()).orElse(null);

        return AppointmentResult.builder()
                .id(savedAppointment.getId())
                .patientId(savedAppointment.getPatientId())
                .patientName(patientUser != null ? patientUser.getFullName() : "Unknown")
                .doctorId(savedAppointment.getDoctorId())
                .doctorName(doctorUser != null ? doctorUser.getFullName() : "Unknown")
                .hospitalId(savedAppointment.getHospitalId())
                .hospitalName(hospital.getName())
                .departmentId(savedAppointment.getDepartmentId())
                .departmentName(department.getName())
                .appointmentDate(savedAppointment.getAppointmentDate())
                .appointmentTime(savedAppointment.getAppointmentTime())
                .queueNumber(savedAppointment.getQueueNumber())
                .status(savedAppointment.getStatus())
                .notes(savedAppointment.getNotes())
                .createdAt(savedAppointment.getCreatedAt())
                .build();
    }
}