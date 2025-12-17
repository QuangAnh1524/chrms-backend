package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.DepartmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<DepartmentJpaEntity, Long> {
}
