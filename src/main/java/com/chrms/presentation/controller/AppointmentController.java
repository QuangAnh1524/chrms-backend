package com.chrms.presentation.controller;

import com.chrms.application.dto.result.AppointmentResult;
import com.chrms.application.usecase.shared.ManageAppointmentStatusUseCase;
import com.chrms.presentation.dto.request.CancelAppointmentRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.AppointmentResponse;
import com.chrms.presentation.mapper.AppointmentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointment lifecycle APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AppointmentController {

    private final ManageAppointmentStatusUseCase manageAppointmentStatusUseCase;
    private final AppointmentMapper appointmentMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment details", description = "Retrieve a single appointment")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ApiResponse<AppointmentResponse> getAppointmentDetail(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        AppointmentResult result = manageAppointmentStatusUseCase.getAppointmentDetail(id, userId);
        return ApiResponse.success(appointmentMapper.toResponse(result));
    }

    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Confirm appointment", description = "Confirm a pending appointment (Doctor/Admin)")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<AppointmentResponse> confirmAppointment(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        AppointmentResult result = manageAppointmentStatusUseCase.confirmAppointment(id, userId);
        return ApiResponse.success("Appointment confirmed", appointmentMapper.toResponse(result));
    }

    @PostMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Complete appointment", description = "Mark a confirmed appointment as completed (Doctor/Admin)")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<AppointmentResponse> completeAppointment(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        AppointmentResult result = manageAppointmentStatusUseCase.completeAppointment(id, userId);
        return ApiResponse.success("Appointment completed", appointmentMapper.toResponse(result));
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancel appointment", description = "Cancel an appointment (Patient/Doctor/Admin)")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ApiResponse<AppointmentResponse> cancelAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) CancelAppointmentRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String reason = request != null ? request.getReason() : null;
        AppointmentResult result = manageAppointmentStatusUseCase.cancelAppointment(id, userId, reason);
        return ApiResponse.success("Appointment cancelled", appointmentMapper.toResponse(result));
    }
}
