package com.chrms.infrastructure.persistence.repository;

import com.chrms.domain.enums.PaymentStatus;
import com.chrms.infrastructure.persistence.entity.PaymentTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionJpaRepository extends JpaRepository<PaymentTransactionJpaEntity, Long> {
    Optional<PaymentTransactionJpaEntity> findByTransactionRef(String transactionRef);
    List<PaymentTransactionJpaEntity> findByAppointmentId(Long appointmentId);
    List<PaymentTransactionJpaEntity> findByStatus(PaymentStatus status);
}

