package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Medicine;
import com.chrms.domain.repository.MedicineRepository;
import com.chrms.infrastructure.persistence.entity.MedicineJpaEntity;
import com.chrms.infrastructure.persistence.repository.MedicineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MedicineRepositoryAdapter implements MedicineRepository {

    private final MedicineJpaRepository jpaRepository;

    @Override
    public Medicine save(Medicine medicine) {
        MedicineJpaEntity entity = toEntity(medicine);
        MedicineJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Medicine> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Medicine> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medicine> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Medicine toDomain(MedicineJpaEntity entity) {
        return Medicine.builder()
                .id(entity.getId())
                .name(entity.getName())
                .genericName(entity.getGenericName())
                .manufacturer(entity.getManufacturer())
                .dosageForm(entity.getDosageForm())
                .strength(entity.getStrength())
                .description(entity.getDescription())
                .unitPrice(entity.getUnitPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private MedicineJpaEntity toEntity(Medicine domain) {
        return MedicineJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .genericName(domain.getGenericName())
                .manufacturer(domain.getManufacturer())
                .dosageForm(domain.getDosageForm())
                .strength(domain.getStrength())
                .description(domain.getDescription())
                .unitPrice(domain.getUnitPrice())
                .build();
    }
}

