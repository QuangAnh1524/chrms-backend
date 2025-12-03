package com.chrms.presentation.controller;

import com.chrms.application.dto.command.CreateMedicalRecordCommand;
import com.chrms.application.dto.result.MedicalRecordResult;
import com.chrms.application.usecase.doctor.ApproveMedicalRecordUseCase;
import com.chrms.application.usecase.doctor.CreateMedicalRecordUseCase;
import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.presentation.dto.request.CreateMedicalRecordRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.MedicalRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "Medical Record APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class MedicalRecordController {

    private final CreateMedicalRecordUseCase createRecordUseCase;
    private final ApproveMedicalRecordUseCase approveRecordUseCase;
    private final MedicalRecordRepository recordRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create medical record", description = "Create a new medical record for an appointment")
    public ApiResponse<MedicalRecordResponse> createRecord(@Valid @RequestBody CreateMedicalRecordRequest request) {
        CreateMedicalRecordCommand command = CreateMedicalRecordCommand.builder()
                .appointmentId(request.getAppointmentId())
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .notes(request.getNotes())
                .build();

        MedicalRecordResult result = createRecordUseCase.execute(command);

        MedicalRecordResponse response = MedicalRecordResponse.builder()
                .id(result.getId())
                .patientId(result.getPatientId())
                .doctorId(result.getDoctorId())
                .hospitalId(result.getHospitalId())
                .appointmentId(result.getAppointmentId())
                .diagnosis(result.getDiagnosis())
                .treatment(result.getTreatment())
                .status(result.getStatus())
                .recordDate(result.getRecordDate())
                .notes(result.getNotes())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();

        return ApiResponse.success("Medical record created successfully", response);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve medical record", description = "Approve a medical record")
    public ApiResponse<MedicalRecordResponse> approveRecord(@PathVariable Long id) {
        MedicalRecord record = approveRecordUseCase.execute(id);

        MedicalRecordResponse response = MedicalRecordResponse.builder()
                .id(record.getId())
                .patientId(record.getPatientId())
                .doctorId(record.getDoctorId())
                .hospitalId(record.getHospitalId())
                .appointmentId(record.getAppointmentId())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .status(record.getStatus())
                .recordDate(record.getRecordDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();

        return ApiResponse.success("Medical record approved successfully", response);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get records by patient", description = "Get all medical records for a patient")
    public ApiResponse<List<MedicalRecordResponse>> getRecordsByPatient(@PathVariable Long patientId) {
        List<MedicalRecord> records = recordRepository.findByPatientId(patientId);

        List<MedicalRecordResponse> response = records.stream()
                .map(record -> MedicalRecordResponse.builder()
                        .id(record.getId())
                        .patientId(record.getPatientId())
                        .doctorId(record.getDoctorId())
                        .hospitalId(record.getHospitalId())
                        .appointmentId(record.getAppointmentId())
                        .diagnosis(record.getDiagnosis())
                        .treatment(record.getTreatment())
                        .status(record.getStatus())
                        .recordDate(record.getRecordDate())
                        .notes(record.getNotes())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get record by ID", description = "Get medical record details by ID")
    public ApiResponse<MedicalRecordResponse> getRecordById(@PathVariable Long id) {
        MedicalRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("MedicalRecord", id));

        MedicalRecordResponse response = MedicalRecordResponse.builder()
                .id(record.getId())
                .patientId(record.getPatientId())
                .doctorId(record.getDoctorId())
                .hospitalId(record.getHospitalId())
                .appointmentId(record.getAppointmentId())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .status(record.getStatus())
                .recordDate(record.getRecordDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();

        return ApiResponse.success(response);
    }
}

