package com.chrms.domain.entity;

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
public class PaymentTransaction {
    private Long id;
    private Long appointmentId;
    private BigDecimal amount;
    private String paymentMethod; // VNPAY, CASH, CARD
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}

