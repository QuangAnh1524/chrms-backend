package com.chrms.application.usecase.shared;

import com.chrms.application.dto.result.RecordShareResult;
import com.chrms.application.dto.result.SharedMedicalRecordResult;
import com.chrms.domain.entity.Doctor;
import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.entity.Prescription;
import com.chrms.domain.entity.RecordShare;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.DoctorRepository;
import com.chrms.domain.repository.MedicalRecordFileRepository;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.PrescriptionItemRepository;
import com.chrms.domain.repository.PrescriptionRepository;
import com.chrms.domain.repository.RecordShareRepository;
import com.chrms.infrastructure.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetSharedMedicalRecordsUseCase {

    private final RecordShareRepository recordShareRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordFileRepository fileRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final DoctorRepository doctorRepository;
    private final RedisCacheService cacheService;

    @SuppressWarnings("unchecked")
    public List<SharedMedicalRecordResult> execute(Long actorUserId, Role actorRole, Long patientIdFilter) {
        if (actorRole != Role.DOCTOR && actorRole != Role.ADMIN) {
            throw new UnauthorizedException("Chỉ bác sĩ hoặc admin mới được xem hồ sơ được chia sẻ");
        }

        Doctor doctor = doctorRepository.findByUserId(actorUserId)
                .orElseThrow(() -> new UnauthorizedException("Tài khoản cần gắn với hồ sơ bác sĩ để xem hồ sơ được chia sẻ"));

        Long hospitalId = doctor.getHospitalId();
        String cacheKey = RedisCacheService.CACHE_RECORD_SHARE_SHARED_TO_PREFIX + hospitalId +
                (patientIdFilter != null ? (":patient:" + patientIdFilter) : ":all");

        Object cached = cacheService.get(cacheKey);
        if (cached != null) {
            return (List<SharedMedicalRecordResult>) cached;
        }

        List<RecordShare> shares = recordShareRepository.findByToHospitalId(hospitalId);
        LocalDateTime now = LocalDateTime.now();
        List<SharedMedicalRecordResult> results = new ArrayList<>();

        for (RecordShare share : shares) {
            if (share.getExpiryDate() != null && share.getExpiryDate().isBefore(now)) {
                continue;
            }

            MedicalRecord record = medicalRecordRepository.findById(share.getMedicalRecordId())
                    .orElse(null);
            if (record == null) {
                continue;
            }

            if (patientIdFilter != null && !patientIdFilter.equals(record.getPatientId())) {
                continue;
            }

            SharedMedicalRecordResult result = SharedMedicalRecordResult.builder()
                    .share(mapToResult(share))
                    .medicalRecord(record)
                    .files(fileRepository.findByMedicalRecordId(record.getId()))
                    .prescription(prescriptionRepository.findByMedicalRecordId(record.getId()).orElse(null))
                    .build();

            Prescription prescription = result.getPrescription();
            if (prescription != null) {
                result.setPrescriptionItems(
                        prescriptionItemRepository.findByPrescriptionId(prescription.getId()).stream()
                                .collect(Collectors.toList())
                );
            } else {
                result.setPrescriptionItems(List.of());
            }

            // Cache access key for quick authorization checks
            String accessKey = RedisCacheService.CACHE_RECORD_SHARE_ACCESS_PREFIX + hospitalId + ":" + record.getId();
            if (share.getExpiryDate() != null) {
                long ttlMillis = java.time.Duration.between(now, share.getExpiryDate()).toMillis();
                if (ttlMillis > 0) {
                    cacheService.set(accessKey, true, ttlMillis, TimeUnit.MILLISECONDS);
                }
            } else {
                cacheService.set(accessKey, true);
            }

            results.add(result);
        }

        cacheService.set(cacheKey, results, 5, TimeUnit.MINUTES);
        return results;
    }

    private RecordShareResult mapToResult(RecordShare share) {
        return RecordShareResult.builder()
                .id(share.getId())
                .medicalRecordId(share.getMedicalRecordId())
                .fromHospitalId(share.getFromHospitalId())
                .toHospitalId(share.getToHospitalId())
                .sharedBy(share.getSharedBy())
                .sharedAt(share.getSharedAt())
                .expiryDate(share.getExpiryDate())
                .notes(share.getNotes())
                .build();
    }
}
