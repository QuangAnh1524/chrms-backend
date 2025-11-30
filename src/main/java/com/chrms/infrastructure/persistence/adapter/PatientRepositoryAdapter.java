package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Patient;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.infrastructure.persistence.entity.PatientJpaEntity;
import com.chrms.infrastructure.persistence.repository.PatientJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository jpaRepository;

    @Override
    public Patient save(Patient patient) {
        PatientJpaEntity entity = toEntity(patient);
        PatientJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Patient> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public List<Patient> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Patient toDomain(PatientJpaEntity entity) {
        return Patient.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .dateOfBirth(entity.getDateOfBirth())
                .gender(entity.getGender())
                .address(entity.getAddress())
                .emergencyContact(entity.getEmergencyContact())
                .bloodType(entity.getBloodType())
                .allergies(entity.getAllergies())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PatientJpaEntity toEntity(Patient domain) {
        return PatientJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .dateOfBirth(domain.getDateOfBirth())
                .gender(domain.getGender())
                .address(domain.getAddress())
                .emergencyContact(domain.getEmergencyContact())
                .bloodType(domain.getBloodType())
                .allergies(domain.getAllergies())
                .build();
    }
}