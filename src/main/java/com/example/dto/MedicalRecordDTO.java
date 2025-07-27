package com.example.dto;

import com.example.entity.MedicalRecord;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MedicalRecordDTO {
    private String diagnosis;
    private String symptoms;
    private String treatment;
    private String prescriptions;
    private String notes;
    private List<String> allergies = new ArrayList<>();
    private List<VitalSignDTO> vitalSigns = new ArrayList<>();
    private Long patientId;
    private Long doctorId;
    private LocalDateTime visitDate;
    private MedicalRecord.RecordType recordType;

    @Data
    public static class VitalSignDTO {
        private String type;
        private String value;
        private String unit;
        private LocalDateTime recordedAt;
    }
}
