package com.chrms.application.usecase.patient;

import com.chrms.application.dto.command.BookAppointmentCommand;
import com.chrms.application.dto.result.AppointmentResult;
import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.Hospital;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.AppointmentStatus;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.HospitalRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

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

        // Validate appointment date is not in the past
        if (command.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleViolationException("Cannot book appointment in the past");
        }

        // Check if doctor has appointment at the same time
        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndDate(
                command.getDoctorId(),
                command.getAppointmentDate()
        );

        boolean hasConflict = existingAppointments.stream()
                .anyMatch(apt -> apt.getAppointmentTime().equals(command.getAppointmentTime())
                        && apt.getStatus() != AppointmentStatus.CANCELLED);

        if (hasConflict) {
            throw new BusinessRuleViolationException("Doctor is not available at this time");
        }

        // Create appointment
        Appointment appointment = Appointment.builder()
                .patientId(command.getPatientId())
                .doctorId(command.getDoctorId())
                .hospitalId(command.getHospitalId())
                .appointmentDate(command.getAppointmentDate())
                .appointmentTime(command.getAppointmentTime())
                .status(AppointmentStatus.PENDING)
                .symptoms(command.getSymptoms())
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
                .appointmentDate(savedAppointment.getAppointmentDate())
                .appointmentTime(savedAppointment.getAppointmentTime())
                .status(savedAppointment.getStatus())
                .symptoms(savedAppointment.getSymptoms())
                .notes(savedAppointment.getNotes())
                .createdAt(savedAppointment.getCreatedAt())
                .build();
    }
}