package com.chrms.domain.repository;

import com.chrms.domain.entity.PrescriptionItem;

import java.util.List;

public interface PrescriptionItemRepository {
    PrescriptionItem save(PrescriptionItem item);
    List<PrescriptionItem> findByPrescriptionId(Long prescriptionId);
    void deleteByPrescriptionId(Long prescriptionId);
    void deleteById(Long id);
}

