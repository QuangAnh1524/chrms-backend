package com.chrms.presentation.controller;

import com.chrms.application.usecase.patient.CreatePaymentTransactionUseCase;
import com.chrms.application.usecase.patient.PaymentInitiationResult;
import com.chrms.domain.entity.PaymentTransaction;
import com.chrms.domain.enums.PaymentStatus;
import com.chrms.domain.repository.PaymentTransactionRepository;
import com.chrms.presentation.dto.request.CreatePaymentRequest;
import com.chrms.presentation.dto.response.ApiResponse;
import com.chrms.presentation.dto.response.PaymentTransactionResponse;
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
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment Transaction APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final CreatePaymentTransactionUseCase createPaymentUseCase;
    private final PaymentTransactionRepository transactionRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment transaction", description = "Create a new payment transaction for an appointment")
    public ApiResponse<PaymentTransactionResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentInitiationResult result = createPaymentUseCase.execute(
                request.getAppointmentId(),
                request.getPaymentMethod(),
                request.getTransactionRef(),
                request.getReturnUrl()
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
    public ApiResponse<List<PaymentTransactionResponse>> getPaymentsByAppointment(@PathVariable Long appointmentId) {
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
    public ApiResponse<PaymentTransactionResponse> completePayment(@PathVariable String transactionRef) {
        PaymentTransaction transaction = createPaymentUseCase.updatePaymentStatus(transactionRef, PaymentStatus.COMPLETED);

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
}

