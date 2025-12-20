package com.chrms.application.usecase.shared;

import com.chrms.application.dto.result.RecordShareResult;
import com.chrms.domain.repository.RecordShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMyRecordSharesUseCase {

    private final RecordShareRepository recordShareRepository;

    public List<RecordShareResult> execute(Long actorUserId) {
        return recordShareRepository.findBySharedBy(actorUserId).stream()
                .map(share -> RecordShareResult.builder()
                        .id(share.getId())
                        .medicalRecordId(share.getMedicalRecordId())
                        .fromHospitalId(share.getFromHospitalId())
                        .toHospitalId(share.getToHospitalId())
                        .sharedBy(share.getSharedBy())
                        .sharedAt(share.getSharedAt())
                        .expiryDate(share.getExpiryDate())
                        .notes(share.getNotes())
                        .build())
                .collect(Collectors.toList());
    }
}

