package com.hospital.controller;

import com.hospital.dto.BillDTO;
import com.hospital.dto.BillItemDTO;
import com.hospital.entity.Bill;
import com.hospital.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bill Management", description = "APIs for managing hospital bills and billing operations")
public class BillController {

    private final BillService billService;

    @PostMapping
    @Operation(summary = "Create a new bill")
    public ResponseEntity<Bill> createBill(@Valid @RequestBody BillDTO billDTO) {
        log.info("Request received to create bill for patient ID: {}", billDTO.getPatientId());
        Bill bill = billService.createBill(billDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    @PostMapping("/consultation/{appointmentId}")
    @Operation(summary = "Create consultation bill for an appointment")
    public ResponseEntity<Bill> createConsultationBill(@PathVariable Long appointmentId) {
        log.info("Request received to create consultation bill for appointment ID: {}", appointmentId);
        Bill bill = billService.createConsultationBill(appointmentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    @PostMapping("/lab")
    @Operation(summary = "Create lab services bill")
    public ResponseEntity<Bill> createLabBill(
            @RequestParam Long patientId,
            @RequestParam Long hospitalId,
            @Valid @RequestBody List<BillItemDTO> labItems) {
        log.info("Request received to create lab bill for patient ID: {}", patientId);
        Bill bill = billService.createLabBill(patientId, hospitalId, labItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    @PostMapping("/pharmacy")
    @Operation(summary = "Create pharmacy bill")
    public ResponseEntity<Bill> createPharmacyBill(
            @RequestParam Long patientId,
            @RequestParam Long hospitalId,
            @Valid @RequestBody List<BillItemDTO> pharmacyItems) {
        log.info("Request received to create pharmacy bill for patient ID: {}", patientId);
        Bill bill = billService.createPharmacyBill(patientId, hospitalId, pharmacyItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all bills for a patient")
    public ResponseEntity<List<Bill>> getBillsByPatient(@PathVariable Long patientId) {
        log.info("Request received to get bills for patient ID: {}", patientId);
        List<Bill> bills = billService.getBillsByPatient(patientId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/hospital/{hospitalId}")
    @Operation(summary = "Get all bills for a hospital")
    public ResponseEntity<List<Bill>> getBillsByHospital(@PathVariable Long hospitalId) {
        log.info("Request received to get bills for hospital ID: {}", hospitalId);
        List<Bill> bills = billService.getBillsByHospital(hospitalId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/hospital/{hospitalId}/pending")
    @Operation(summary = "Get pending bills for a hospital")
    public ResponseEntity<List<Bill>> getPendingBills(@PathVariable Long hospitalId) {
        log.info("Request received to get pending bills for hospital ID: {}", hospitalId);
        List<Bill> bills = billService.getPendingBills(hospitalId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get all overdue bills")
    public ResponseEntity<List<Bill>> getOverdueBills() {
        log.info("Request received to get overdue bills");
        List<Bill> bills = billService.getOverdueBills();
        return ResponseEntity.ok(bills);
    }

    @PutMapping("/{billId}/status")
    @Operation(summary = "Update bill status")
    public ResponseEntity<Bill> updateBillStatus(
            @PathVariable Long billId,
            @RequestBody Map<String, String> statusRequest) {
        String statusValue = statusRequest.get("status");
        if (statusValue == null || statusValue.trim().isEmpty()) {
            statusValue = "PENDING"; // Default value
        }

        Bill.BillStatus status;
        try {
            status = Bill.BillStatus.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid bill status: {}", statusValue);
            throw new IllegalArgumentException("Invalid bill status: " + statusValue +
                    ". Valid values are: " + java.util.Arrays.toString(Bill.BillStatus.values()));
        }

        log.info("Request received to update bill status for bill ID: {} to {}", billId, status);
        Bill bill = billService.updateBillStatus(billId, status);
        return ResponseEntity.ok(bill);
    }

    @PostMapping("/{billId}/payment")
    @Operation(summary = "Add payment to a bill")
    public ResponseEntity<Bill> addPayment(
            @PathVariable Long billId,
            @RequestParam BigDecimal amount,
            @RequestParam Bill.PaymentMethod paymentMethod,
            @RequestParam(required = false) String paymentReference) {
        log.info("Request received to add payment of {} to bill ID: {}", amount, billId);
        Bill bill = billService.addPayment(billId, amount, paymentMethod, paymentReference);
        return ResponseEntity.ok(bill);
    }
}
