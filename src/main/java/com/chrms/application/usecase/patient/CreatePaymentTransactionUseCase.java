package com.chrms.application.usecase.patient;

import com.chrms.application.port.payment.PaymentGatewayClient;
import com.chrms.application.port.payment.PaymentGatewayResponse;
import com.chrms.domain.entity.Appointment;
import com.chrms.domain.entity.PaymentTransaction;
import com.chrms.domain.enums.PaymentStatus;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.AppointmentRepository;
import com.chrms.domain.repository.PaymentTransactionRepository;
import com.chrms.domain.repository.PatientRepository;
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
    private final PaymentGatewayClient paymentGatewayClient;
    private final PatientRepository patientRepository;

    @Transactional
    public PaymentInitiationResult execute(Long appointmentId,
                                           String paymentMethod,
                                           String transactionRef,
                                           String returnUrl,
                                           Long actorUserId,
                                           Role actorRole) {
        Appointment appointment = validateAppointmentOwnership(appointmentId, actorUserId, actorRole);

        java.math.BigDecimal amount = calculateAmount(appointment);
        PaymentGatewayResponse gatewayResponse = null;

        if (!"CASH".equalsIgnoreCase(paymentMethod)) {
            gatewayResponse = paymentGatewayClient.initiatePayment(
                    amount,
                    "APPOINTMENT-" + appointmentId,
                    returnUrl
            );
            transactionRef = gatewayResponse.getTransactionRef();
        }

        // Generate transaction reference if not provided
        if (transactionRef == null || transactionRef.isEmpty()) {
            transactionRef = generateFallbackTransactionRef();
        }

        // Create transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .appointmentId(appointmentId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status(PaymentStatus.PENDING)
                .transactionRef(transactionRef)
                .build();

        PaymentTransaction saved = transactionRepository.save(transaction);

        return PaymentInitiationResult.builder()
                .transaction(saved)
                .paymentUrl(gatewayResponse != null ? gatewayResponse.getPaymentUrl() : null)
                .build();
    }

    @Transactional
    public PaymentTransaction completePayment(String transactionRef, Long actorUserId, Role actorRole) {
        PaymentTransaction transaction = transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new EntityNotFoundException("PaymentTransaction with reference " + transactionRef + " not found"));

        if (transaction.getStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessRuleViolationException("Payment has already been completed");
        }

        validateAppointmentOwnership(transaction.getAppointmentId(), actorUserId, actorRole);

        transaction.setStatus(PaymentStatus.COMPLETED);
        transaction.setPaidAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    private java.math.BigDecimal calculateAmount(Appointment appointment) {
        // Mock calculation - in real scenario, get from doctor's consultation fee
        return new java.math.BigDecimal("500000"); // 500,000 VND default
    }

    private String generateFallbackTransactionRef() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Appointment validateAppointmentOwnership(Long appointmentId, Long actorUserId, Role actorRole) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));

        if (appointment.getStatus() == null || appointment.getStatus().isTerminal()) {
            throw new BusinessRuleViolationException("Payment cannot be processed for finalized appointments");
        }

        if (actorRole == null) {
            throw new UnauthorizedException("Thiếu thông tin quyền hạn để xử lý thanh toán");
        }

        if (actorRole == Role.ADMIN) {
            return appointment;
        }

        if (actorRole != Role.PATIENT) {
            throw new UnauthorizedException("Only patients or administrators can manage payments");
        }

        Long patientId = patientRepository.findByUserId(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + actorUserId))
                .getId();

        if (!appointment.getPatientId().equals(patientId)) {
            throw new UnauthorizedException("You can only manage payments for your own appointments");
        }

        boolean hasCompletedPayment = transactionRepository.findByAppointmentId(appointmentId).stream()
                .anyMatch(t -> t.getStatus() == PaymentStatus.COMPLETED);

        if (hasCompletedPayment) {
            throw new BusinessRuleViolationException("A completed payment already exists for this appointment");
        }

        return appointment;
    }
}

