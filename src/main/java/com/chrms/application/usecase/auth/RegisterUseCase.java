package com.chrms.application.usecase.auth;

import com.chrms.application.dto.command.RegisterCommand;
import com.chrms.application.dto.result.AuthResult;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.Patient;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.PatientRepository;
import com.chrms.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResult execute(RegisterCommand command) {
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessRuleViolationException("Email already exists");
        }

        User user = User.builder()
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .fullName(command.getFullName())
                .phone(command.getPhone())
                .role(command.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Auto create Patient profile if role is PATIENT
        if (savedUser.getRole() == Role.PATIENT) {
            Patient patient = Patient.builder()
                    .userId(savedUser.getId())
                    .dateOfBirth(command.getDateOfBirth())
                    .gender(command.getGender())
                    .address(command.getAddress())
                    .emergencyContact(command.getEmergencyContact())
                    .bloodType(command.getBloodType())
                    .allergies(command.getAllergies())
                    .build();
            patientRepository.save(patient);
        }

        if (savedUser.getRole() == Role.DOCTOR) {
            validateDoctorRegistration(command);
            Doctor doctor = Doctor.builder()
                    .userId(savedUser.getId())
                    .hospitalId(command.getHospitalId())
                    .departmentId(command.getDepartmentId())
                    .specialty(command.getSpecialty())
                    .licenseNumber(command.getLicenseNumber())
                    .experienceYears(command.getExperienceYears() == null ? 0 : command.getExperienceYears())
                    .consultationFee(command.getConsultationFee() == null
                            ? java.math.BigDecimal.ZERO
                            : command.getConsultationFee())
                    .build();
            doctorRepository.save(doctor);
        }

        return AuthResult.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .build();
    }

    private void validateDoctorRegistration(RegisterCommand command) {
        if (command.getHospitalId() == null) {
            throw new BusinessRuleViolationException("Hospital ID is required for doctor registration");
        }
        if (command.getSpecialty() == null || command.getSpecialty().isBlank()) {
            throw new BusinessRuleViolationException("Specialty is required for doctor registration");
        }
        if (command.getLicenseNumber() == null || command.getLicenseNumber().isBlank()) {
            throw new BusinessRuleViolationException("License number is required for doctor registration");
        }
    }
}
