package com.chrms.application.usecase.shared;

import com.chrms.application.dto.result.RecordShareResult;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.entity.RecordShare;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.BusinessRuleViolationException;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.RecordShareRepository;
import com.chrms.domain.repository.UserRepository;
import com.chrms.infrastructure.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShareMedicalRecordUseCase {

    private final MedicalRecordRepository medicalRecordRepository;
    private final RecordShareRepository recordShareRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final RedisCacheService cacheService;

    @Transactional
    public RecordShareResult execute(Long medicalRecordId, Long toHospitalId, String notes, LocalDateTime expiryDate, Long actorUserId) {
        MedicalRecord record = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new EntityNotFoundException("MedicalRecord", medicalRecordId));

        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", actorUserId));

        if (expiryDate != null && expiryDate.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleViolationException("expiryDate must be in the future");
        }

        Long fromHospitalId = record.getHospitalId();

        if (actor.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(actorUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Doctor profile not found for user: " + actorUserId));
            if (!record.getDoctorId().equals(doctor.getId())) {
                throw new UnauthorizedException("Bác sĩ không phụ trách hồ sơ này");
            }
            if (!fromHospitalId.equals(doctor.getHospitalId())) {
                throw new UnauthorizedException("Bác sĩ không thuộc bệnh viện sở hữu hồ sơ");
            }
        } else if (actor.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Chỉ bác sĩ phụ trách hoặc admin mới được chia sẻ hồ sơ");
        }

        RecordShare share = RecordShare.builder()
                .medicalRecordId(medicalRecordId)
                .fromHospitalId(fromHospitalId)
                .toHospitalId(toHospitalId)
                .sharedBy(actorUserId)
                .sharedAt(LocalDateTime.now())
                .expiryDate(expiryDate)
                .notes(notes)
                .build();

        RecordShare saved = recordShareRepository.save(share);

        // Invalidate cached shared lists for the destination hospital
        String sharedListCacheKey = RedisCacheService.CACHE_RECORD_SHARE_SHARED_TO_PREFIX + toHospitalId;
        cacheService.delete(sharedListCacheKey);
        String patientCacheKey = RedisCacheService.CACHE_RECORD_SHARE_SHARED_TO_PREFIX + toHospitalId + ":patient:" + record.getPatientId();
        cacheService.delete(patientCacheKey);

        // Cache access flag for quick authorization checks
        String accessKey = RedisCacheService.CACHE_RECORD_SHARE_ACCESS_PREFIX + toHospitalId + ":" + medicalRecordId;
        if (expiryDate != null) {
            Duration ttl = Duration.between(LocalDateTime.now(), expiryDate);
            if (!ttl.isNegative() && !ttl.isZero()) {
                cacheService.set(accessKey, true, ttl.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
            }
        } else {
            cacheService.set(accessKey, true);
        }

        return RecordShareResult.builder()
                .id(saved.getId())
                .medicalRecordId(saved.getMedicalRecordId())
                .fromHospitalId(saved.getFromHospitalId())
                .toHospitalId(saved.getToHospitalId())
                .sharedBy(saved.getSharedBy())
                .sharedAt(saved.getSharedAt())
                .expiryDate(saved.getExpiryDate())
                .notes(saved.getNotes())
                .build();
    }
}
