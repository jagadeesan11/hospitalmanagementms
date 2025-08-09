package com.hospital.dto;

import com.hospital.entity.Bill;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillDTO {

    private Long id;

    private String billNumber;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    private LocalDateTime billDate;

    private LocalDateTime dueDate;

    private Bill.BillStatus status;

    @DecimalMin(value = "0.0", message = "Sub total must be positive")
    private BigDecimal subTotal;

    @DecimalMin(value = "0.0", message = "Tax amount must be positive")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0.0", message = "Discount amount must be positive")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.0", message = "Paid amount must be positive")
    private BigDecimal paidAmount;

    private BigDecimal balanceAmount;

    private Bill.PaymentMethod paymentMethod;

    private String paymentReference;

    private String notes;

    @NotEmpty(message = "Bill must have at least one item")
    @Valid
    private List<BillItemDTO> billItems;
}
