package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.RecordShare;
import com.chrms.domain.repository.RecordShareRepository;
import com.chrms.infrastructure.persistence.entity.RecordShareJpaEntity;
import com.chrms.infrastructure.persistence.repository.RecordShareJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecordShareRepositoryAdapter implements RecordShareRepository {

    private final RecordShareJpaRepository jpaRepository;

    @Override
    public RecordShare save(RecordShare share) {
        RecordShareJpaEntity saved = jpaRepository.save(toEntity(share));
        return toDomain(saved);
    }

    @Override
    public Optional<RecordShare> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<RecordShare> findByMedicalRecordId(Long medicalRecordId) {
        return jpaRepository.findByMedicalRecordId(medicalRecordId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordShare> findByToHospitalId(Long hospitalId) {
        return jpaRepository.findByToHospitalId(hospitalId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordShare> findBySharedBy(Long userId) {
        return jpaRepository.findBySharedBy(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private RecordShare toDomain(RecordShareJpaEntity entity) {
        return RecordShare.builder()
                .id(entity.getId())
                .medicalRecordId(entity.getMedicalRecordId())
                .fromHospitalId(entity.getFromHospitalId())
                .toHospitalId(entity.getToHospitalId())
                .sharedBy(entity.getSharedBy())
                .sharedAt(entity.getSharedAt())
                .expiryDate(entity.getExpiryDate())
                .notes(entity.getNotes())
                .build();
    }

    private RecordShareJpaEntity toEntity(RecordShare domain) {
        return RecordShareJpaEntity.builder()
                .id(domain.getId())
                .medicalRecordId(domain.getMedicalRecordId())
                .fromHospitalId(domain.getFromHospitalId())
                .toHospitalId(domain.getToHospitalId())
                .sharedBy(domain.getSharedBy())
                .sharedAt(domain.getSharedAt())
                .expiryDate(domain.getExpiryDate())
                .notes(domain.getNotes())
                .build();
    }
}

