package com.chrms.domain.repository;

import com.chrms.domain.entity.Hospital;
import com.chrms.domain.enums.HospitalType;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository {
    Hospital save(Hospital hospital);
    Optional<Hospital> findById(Long id);
    List<Hospital> findByType(HospitalType type);
    List<Hospital> findAll();
    void deleteById(Long id);
}
