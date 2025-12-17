package com.chrms.infrastructure.integration.payment;

import com.chrms.application.port.payment.PaymentGatewayClient;
import com.chrms.application.port.payment.PaymentGatewayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class MockPaymentGatewayClient implements PaymentGatewayClient {

    @Value("${payment.mock.return-url:http://localhost:8080/api/v1/payments/complete}")
    private String defaultReturnUrl;

    @Override
    public PaymentGatewayResponse initiatePayment(BigDecimal amount, String orderInfo, String returnUrl) {
        String resolvedReturnUrl = (returnUrl == null || returnUrl.isBlank()) ? defaultReturnUrl : returnUrl;
        String transactionRef = "VNPAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        String returnUrlWithRef = resolvedReturnUrl.contains("?")
                ? resolvedReturnUrl + "&transactionRef=" + transactionRef
                : resolvedReturnUrl + "?transactionRef=" + transactionRef;

        String encodedReturnUrl = URLEncoder.encode(returnUrlWithRef, StandardCharsets.UTF_8);
        String encodedOrder = URLEncoder.encode(orderInfo, StandardCharsets.UTF_8);
        String paymentUrl = String.format(
                "https://sandbox.vnpay.vn/pay?txnRef=%s&amount=%s&returnUrl=%s&orderInfo=%s",
                transactionRef,
                amount.toPlainString(),
                encodedReturnUrl,
                encodedOrder
        );

        log.info("[MOCK PAYMENT] Init payment ref={} amount={} returnUrl={} orderInfo={}", transactionRef, amount, resolvedReturnUrl, orderInfo);

        return PaymentGatewayResponse.builder()
                .transactionRef(transactionRef)
                .paymentUrl(paymentUrl)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();
    }
}
