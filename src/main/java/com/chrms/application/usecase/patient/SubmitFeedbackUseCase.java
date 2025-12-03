package com.chrms.application.usecase.patient;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.Feedback;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmitFeedbackUseCase {
    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public Feedback execute(Long appointmentId, Long patientId, Integer rating, String comment) {
        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

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
                .patientId(patientId)
                .doctorId(appointment.getDoctorId())
                .rating(rating)
                .comment(comment)
                .build();

        return feedbackRepository.save(feedback);
    }
}

