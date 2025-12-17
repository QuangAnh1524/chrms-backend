package com.chrms.application.usecase.patient;

import com.chrms.application.dto.result.PatientProfileResult;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPatientProfileUseCase {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientProfileResult execute(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + userId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        return PatientProfileResult.builder()
                .patientId(patient.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .emergencyContact(patient.getEmergencyContact())
                .bloodType(patient.getBloodType())
                .allergies(patient.getAllergies())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
