package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "appointments", "hospital"})
    private Patient patient;

    @Column(nullable = false)
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String prescriptions;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ElementCollection
    @CollectionTable(name = "medical_record_allergies")
    private List<String> allergies = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "medical_record_vital_signs")
    private List<VitalSign> vitalSigns = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "appointments", "department", "hospital"})
    private Doctor treatingDoctor;

    @Column(nullable = false)
    private LocalDateTime visitDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    public enum RecordType {
        REGULAR_CHECKUP,
        EMERGENCY,
        FOLLOW_UP,
        SURGERY,
        LAB_TEST,
        VACCINATION,
        PRESCRIPTION,
        CONSULTATION
    }

    @Embeddable
    @Data
    public static class VitalSign {
        private String type; // e.g., "Blood Pressure", "Temperature", "Heart Rate"
        private String value;
        private String unit;
        private LocalDateTime recordedAt;
    }
}
