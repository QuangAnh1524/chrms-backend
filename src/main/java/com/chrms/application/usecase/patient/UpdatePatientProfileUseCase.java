package com.chrms.application.usecase.patient;

import com.chrms.application.dto.command.UpdatePatientProfileCommand;
import com.chrms.application.dto.result.PatientProfileResult;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePatientProfileUseCase {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public PatientProfileResult execute(UpdatePatientProfileCommand command) {
        Patient patient = patientRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found for user: " + command.getUserId()));

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", command.getUserId()));

        if (command.getFullName() != null) {
            user.setFullName(command.getFullName());
        }
        if (command.getPhone() != null) {
            user.setPhone(command.getPhone());
        }

        if (command.getDateOfBirth() != null) {
            patient.setDateOfBirth(command.getDateOfBirth());
        }
        if (command.getGender() != null) {
            patient.setGender(command.getGender());
        }
        if (command.getAddress() != null) {
            patient.setAddress(command.getAddress());
        }
        if (command.getEmergencyContact() != null) {
            patient.setEmergencyContact(command.getEmergencyContact());
        }
        if (command.getBloodType() != null) {
            patient.setBloodType(command.getBloodType());
        }
        if (command.getAllergies() != null) {
            patient.setAllergies(command.getAllergies());
        }

        userRepository.save(user);
        Patient updatedPatient = patientRepository.save(patient);

        return PatientProfileResult.builder()
                .patientId(updatedPatient.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .dateOfBirth(updatedPatient.getDateOfBirth())
                .gender(updatedPatient.getGender())
                .address(updatedPatient.getAddress())
                .emergencyContact(updatedPatient.getEmergencyContact())
                .bloodType(updatedPatient.getBloodType())
                .allergies(updatedPatient.getAllergies())
                .createdAt(updatedPatient.getCreatedAt())
                .updatedAt(updatedPatient.getUpdatedAt())
                .build();
    }
}
