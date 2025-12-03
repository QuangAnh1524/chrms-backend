package com.chrms.presentation.dto.response;

import com.chrms.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponse {
    private Long id;
    private Long appointmentId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}

