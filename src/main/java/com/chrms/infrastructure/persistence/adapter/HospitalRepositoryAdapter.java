package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Hospital;
import com.chrms.domain.enums.HospitalType;
import com.chrms.domain.repository.HospitalRepository;
import com.chrms.infrastructure.persistence.entity.HospitalJpaEntity;
import com.chrms.infrastructure.persistence.repository.HospitalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HospitalRepositoryAdapter implements HospitalRepository {

    private final HospitalJpaRepository jpaRepository;

    @Override
    public Hospital save(Hospital hospital) {
        HospitalJpaEntity entity = toEntity(hospital);
        HospitalJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Hospital> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Hospital> findByType(HospitalType type) {
        return jpaRepository.findByType(type).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Hospital> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Hospital toDomain(HospitalJpaEntity entity) {
        return Hospital.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private HospitalJpaEntity toEntity(Hospital domain) {
        return HospitalJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .email(domain.getEmail())
                .type(domain.getType())
                .build();
    }
}