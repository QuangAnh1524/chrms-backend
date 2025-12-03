package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.PaymentTransaction;
import com.chrms.domain.enums.PaymentStatus;
import com.chrms.domain.repository.PaymentTransactionRepository;
import com.chrms.infrastructure.persistence.entity.PaymentTransactionJpaEntity;
import com.chrms.infrastructure.persistence.repository.PaymentTransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentTransactionRepositoryAdapter implements PaymentTransactionRepository {

    private final PaymentTransactionJpaRepository jpaRepository;

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        PaymentTransactionJpaEntity entity = toEntity(transaction);
        PaymentTransactionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PaymentTransaction> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PaymentTransaction> findByTransactionRef(String transactionRef) {
        return jpaRepository.findByTransactionRef(transactionRef).map(this::toDomain);
    }

    @Override
    public List<PaymentTransaction> findByAppointmentId(Long appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentTransaction> findByStatus(PaymentStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentTransaction> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private PaymentTransaction toDomain(PaymentTransactionJpaEntity entity) {
        return PaymentTransaction.builder()
                .id(entity.getId())
                .appointmentId(entity.getAppointmentId())
                .amount(entity.getAmount())
                .paymentMethod(entity.getPaymentMethod())
                .status(entity.getStatus())
                .transactionRef(entity.getTransactionRef())
                .paidAt(entity.getPaidAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private PaymentTransactionJpaEntity toEntity(PaymentTransaction domain) {
        return PaymentTransactionJpaEntity.builder()
                .id(domain.getId())
                .appointmentId(domain.getAppointmentId())
                .amount(domain.getAmount())
                .paymentMethod(domain.getPaymentMethod())
                .status(domain.getStatus())
                .transactionRef(domain.getTransactionRef())
                .paidAt(domain.getPaidAt())
                .build();
    }
}

