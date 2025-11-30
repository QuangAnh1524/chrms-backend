package com.chrms.presentation.controller;

import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.User;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.UserRepository;
import com.chrms.presentation.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor", description = "Doctor APIs")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all doctors", description = "Get list of all doctors")
    public ApiResponse<List<DoctorListResponse>> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();

        List<DoctorListResponse> response = doctors.stream()
                .map(doctor -> {
                    User user = userRepository.findById(doctor.getUserId()).orElse(null);
                    return DoctorListResponse.builder()
                            .id(doctor.getId())
                            .fullName(user != null ? user.getFullName() : "Unknown")
                            .specialty(doctor.getSpecialty())
                            .hospitalId(doctor.getHospitalId())
                            .departmentId(doctor.getDepartmentId())
                            .experienceYears(doctor.getExperienceYears())
                            .consultationFee(doctor.getConsultationFee())
                            .build();
                })
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Get doctor details by ID")
    public ApiResponse<DoctorListResponse> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("Doctor", id));

        User user = userRepository.findById(doctor.getUserId()).orElse(null);

        DoctorListResponse response = DoctorListResponse.builder()
                .id(doctor.getId())
                .fullName(user != null ? user.getFullName() : "Unknown")
                .specialty(doctor.getSpecialty())
                .hospitalId(doctor.getHospitalId())
                .departmentId(doctor.getDepartmentId())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .build();

        return ApiResponse.success(response);
    }

    @GetMapping("/hospital/{hospitalId}")
    @Operation(summary = "Get doctors by hospital", description = "Get all doctors in a hospital")
    public ApiResponse<List<DoctorListResponse>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        List<Doctor> doctors = doctorRepository.findByHospitalId(hospitalId);

        List<DoctorListResponse> response = doctors.stream()
                .map(doctor -> {
                    User user = userRepository.findById(doctor.getUserId()).orElse(null);
                    return DoctorListResponse.builder()
                            .id(doctor.getId())
                            .fullName(user != null ? user.getFullName() : "Unknown")
                            .specialty(doctor.getSpecialty())
                            .hospitalId(doctor.getHospitalId())
                            .departmentId(doctor.getDepartmentId())
                            .experienceYears(doctor.getExperienceYears())
                            .consultationFee(doctor.getConsultationFee())
                            .build();
                })
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class DoctorListResponse {
        private Long id;
        private String fullName;
        private String specialty;
        private Long hospitalId;
        private Long departmentId;
        private Integer experienceYears;
        private BigDecimal consultationFee;
    }
}