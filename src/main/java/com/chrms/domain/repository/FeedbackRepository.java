package com.chrms.domain.repository;

import com.chrms.domain.entity.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository {
    Feedback save(Feedback feedback);
    Optional<Feedback> findById(Long id);
    Optional<Feedback> findByAppointmentId(Long appointmentId);
    List<Feedback> findByDoctorId(Long doctorId);
    List<Feedback> findByPatientId(Long patientId);
    Double getAverageRatingByDoctorId(Long doctorId);
}

