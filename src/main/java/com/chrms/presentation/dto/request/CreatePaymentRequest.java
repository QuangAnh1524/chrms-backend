package com.chrms.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // VNPAY, CASH, CARD

    private String transactionRef; // Optional, will be generated if not provided

    private String returnUrl; // Optional custom return/callback URL
}

