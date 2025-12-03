package com.chrms.infrastructure.persistence.adapter;

import com.chrms.domain.entity.MedicalRecordFile;
import com.chrms.domain.enums.FileType;
import com.chrms.domain.repository.MedicalRecordFileRepository;
import com.chrms.infrastructure.persistence.entity.MedicalRecordFileJpaEntity;
import com.chrms.infrastructure.persistence.repository.MedicalRecordFileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MedicalRecordFileRepositoryAdapter implements MedicalRecordFileRepository {

    private final MedicalRecordFileJpaRepository jpaRepository;

    @Override
    public MedicalRecordFile save(MedicalRecordFile file) {
        MedicalRecordFileJpaEntity entity = toEntity(file);
        MedicalRecordFileJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MedicalRecordFile> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<MedicalRecordFile> findByMedicalRecordId(Long medicalRecordId) {
        return jpaRepository.findByMedicalRecordId(medicalRecordId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecordFile> findByMedicalRecordIdAndFileType(Long medicalRecordId, FileType fileType) {
        return jpaRepository.findByMedicalRecordIdAndFileType(medicalRecordId, fileType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private MedicalRecordFile toDomain(MedicalRecordFileJpaEntity entity) {
        return MedicalRecordFile.builder()
                .id(entity.getId())
                .medicalRecordId(entity.getMedicalRecordId())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .uploadedBy(entity.getUploadedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private MedicalRecordFileJpaEntity toEntity(MedicalRecordFile domain) {
        return MedicalRecordFileJpaEntity.builder()
                .id(domain.getId())
                .medicalRecordId(domain.getMedicalRecordId())
                .fileName(domain.getFileName())
                .filePath(domain.getFilePath())
                .fileType(domain.getFileType())
                .fileSize(domain.getFileSize())
                .uploadedBy(domain.getUploadedBy())
                .build();
    }
}

