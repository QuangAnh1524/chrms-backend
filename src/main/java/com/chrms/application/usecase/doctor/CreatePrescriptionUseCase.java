package com.chrms.application.usecase.doctor;

import com.chrms.application.dto.command.CreatePrescriptionCommand;
import com.chrms.application.dto.result.PrescriptionResult;
import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.entity.Medicine;
import com.chrms.domain.entity.Prescription;
import com.chrms.domain.entity.PrescriptionItem;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.MedicineRepository;
import com.chrms.domain.repository.PrescriptionItemRepository;
import com.chrms.domain.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatePrescriptionUseCase {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicineRepository medicineRepository;

    @Transactional
    public PrescriptionResult execute(CreatePrescriptionCommand command) {
        // Validate medical record exists
        medicalRecordRepository.findById(command.getMedicalRecordId())
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord", command.getMedicalRecordId()));

        // Check if prescription already exists
        if (prescriptionRepository.findByMedicalRecordId(command.getMedicalRecordId()).isPresent()) {
            throw new BusinessRuleViolationException("Prescription already exists for this medical record");
        }

        // Validate items
        if (command.getItems() == null || command.getItems().isEmpty()) {
            throw new BusinessRuleViolationException("Prescription must have at least one item");
        }

        // Create prescription
        Prescription prescription = Prescription.builder()
                .medicalRecordId(command.getMedicalRecordId())
                .build();

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Create prescription items
        List<PrescriptionItem> items = command.getItems().stream()
                .map(itemDto -> {
                    // Validate medicine exists
                    Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                            .orElseThrow(() -> new EntityNotFoundException("Medicine", itemDto.getMedicineId()));

                    return PrescriptionItem.builder()
                            .prescriptionId(savedPrescription.getId())
                            .medicineId(itemDto.getMedicineId())
                            .dosage(itemDto.getDosage())
                            .frequency(itemDto.getFrequency())
                            .duration(itemDto.getDuration())
                            .quantity(itemDto.getQuantity())
                            .instructions(itemDto.getInstructions())
                            .medicine(medicine)
                            .build();
                })
                .collect(Collectors.toList());

        // Save items
        List<PrescriptionItem> savedItems = items.stream()
                .map(prescriptionItemRepository::save)
                .collect(Collectors.toList());

        // Build result
        List<PrescriptionResult.PrescriptionItemResult> itemResults = savedItems.stream()
                .map(item -> PrescriptionResult.PrescriptionItemResult.builder()
                        .id(item.getId())
                        .medicineId(item.getMedicineId())
                        .medicineName(item.getMedicine() != null ? item.getMedicine().getName() : null)
                        .dosage(item.getDosage())
                        .frequency(item.getFrequency())
                        .duration(item.getDuration())
                        .quantity(item.getQuantity())
                        .instructions(item.getInstructions())
                        .build())
                .collect(Collectors.toList());

        return PrescriptionResult.builder()
                .id(savedPrescription.getId())
                .medicalRecordId(savedPrescription.getMedicalRecordId())
                .items(itemResults)
                .createdAt(savedPrescription.getCreatedAt())
                .updatedAt(savedPrescription.getUpdatedAt())
                .build();
    }
}

