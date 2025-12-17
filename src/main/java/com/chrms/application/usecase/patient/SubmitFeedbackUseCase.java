package com.chrms.application.usecase.patient;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.Feedback;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.FeedbackRepository;
import com.chrms.domain.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmitFeedbackUseCase {
    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public Feedback execute(Long appointmentId, Long patientUserId, Integer rating, String comment) {
        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

        // Validate patient profile exists
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient profile not found for user: " + patientUserId
                ));

        // Ensure the appointment belongs to the authenticated patient
        if (!appointment.getPatientId().equals(patient.getId())) {
            throw new BusinessRuleViolationException("You can only submit feedback for your own appointments");
        }

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new BusinessRuleViolationException("Rating must be between 1 and 5");
        }

        // Check if feedback already exists
        if (feedbackRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new BusinessRuleViolationException("Feedback already submitted for this appointment");
        }

        // Create feedback
        Feedback feedback = Feedback.builder()
                .appointmentId(appointmentId)
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .rating(rating)
                .comment(comment)
                .build();

        return feedbackRepository.save(feedback);
    }
}

