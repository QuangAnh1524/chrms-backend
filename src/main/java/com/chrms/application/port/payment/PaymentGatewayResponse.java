package com.chrms.application.port.payment;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PaymentGatewayResponse {
    String transactionRef;
    String paymentUrl;
    LocalDateTime expiredAt;
}
