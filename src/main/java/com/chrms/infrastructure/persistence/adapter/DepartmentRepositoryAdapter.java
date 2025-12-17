package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Department;
import com.chrms.domain.repository.DepartmentRepository;
import com.chrms.infrastructure.persistence.entity.DepartmentJpaEntity;
import com.chrms.infrastructure.persistence.repository.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DepartmentRepositoryAdapter implements DepartmentRepository {

    private final DepartmentJpaRepository jpaRepository;

    @Override
    public Optional<Department> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private Department toDomain(DepartmentJpaEntity entity) {
        return Department.builder()
                .id(entity.getId())
                .hospitalId(entity.getHospitalId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
