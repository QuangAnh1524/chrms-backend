package com.chrms.presentation.controller;

import com.chrms.domain.entity.Hospital;
import com.chrms.domain.repository.HospitalRepository;
import com.chrms.presentation.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
@Tag(name = "Hospital", description = "Hospital APIs")
public class HospitalController {

    private final HospitalRepository hospitalRepository;

    @GetMapping
    @Operation(summary = "Get all hospitals", description = "Get list of all hospitals")
    public ApiResponse<List<HospitalResponse>> getAllHospitals() {
        List<Hospital> hospitals = hospitalRepository.findAll();

        List<HospitalResponse> response = hospitals.stream()
                .map(hospital -> HospitalResponse.builder()
                        .id(hospital.getId())
                        .name(hospital.getName())
                        .address(hospital.getAddress())
                        .phone(hospital.getPhone())
                        .email(hospital.getEmail())
                        .type(hospital.getType().name())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hospital by ID", description = "Get hospital details by ID")
    public ApiResponse<HospitalResponse> getHospitalById(@PathVariable Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("Hospital", id));

        HospitalResponse response = HospitalResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .phone(hospital.getPhone())
                .email(hospital.getEmail())
                .type(hospital.getType().name())
                .build();

        return ApiResponse.success(response);
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class HospitalResponse {
        private Long id;
        private String name;
        private String address;
        private String phone;
        private String email;
        private String type;
    }
}