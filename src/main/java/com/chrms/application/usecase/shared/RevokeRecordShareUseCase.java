package com.chrms.application.usecase.shared;

import com.chrms.domain.entity.RecordShare;
import com.chrms.domain.entity.User;
import com.chrms.domain.enums.Role;
import com.chrms.domain.exception.EntityNotFoundException;
import com.chrms.domain.exception.UnauthorizedException;
import com.chrms.domain.repository.MedicalRecordRepository;
import com.chrms.domain.repository.RecordShareRepository;
import com.chrms.domain.repository.UserRepository;
import com.chrms.infrastructure.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RevokeRecordShareUseCase {

    private final RecordShareRepository recordShareRepository;
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final RedisCacheService cacheService;

    @Transactional
    public void execute(Long shareId, Long actorUserId) {
        RecordShare share = recordShareRepository.findById(shareId)
                .orElseThrow(() -> new EntityNotFoundException("RecordShare", shareId));

        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", actorUserId));

        if (!share.getSharedBy().equals(actorUserId) && actor.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Chỉ người tạo chia sẻ hoặc admin mới được thu hồi");
        }

        recordShareRepository.deleteById(shareId);

        // Remove cached access/list entries
        String accessKey = RedisCacheService.CACHE_RECORD_SHARE_ACCESS_PREFIX + share.getToHospitalId() + ":" + share.getMedicalRecordId();
        cacheService.delete(accessKey);
        String sharedListKey = RedisCacheService.CACHE_RECORD_SHARE_SHARED_TO_PREFIX + share.getToHospitalId();
        cacheService.delete(sharedListKey);
        medicalRecordRepository.findById(share.getMedicalRecordId()).ifPresent(record ->
                cacheService.delete(RedisCacheService.CACHE_RECORD_SHARE_SHARED_TO_PREFIX + share.getToHospitalId() + ":patient:" + record.getPatientId()));
    }
}
