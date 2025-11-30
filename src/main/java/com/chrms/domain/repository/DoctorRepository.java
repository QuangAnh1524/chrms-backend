package com.chrms.domain.repository;

import com.chrms.domain.entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Doctor save(Doctor doctor);
    Optional<Doctor> findById(Long id);
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByHospitalId(Long hospitalId);
    List<Doctor> findByDepartmentId(Long departmentId);
    List<Doctor> findBySpecialty(String specialty);
    List<Doctor> findAll();
    void deleteById(Long id);
}
