package com.hospital.dto;

import com.hospital.entity.BillItem;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@Data
public class BillItemDTO {

    private Long id;

    @NotNull(message = "Service type is required")
    private BillItem.ServiceType serviceType;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    private String description;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    @DecimalMin(value = "0.0", message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", message = "Discount percentage must be positive")
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", message = "Discount amount must be positive")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Tax percentage must be positive")
    private BigDecimal taxPercentage;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    // Reference IDs for linking to specific services
    private Long appointmentId;
    private Long medicalRecordId;
    private Long labTestId;
    private Long pharmacyItemId;
}
