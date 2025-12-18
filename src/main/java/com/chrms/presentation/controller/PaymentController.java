package com.chrms.presentation.controller;

import com.chrms.application.usecase.patient.CreatePaymentTransactionUseCase;
import com.chrms.application.usecase.patient.PaymentInitiationResult;
import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.PaymentTransaction;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.PaymentTransactionRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.infrastructure.security.SecurityUtils;
import com.chrms.presentation.dto.request.CreatePaymentRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.PaymentTransactionResponse;
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
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment Transaction APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final CreatePaymentTransactionUseCase createPaymentUseCase;
    private final PaymentTransactionRepository transactionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment transaction", description = "Create a new payment transaction for an appointment")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    public ApiResponse<PaymentTransactionResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request, HttpServletRequest httpRequest) {
        Long userId = SecurityUtils.getUserId(httpRequest);
        Role role = SecurityUtils.getUserRole(httpRequest);

        PaymentInitiationResult result = createPaymentUseCase.execute(
                request.getAppointmentId(),
                request.getPaymentMethod(),
                request.getTransactionRef(),
                request.getReturnUrl(),
                userId,
                role
        );

        PaymentTransaction transaction = result.getTransaction();

        PaymentTransactionResponse response = PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .appointmentId(transaction.getAppointmentId())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .transactionRef(transaction.getTransactionRef())
                .paymentUrl(result.getPaymentUrl())
                .paidAt(transaction.getPaidAt())
                .createdAt(transaction.getCreatedAt())
                .build();

        return ApiResponse.success("Payment transaction created successfully", response);
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get payments by appointment", description = "Get all payment transactions for an appointment")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    public ApiResponse<List<PaymentTransactionResponse>> getPaymentsByAppointment(@PathVariable Long appointmentId, HttpServletRequest httpRequest) {
        Long userId = SecurityUtils.getUserId(httpRequest);
        Role role = SecurityUtils.getUserRole(httpRequest);

        validatePaymentAccess(appointmentId, userId, role);
        List<PaymentTransaction> transactions = transactionRepository.findByAppointmentId(appointmentId);

        List<PaymentTransactionResponse> response = transactions.stream()
                .map(transaction -> PaymentTransactionResponse.builder()
                        .id(transaction.getId())
                        .appointmentId(transaction.getAppointmentId())
                        .amount(transaction.getAmount())
                        .paymentMethod(transaction.getPaymentMethod())
                        .status(transaction.getStatus())
                        .transactionRef(transaction.getTransactionRef())
                        .paymentUrl(null)
                        .paidAt(transaction.getPaidAt())
                        .createdAt(transaction.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    @PostMapping("/{transactionRef}/complete")
    @Operation(summary = "Complete payment", description = "Mark payment transaction as completed (for VNPay callback)")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN')")
    public ApiResponse<PaymentTransactionResponse> completePayment(@PathVariable String transactionRef, HttpServletRequest httpRequest) {
        Long userId = SecurityUtils.getUserId(httpRequest);
        Role role = SecurityUtils.getUserRole(httpRequest);

        PaymentTransaction transaction = createPaymentUseCase.completePayment(transactionRef, userId, role);

        PaymentTransactionResponse response = PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .appointmentId(transaction.getAppointmentId())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .transactionRef(transaction.getTransactionRef())
                .paymentUrl(null)
                .paidAt(transaction.getPaidAt())
                .createdAt(transaction.getCreatedAt())
                .build();

        return ApiResponse.success("Payment completed successfully", response);
    }

    private void validatePaymentAccess(Long appointmentId, Long actorUserId, Role role) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("Appointment", appointmentId));

        if (role == Role.ADMIN) {
            return;
        }

        if (role != Role.PATIENT) {
            throw new UnauthorizedException("Chỉ bệnh nhân hoặc quản trị viên mới được xem giao dịch thanh toán");
        }

        Long patientId = patientRepository.findByUserId(actorUserId)
                .orElseThrow(() -> new com.chrms.domain.exception.EntityNotFoundException("Patient profile not found for user: " + actorUserId))
                .getId();

        if (!appointment.getPatientId().equals(patientId)) {
            throw new UnauthorizedException("Bạn chỉ được xem thanh toán cho lịch hẹn của mình");
        }
    }
}

