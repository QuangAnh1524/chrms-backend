package com.chrms.presentation.controller;

import com.chrms.application.dto.result.AppointmentResult;
import com.chrms.application.usecase.patient.BookAppointmentUseCase;
import com.chrms.application.usecase.patient.GetPatientAppointmentsUseCase;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.presentation.dto.request.BookAppointmentRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.AppointmentResponse;
import com.chrms.presentation.mapper.AppointmentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PatientController {

    private final BookAppointmentUseCase bookAppointmentUseCase;
    private final GetPatientAppointmentsUseCase getPatientAppointmentsUseCase;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book appointment", description = "Patient books a new appointment")
    public ApiResponse<AppointmentResponse> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + userId));

        AppointmentResult result = bookAppointmentUseCase.execute(
                appointmentMapper.toCommand(request, patient.getId())
        );

        return ApiResponse.success("Appointment booked successfully", appointmentMapper.toResponse(result));
    }

    @GetMapping("/appointments/upcoming")
    @Operation(summary = "Get upcoming appointments", description = "Retrieve upcoming appointments for the authenticated patient")
    public ApiResponse<List<AppointmentResponse>> getUpcomingAppointments(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + userId));

        List<AppointmentResult> results = getPatientAppointmentsUseCase.getUpcomingAppointments(patient.getId());
        List<AppointmentResponse> responses = results.stream()
                .map(appointmentMapper::toResponse)
                .toList();

        return ApiResponse.success("Upcoming appointments retrieved successfully", responses);
    }

    @GetMapping("/appointments/history")
    @Operation(summary = "Get appointment history", description = "Retrieve appointment history for the authenticated patient")
    public ApiResponse<List<AppointmentResponse>> getAppointmentHistory(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + userId));

        List<AppointmentResult> results = getPatientAppointmentsUseCase.getAppointmentHistory(patient.getId());
        List<AppointmentResponse> responses = results.stream()
                .map(appointmentMapper::toResponse)
                .toList();

        return ApiResponse.success("Appointment history retrieved successfully", responses);
    }
}