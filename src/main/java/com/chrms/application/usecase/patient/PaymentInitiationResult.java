package com.chrms.application.usecase.patient;

import com.chrms.domain.entity.PaymentTransaction;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentInitiationResult {
    PaymentTransaction transaction;
    String paymentUrl;
}
