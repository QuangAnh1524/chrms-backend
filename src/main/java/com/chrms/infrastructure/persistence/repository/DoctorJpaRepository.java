package com.chrms.infrastructure.persistence.repository;

import com.chrms.infrastructure.persistence.entity.DoctorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorJpaRepository extends JpaRepository<DoctorJpaEntity, Long> {
    Optional<DoctorJpaEntity> findByUserId(Long userId);
    List<DoctorJpaEntity> findByHospitalId(Long hospitalId);
    List<DoctorJpaEntity> findByDepartmentId(Long departmentId);
    List<DoctorJpaEntity> findBySpecialtyContainingIgnoreCase(String specialty);
}