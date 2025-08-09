package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Table(name = "service_catalog")
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceCatalog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_code", unique = true, nullable = false)
    private String serviceCode;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ServiceCategory category;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = new BigDecimal("18.00");

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    @JsonBackReference("hospital-services")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonBackReference("department-services")
    private Department department;

    public enum ServiceType {
        CONSULTATION,
        LAB_TEST,
        PHARMACY,
        PROCEDURE,
        ROOM_CHARGES,
        DIAGNOSTIC,
        THERAPY,
        OTHER
    }

    public enum ServiceCategory {
        // Consultation categories
        GENERAL_CONSULTATION,
        SPECIALIST_CONSULTATION,
        EMERGENCY_CONSULTATION,
        FOLLOW_UP_CONSULTATION,

        // Lab categories
        BLOOD_TEST,
        URINE_TEST,
        IMAGING,
        PATHOLOGY,
        MICROBIOLOGY,
        BIOCHEMISTRY,

        // Pharmacy categories
        MEDICINE,
        INJECTION,
        SUPPLEMENT,
        MEDICAL_DEVICE,

        // Other categories
        ROOM_CHARGE,
        PROCEDURE_CHARGE,
        EQUIPMENT_CHARGE,
        MISCELLANEOUS
    }
}
