package com.chrms.domain.repository;

import com.chrms.domain.entity.Department;

import java.util.Optional;

public interface DepartmentRepository {
    Optional<Department> findById(Long id);
}
