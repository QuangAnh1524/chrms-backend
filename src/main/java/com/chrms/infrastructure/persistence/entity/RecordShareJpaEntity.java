package com.chrms.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "record_shares")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordShareJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medical_record_id", nullable = false)
    private Long medicalRecordId;

    @Column(name = "from_hospital_id", nullable = false)
    private Long fromHospitalId;

    @Column(name = "to_hospital_id", nullable = false)
    private Long toHospitalId;

    @Column(name = "shared_by", nullable = false)
    private Long sharedBy;

    @Column(name = "shared_at", nullable = false)
    private LocalDateTime sharedAt;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "notes")
    private String notes;
}

