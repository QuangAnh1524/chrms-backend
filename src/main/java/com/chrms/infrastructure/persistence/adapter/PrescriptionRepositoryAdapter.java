package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Prescription;
import com.chrms.domain.repository.PrescriptionRepository;
import com.chrms.infrastructure.persistence.entity.PrescriptionJpaEntity;
import com.chrms.infrastructure.persistence.repository.PrescriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PrescriptionRepositoryAdapter implements PrescriptionRepository {

    private final PrescriptionJpaRepository jpaRepository;

    @Override
    public Prescription save(Prescription prescription) {
        PrescriptionJpaEntity entity = toEntity(prescription);
        PrescriptionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Prescription> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Prescription> findByMedicalRecordId(Long medicalRecordId) {
        return jpaRepository.findByMedicalRecordId(medicalRecordId).map(this::toDomain);
    }

    @Override
    public List<Prescription> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Prescription toDomain(PrescriptionJpaEntity entity) {
        return Prescription.builder()
                .id(entity.getId())
                .medicalRecordId(entity.getMedicalRecordId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PrescriptionJpaEntity toEntity(Prescription domain) {
        return PrescriptionJpaEntity.builder()
                .id(domain.getId())
                .medicalRecordId(domain.getMedicalRecordId())
                .build();
    }
}

