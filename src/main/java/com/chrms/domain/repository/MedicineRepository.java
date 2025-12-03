package com.chrms.domain.repository;

import com.chrms.domain.entity.Medicine;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository {
    Medicine save(Medicine medicine);
    Optional<Medicine> findById(Long id);
    List<Medicine> findByNameContaining(String name);
    List<Medicine> findAll();
    void deleteById(Long id);
}

