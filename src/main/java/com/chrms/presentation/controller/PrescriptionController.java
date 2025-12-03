package com.chrms.presentation.controller;

import com.chrms.application.dto.command.CreatePrescriptionCommand;
import com.chrms.application.dto.result.PrescriptionResult;
import com.chrms.application.usecase.doctor.CreatePrescriptionUseCase;
import com.chrms.domain.entity.Prescription;
import com.chrms.domain.repository.PrescriptionRepository;
import com.chrms.presentation.dto.request.CreatePrescriptionRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.PrescriptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescription", description = "Prescription APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PrescriptionController {

    private final CreatePrescriptionUseCase createPrescriptionUseCase;
    private final PrescriptionRepository prescriptionRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create prescription", description = "Create a new prescription for a medical record")
    public ApiResponse<PrescriptionResponse> createPrescription(@Valid @RequestBody CreatePrescriptionRequest request) {
        CreatePrescriptionCommand command = CreatePrescriptionCommand.builder()
                .medicalRecordId(request.getMedicalRecordId())
                .items(request.getItems().stream()
                        .map(item -> CreatePrescriptionCommand.PrescriptionItemDto.builder()
                                .medicineId(item.getMedicineId())
                                .dosage(item.getDosage())
                                .frequency(item.getFrequency())
                                .duration(item.getDuration())
                                .quantity(item.getQuantity())
                                .instructions(item.getInstructions())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        PrescriptionResult result = createPrescriptionUseCase.execute(command);

        PrescriptionResponse response = PrescriptionResponse.builder()
                .id(result.getId())
                .medicalRecordId(result.getMedicalRecordId())
                .items(result.getItems().stream()
                        .map(item -> PrescriptionResponse.PrescriptionItemResponse.builder()
                                .id(item.getId())
                                .medicineId(item.getMedicineId())
                                .medicineName(item.getMedicineName())
                                .dosage(item.getDosage())
                                .frequency(item.getFrequency())
                                .duration(item.getDuration())
                                .quantity(item.getQuantity())
                                .instructions(item.getInstructions())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();

        return ApiResponse.success("Prescription created successfully", response);
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    @Operation(summary = "Get prescription by medical record", description = "Get prescription for a medical record")
    public ApiResponse<PrescriptionResponse> getPrescriptionByMedicalRecord(@PathVariable Long medicalRecordId) {
        Prescription prescription = prescriptionRepository.findByMedicalRecordId(medicalRecordId)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("Prescription", medicalRecordId));

        // Note: Items should be loaded separately via PrescriptionItemRepository
        PrescriptionResponse response = PrescriptionResponse.builder()
                .id(prescription.getId())
                .medicalRecordId(prescription.getMedicalRecordId())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();

        return ApiResponse.success(response);
    }
}

