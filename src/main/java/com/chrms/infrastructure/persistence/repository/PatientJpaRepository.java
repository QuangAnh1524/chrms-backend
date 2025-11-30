package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.PatientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientJpaRepository extends JpaRepository<PatientJpaEntity, Long> {
    Optional<PatientJpaEntity> findByUserId(Long userId);
}