package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.Doctor;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.infrastructure.persistence.entity.DoctorJpaEntity;
import com.chrms.infrastructure.persistence.repository.DoctorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DoctorRepositoryAdapter implements DoctorRepository {

    private final DoctorJpaRepository jpaRepository;

    @Override
    public Doctor save(Doctor doctor) {
        DoctorJpaEntity entity = toEntity(doctor);
        DoctorJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Doctor> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public List<Doctor> findByHospitalId(Long hospitalId) {
        return jpaRepository.findByHospitalId(hospitalId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Doctor> findByDepartmentId(Long departmentId) {
        return jpaRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Doctor> findBySpecialty(String specialty) {
        return jpaRepository.findBySpecialtyContainingIgnoreCase(specialty).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Doctor> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Doctor toDomain(DoctorJpaEntity entity) {
        return Doctor.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .hospitalId(entity.getHospitalId())
                .departmentId(entity.getDepartmentId())
                .specialty(entity.getSpecialty())
                .licenseNumber(entity.getLicenseNumber())
                .experienceYears(entity.getExperienceYears())
                .consultationFee(entity.getConsultationFee())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private DoctorJpaEntity toEntity(Doctor domain) {
        return DoctorJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .hospitalId(domain.getHospitalId())
                .departmentId(domain.getDepartmentId())
                .specialty(domain.getSpecialty())
                .licenseNumber(domain.getLicenseNumber())
                .experienceYears(domain.getExperienceYears())
                .consultationFee(domain.getConsultationFee())
                .build();
    }
}