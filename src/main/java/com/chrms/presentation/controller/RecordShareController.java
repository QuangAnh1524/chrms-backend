package com.chrms.presentation.controller;

import com.chrms.application.dto.result.RecordShareResult;
import com.chrms.application.dto.result.SharedMedicalRecordResult;
import com.chrms.application.usecase.shared.GetMyRecordSharesUseCase;
import com.chrms.application.usecase.shared.GetSharedMedicalRecordsUseCase;
import com.chrms.application.usecase.shared.RevokeRecordShareUseCase;
import com.chrms.application.usecase.shared.ShareMedicalRecordUseCase;
import com.chrms.domain.enums.Role;
import com.chrms.infrastructure.security.SecurityUtils;
import com.chrms.presentation.dto.request.ShareMedicalRecordRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.RecordShareResponse;
import com.chrms.presentation.dto.response.SharedMedicalRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
@Tag(name = "Record Share", description = "Record sharing APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class RecordShareController {

    private final ShareMedicalRecordUseCase shareMedicalRecordUseCase;
    private final GetSharedMedicalRecordsUseCase getSharedMedicalRecordsUseCase;
    private final GetMyRecordSharesUseCase getMyRecordSharesUseCase;
    private final RevokeRecordShareUseCase revokeRecordShareUseCase;

    @PostMapping("/{id}/share")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Share medical record", description = "Share a medical record to another hospital")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<RecordShareResponse> shareMedicalRecord(
            @PathVariable("id") Long medicalRecordId,
            @Valid @RequestBody ShareMedicalRecordRequest request,
            HttpServletRequest httpRequest) {

        Long actorUserId = SecurityUtils.getUserId(httpRequest);
        RecordShareResult result = shareMedicalRecordUseCase.execute(
                medicalRecordId,
                request.getToHospitalId(),
                request.getNotes(),
                request.getExpiryDate(),
                actorUserId
        );

        return ApiResponse.success("Chia sẻ hồ sơ thành công", mapToResponse(result));
    }

    @GetMapping("/shared-to-me")
    @Operation(summary = "Get records shared to my hospital", description = "List medical records shared to the doctor's hospital")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<List<SharedMedicalRecordResponse>> getSharedToMe(
            @RequestParam(value = "patientId", required = false) Long patientId,
            HttpServletRequest httpRequest) {

        Long actorUserId = SecurityUtils.getUserId(httpRequest);
        Role actorRole = SecurityUtils.getUserRole(httpRequest);

        List<SharedMedicalRecordResult> results = getSharedMedicalRecordsUseCase.execute(actorUserId, actorRole, patientId);

        List<SharedMedicalRecordResponse> response = results.stream()
                .map(result -> SharedMedicalRecordResponse.builder()
                        .share(mapToResponse(result.getShare()))
                        .medicalRecord(result.getMedicalRecord())
                        .files(result.getFiles())
                        .prescription(result.getPrescription())
                        .prescriptionItems(result.getPrescriptionItems())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    @GetMapping("/my-shares")
    @Operation(summary = "Get records I shared", description = "List record shares created by current user")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<List<RecordShareResponse>> getMyShares(HttpServletRequest httpRequest) {
        Long actorUserId = SecurityUtils.getUserId(httpRequest);
        List<RecordShareResponse> response = getMyRecordSharesUseCase.execute(actorUserId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(response);
    }

    @DeleteMapping("/shares/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke record share", description = "Revoke a previously shared medical record")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public void revokeShare(@PathVariable("id") Long shareId, HttpServletRequest httpRequest) {
        Long actorUserId = SecurityUtils.getUserId(httpRequest);
        revokeRecordShareUseCase.execute(shareId, actorUserId);
    }

    private RecordShareResponse mapToResponse(RecordShareResult result) {
        return RecordShareResponse.builder()
                .id(result.getId())
                .medicalRecordId(result.getMedicalRecordId())
                .fromHospitalId(result.getFromHospitalId())
                .toHospitalId(result.getToHospitalId())
                .sharedBy(result.getSharedBy())
                .sharedAt(result.getSharedAt())
                .expiryDate(result.getExpiryDate())
                .notes(result.getNotes())
                .build();
    }
}

