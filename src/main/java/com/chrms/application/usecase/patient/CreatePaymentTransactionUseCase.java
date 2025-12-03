package com.chrms.application.usecase.patient;

import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.PaymentTransaction;
import com.chrms.domain.enums.PaymentStatus;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionUseCase {
    private final PaymentTransactionRepository transactionRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public PaymentTransaction execute(Long appointmentId, String paymentMethod, String transactionRef) {
        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

        // Generate transaction reference if not provided
        if (transactionRef == null || transactionRef.isEmpty()) {
            transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        // Create transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .appointmentId(appointmentId)
                .amount(calculateAmount(appointment)) // You can implement this based on doctor fee
                .paymentMethod(paymentMethod)
                .status(PaymentStatus.PENDING)
                .transactionRef(transactionRef)
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public PaymentTransaction updatePaymentStatus(String transactionRef, PaymentStatus status) {
        PaymentTransaction transaction = transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new EntityNotFoundException("PaymentTransaction with reference " + transactionRef + " not found"));

        transaction.setStatus(status);
        if (status == PaymentStatus.COMPLETED) {
            transaction.setPaidAt(LocalDateTime.now());
        }

        return transactionRepository.save(transaction);
    }

    private java.math.BigDecimal calculateAmount(Appointment appointment) {
        // Mock calculation - in real scenario, get from doctor's consultation fee
        return new java.math.BigDecimal("500000"); // 500,000 VND default
    }
}

