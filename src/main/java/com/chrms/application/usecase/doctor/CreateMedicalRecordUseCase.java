package com.chrms.application.usecase.doctor;

import com.chrms.application.dto.command.CreateMedicalRecordCommand;
import com.chrms.application.dto.result.MedicalRecordResult;
import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.RecordStatus;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateMedicalRecordUseCase {
    private final MedicalRecordRepository recordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public MedicalRecordResult execute(CreateMedicalRecordCommand command, Long actorUserId) {
        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(command.getAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment", command.getAppointmentId()));

        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", actorUserId));

        if (actor.getRole() != com.chrms.domain.enums.Role.DOCTOR) {
            throw new UnauthorizedException("Only doctors can create medical records");
        }

        Long doctorId = doctorRepository.findByUserId(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + actorUserId))
                .getId();

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new UnauthorizedException("Doctor is not assigned to this appointment");
        }

        // Check if record already exists for this appointment
        if (recordRepository.findByAppointmentId(command.getAppointmentId()).isPresent()) {
            throw new BusinessRuleViolationException("Medical record already exists for this appointment");
        }

        // Create medical record
        MedicalRecord record = MedicalRecord.builder()
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .hospitalId(appointment.getHospitalId())
                .appointmentId(command.getAppointmentId())
                .symptoms(command.getSymptoms() != null ? command.getSymptoms() : appointment.getNotes())
                .diagnosis(command.getDiagnosis())
                .treatment(command.getTreatment())
                .status(RecordStatus.DRAFT)
                .recordDate(LocalDate.now())
                .notes(command.getNotes())
                .build();

        MedicalRecord saved = recordRepository.save(record);

        return MedicalRecordResult.builder()
                .id(saved.getId())
                .patientId(saved.getPatientId())
                .doctorId(saved.getDoctorId())
                .hospitalId(saved.getHospitalId())
                .appointmentId(saved.getAppointmentId())
                .symptoms(saved.getSymptoms())
                .diagnosis(saved.getDiagnosis())
                .treatment(saved.getTreatment())
                .status(saved.getStatus())
                .recordDate(saved.getRecordDate())
                .notes(saved.getNotes())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}

