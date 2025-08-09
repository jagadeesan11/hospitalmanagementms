package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bill")
@Data
@EqualsAndHashCode(callSuper = true)
public class Bill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", unique = true, nullable = false)
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(name = "bill_date", nullable = false)
    private LocalDateTime billDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BillStatus status = BillStatus.PENDING;

    @Column(name = "sub_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "balance_amount", precision = 10, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("bill-items")
    private List<BillItem> billItems;

    public enum BillStatus {
        PENDING,
        PARTIALLY_PAID,
        PAID,
        CANCELLED,
        OVERDUE
    }

    public enum PaymentMethod {
        CASH,
        CARD,
        UPI,
        NET_BANKING,
        CHEQUE,
        INSURANCE
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (billDate == null) {
            billDate = LocalDateTime.now();
        }
        if (dueDate == null) {
            dueDate = billDate.plusDays(30); // Default 30 days payment term
        }
        updateBalanceAmount();
    }

    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        updateBalanceAmount();
    }

    private void updateBalanceAmount() {
        if (totalAmount != null && paidAmount != null) {
            balanceAmount = totalAmount.subtract(paidAmount);
        }
    }

    public void calculateTotals() {
        if (billItems != null && !billItems.isEmpty()) {
            subTotal = billItems.stream()
                .map(BillItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate total with tax and discount
            BigDecimal taxableAmount = subTotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
            BigDecimal calculatedTax = taxableAmount.multiply(new BigDecimal("0.18")); // 18% GST

            if (taxAmount == null) {
                taxAmount = calculatedTax;
            }

            totalAmount = subTotal.add(taxAmount).subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
            updateBalanceAmount();
        }
    }
}
