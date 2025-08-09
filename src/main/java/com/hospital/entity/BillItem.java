package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Table(name = "bill_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class BillItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonBackReference("bill-items")
    private Bill bill;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = new BigDecimal("18.00"); // Default GST

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Reference IDs for linking to specific services
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "medical_record_id")
    private Long medicalRecordId;

    @Column(name = "lab_test_id")
    private Long labTestId;

    @Column(name = "pharmacy_item_id")
    private Long pharmacyItemId;

    public enum ServiceType {
        CONSULTATION,
        LAB_TEST,
        PHARMACY,
        PROCEDURE,
        ROOM_CHARGES,
        OTHER
    }

    @PrePersist
    @PreUpdate
    protected void calculateAmounts() {
        if (unitPrice != null && quantity != null) {
            BigDecimal baseAmount = unitPrice.multiply(new BigDecimal(quantity));

            // Calculate discount
            if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                discountAmount = baseAmount.multiply(discountPercentage).divide(new BigDecimal("100"));
            }
            if (discountAmount == null) {
                discountAmount = BigDecimal.ZERO;
            }

            BigDecimal discountedAmount = baseAmount.subtract(discountAmount);

            // Calculate tax
            if (taxPercentage != null && taxPercentage.compareTo(BigDecimal.ZERO) > 0) {
                taxAmount = discountedAmount.multiply(taxPercentage).divide(new BigDecimal("100"));
            }
            if (taxAmount == null) {
                taxAmount = BigDecimal.ZERO;
            }

            totalAmount = discountedAmount.add(taxAmount);
        }
    }
}
