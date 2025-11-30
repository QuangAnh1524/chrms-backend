package com.chrms.infrastructure.persistence.repository;

import com.chrms.domain.enums.HospitalType;
import com.chrms.infrastructure.persistence.entity.HospitalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalJpaRepository extends JpaRepository<HospitalJpaEntity, Long> {
    List<HospitalJpaEntity> findByType(HospitalType type);
}