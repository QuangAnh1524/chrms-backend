package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.FeedbackJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackJpaRepository extends JpaRepository<FeedbackJpaEntity, Long> {
    Optional<FeedbackJpaEntity> findByAppointmentId(Long appointmentId);
    List<FeedbackJpaEntity> findByDoctorId(Long doctorId);
    List<FeedbackJpaEntity> findByPatientId(Long patientId);
    
    @Query("SELECT AVG(f.rating) FROM FeedbackJpaEntity f WHERE f.doctorId = :doctorId")
    Double getAverageRatingByDoctorId(@Param("doctorId") Long doctorId);
}

