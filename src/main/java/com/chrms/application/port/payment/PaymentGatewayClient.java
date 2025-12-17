package com.chrms.application.port.payment;

import java.math.BigDecimal;

public interface PaymentGatewayClient {
    PaymentGatewayResponse initiatePayment(BigDecimal amount, String orderInfo, String returnUrl);
}
