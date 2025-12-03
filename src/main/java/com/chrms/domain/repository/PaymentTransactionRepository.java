package com.chrms.domain.repository;

import com.chrms.domain.entity.PaymentTransaction;
import com.chrms.domain.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction transaction);
    Optional<PaymentTransaction> findById(Long id);
    Optional<PaymentTransaction> findByTransactionRef(String transactionRef);
    List<PaymentTransaction> findByAppointmentId(Long appointmentId);
    List<PaymentTransaction> findByStatus(PaymentStatus status);
    List<PaymentTransaction> findAll();
}

