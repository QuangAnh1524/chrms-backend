package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.MedicineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineJpaRepository extends JpaRepository<MedicineJpaEntity, Long> {
    List<MedicineJpaEntity> findByNameContainingIgnoreCase(String name);
}

