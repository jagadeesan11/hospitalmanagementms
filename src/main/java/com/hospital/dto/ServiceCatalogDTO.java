package com.hospital.dto;

import com.hospital.entity.ServiceCatalog;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Data
public class ServiceCatalogDTO {

    private Long id;

    @NotBlank(message = "Service code is required")
    private String serviceCode;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    private String description;

    @NotNull(message = "Service type is required")
    private ServiceCatalog.ServiceType serviceType;

    @NotNull(message = "Service category is required")
    private ServiceCatalog.ServiceCategory category;

    @DecimalMin(value = "0.0", message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", message = "Tax percentage must be positive")
    private BigDecimal taxPercentage;

    private Boolean isActive = true;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    private Long departmentId;
}
