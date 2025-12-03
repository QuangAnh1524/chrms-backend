package com.chrms.domain.repository;

import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.enums.RecordStatus;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository {
    MedicalRecord save(MedicalRecord record);
    Optional<MedicalRecord> findById(Long id);
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    List<MedicalRecord> findByHospitalId(Long hospitalId);
    List<MedicalRecord> findByStatus(RecordStatus status);
    List<MedicalRecord> searchBySymptoms(String symptoms);
    List<MedicalRecord> searchByDiagnosis(String diagnosis);
    List<MedicalRecord> findAll();
    void deleteById(Long id);
}
