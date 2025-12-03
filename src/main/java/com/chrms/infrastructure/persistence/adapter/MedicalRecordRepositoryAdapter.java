package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.enums.RecordStatus;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.infrastructure.persistence.entity.MedicalRecordJpaEntity;
import com.chrms.infrastructure.persistence.repository.MedicalRecordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MedicalRecordRepositoryAdapter implements MedicalRecordRepository {

    private final MedicalRecordJpaRepository jpaRepository;

    @Override
    public MedicalRecord save(MedicalRecord record) {
        MedicalRecordJpaEntity entity = toEntity(record);
        MedicalRecordJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MedicalRecord> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<MedicalRecord> findByAppointmentId(Long appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId).map(this::toDomain);
    }

    @Override
    public List<MedicalRecord> findByPatientId(Long patientId) {
        return jpaRepository.findByPatientId(patientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> findByDoctorId(Long doctorId) {
        return jpaRepository.findByDoctorId(doctorId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> findByHospitalId(Long hospitalId) {
        return jpaRepository.findByHospitalId(hospitalId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> findByStatus(RecordStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> searchBySymptoms(String symptoms) {
        return jpaRepository.searchBySymptoms(symptoms).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> searchByDiagnosis(String diagnosis) {
        return jpaRepository.searchByDiagnosis(diagnosis).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private MedicalRecord toDomain(MedicalRecordJpaEntity entity) {
        return MedicalRecord.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .doctorId(entity.getDoctorId())
                .hospitalId(entity.getHospitalId())
                .appointmentId(entity.getAppointmentId())
                .diagnosis(entity.getDiagnosis())
                .treatment(entity.getTreatment())
                .status(entity.getStatus())
                .recordDate(entity.getRecordDate())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private MedicalRecordJpaEntity toEntity(MedicalRecord domain) {
        return MedicalRecordJpaEntity.builder()
                .id(domain.getId())
                .patientId(domain.getPatientId())
                .doctorId(domain.getDoctorId())
                .hospitalId(domain.getHospitalId())
                .appointmentId(domain.getAppointmentId())
                .diagnosis(domain.getDiagnosis())
                .treatment(domain.getTreatment())
                .status(domain.getStatus())
                .recordDate(domain.getRecordDate())
                .notes(domain.getNotes())
                .build();
    }
}
