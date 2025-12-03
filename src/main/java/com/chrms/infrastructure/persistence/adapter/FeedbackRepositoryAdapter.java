package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Feedback;
import com.chrms.domain.repository.FeedbackRepository;
import com.chrms.infrastructure.persistence.entity.FeedbackJpaEntity;
import com.chrms.infrastructure.persistence.repository.FeedbackJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackRepositoryAdapter implements FeedbackRepository {

    private final FeedbackJpaRepository jpaRepository;

    @Override
    public Feedback save(Feedback feedback) {
        FeedbackJpaEntity entity = toEntity(feedback);
        FeedbackJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Feedback> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Feedback> findByAppointmentId(Long appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId).map(this::toDomain);
    }

    @Override
    public List<Feedback> findByDoctorId(Long doctorId) {
        return jpaRepository.findByDoctorId(doctorId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Feedback> findByPatientId(Long patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingByDoctorId(Long doctorId) {
        return jpaRepository.getAverageRatingByDoctorId(doctorId);
    }

    private Feedback toDomain(FeedbackJpaEntity entity) {
        return Feedback.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointmentId())
                .patientId(entity.getPatientId())
                .doctorId(entity.getDoctorId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private FeedbackJpaEntity toEntity(Feedback domain) {
        return FeedbackJpaEntity.builder()
                .id(domain.getId())
                .appointmentId(domain.getAppointmentId())
                .patientId(domain.getPatientId())
                .doctorId(domain.getDoctorId())
                .rating(domain.getRating())
                .comment(domain.getComment())
                .build();
    }
}

