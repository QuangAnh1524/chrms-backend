package com.chrms.presentation.dto.response;

import com.chrms.domain.entity.MedicalRecord;
import com.chrms.domain.entity.MedicalRecordFile;
import com.chrms.domain.entity.Prescription;
import com.chrms.domain.entity.PrescriptionItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedMedicalRecordResponse {
    private RecordShareResponse share;
    private MedicalRecord medicalRecord;
    private List<MedicalRecordFile> files;
    private Prescription prescription;
    private List<PrescriptionItem> prescriptionItems;
}

