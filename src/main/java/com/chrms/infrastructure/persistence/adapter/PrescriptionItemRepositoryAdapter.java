package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.PrescriptionItem;
import com.chrms.domain.repository.PrescriptionItemRepository;
import com.chrms.infrastructure.persistence.entity.PrescriptionItemJpaEntity;
import com.chrms.infrastructure.persistence.repository.PrescriptionItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PrescriptionItemRepositoryAdapter implements PrescriptionItemRepository {

    private final PrescriptionItemJpaRepository jpaRepository;

    @Override
    public PrescriptionItem save(PrescriptionItem item) {
        PrescriptionItemJpaEntity entity = toEntity(item);
        PrescriptionItemJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<PrescriptionItem> findByPrescriptionId(Long prescriptionId) {
        return jpaRepository.findByPrescriptionId(prescriptionId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByPrescriptionId(Long prescriptionId) {
        jpaRepository.deleteByPrescriptionId(prescriptionId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private PrescriptionItem toDomain(PrescriptionItemJpaEntity entity) {
        return PrescriptionItem.builder()
                .id(entity.getId())
                .prescriptionId(entity.getPrescriptionId())
                .medicineId(entity.getMedicineId())
                .dosage(entity.getDosage())
                .frequency(entity.getFrequency())
                .duration(entity.getDuration())
                .quantity(entity.getQuantity())
                .instructions(entity.getInstructions())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private PrescriptionItemJpaEntity toEntity(PrescriptionItem domain) {
        return PrescriptionItemJpaEntity.builder()
                .id(domain.getId())
                .prescriptionId(domain.getPrescriptionId())
                .medicineId(domain.getMedicineId())
                .dosage(domain.getDosage())
                .frequency(domain.getFrequency())
                .duration(domain.getDuration())
                .quantity(domain.getQuantity())
                .instructions(domain.getInstructions())
                .build();
    }
}

