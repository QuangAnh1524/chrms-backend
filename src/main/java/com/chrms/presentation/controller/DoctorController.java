package com.chrms.presentation.controller;

import com.chrms.application.dto.command.CreateScheduleCommand;
import com.chrms.application.dto.result.ScheduleResult;
import com.chrms.application.usecase.doctor.CreateScheduleUseCase;
import com.chrms.application.usecase.doctor.GetAvailableSlotsUseCase;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.DoctorSchedule;
import com.chrms.domain.entity.User;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.DoctorScheduleRepository;
import com.chrms.domain.repository.UserRepository;
import com.chrms.presentation.dto.request.CreateScheduleRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor", description = "Doctor APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final CreateScheduleUseCase createScheduleUseCase;
    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;

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

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get doctors by department", description = "Get all doctors in a department")
    public ApiResponse<List<DoctorListResponse>> getDoctorsByDepartment(@PathVariable Long departmentId) {
        List<Doctor> doctors = doctorRepository.findByDepartmentId(departmentId);

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

    @PostMapping("/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create doctor schedule", description = "Create a new schedule for a doctor")
    public ApiResponse<ScheduleResponse> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        CreateScheduleCommand command = CreateScheduleCommand.builder()
                .doctorId(request.getDoctorId())
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isAvailable(request.getIsAvailable())
                .build();

        ScheduleResult result = createScheduleUseCase.execute(command);

        ScheduleResponse response = ScheduleResponse.builder()
                .id(result.getId())
                .doctorId(result.getDoctorId())
                .dayOfWeek(result.getDayOfWeek())
                .startTime(result.getStartTime())
                .endTime(result.getEndTime())
                .isAvailable(result.getIsAvailable())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();

        return ApiResponse.success("Schedule created successfully", response);
    }

    @GetMapping("/{doctorId}/schedules")
    @Operation(summary = "Get doctor schedules", description = "Get all schedules for a doctor")
    public ApiResponse<List<ScheduleResponse>> getDoctorSchedules(@PathVariable Long doctorId) {
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorId(doctorId);

        List<ScheduleResponse> response = schedules.stream()
                .map(schedule -> ScheduleResponse.builder()
                        .id(schedule.getId())
                        .doctorId(schedule.getDoctorId())
                        .dayOfWeek(schedule.getDayOfWeek())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .isAvailable(schedule.getIsAvailable())
                        .createdAt(schedule.getCreatedAt())
                        .updatedAt(schedule.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    @GetMapping("/{doctorId}/available-slots")
    @Operation(summary = "Get available time slots", description = "Get available time slots for a doctor on a specific date")
    public ApiResponse<List<GetAvailableSlotsUseCase.TimeSlotResult>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GetAvailableSlotsUseCase.TimeSlotResult> slots = getAvailableSlotsUseCase.execute(doctorId, date);
        return ApiResponse.success(slots);
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