package com.chrms.infrastructure.persistence.repository;

import com.chrms.domain.enums.RecordStatus;
import com.chrms.infrastructure.persistence.entity.MedicalRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordJpaRepository extends JpaRepository<MedicalRecordJpaEntity, Long> {
    Optional<MedicalRecordJpaEntity> findByAppointmentId(Long appointmentId);
    List<MedicalRecordJpaEntity> findByPatientId(Long patientId);
    List<MedicalRecordJpaEntity> findByDoctorId(Long doctorId);
    List<MedicalRecordJpaEntity> findByHospitalId(Long hospitalId);
    List<MedicalRecordJpaEntity> findByStatus(RecordStatus status);

    @Query(value = "SELECT * FROM medical_records WHERE symptoms_tsvector @@ plainto_tsquery('english', :symptoms)", nativeQuery = true)
    List<MedicalRecordJpaEntity> searchBySymptoms(@Param("symptoms") String symptoms);

    @Query(value = "SELECT * FROM medical_records WHERE diagnosis_tsvector @@ plainto_tsquery('english', :diagnosis)", nativeQuery = true)
    List<MedicalRecordJpaEntity> searchByDiagnosis(@Param("diagnosis") String diagnosis);
}
