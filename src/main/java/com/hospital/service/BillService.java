package com.hospital.service;

import com.hospital.dto.BillDTO;
import com.hospital.dto.BillItemDTO;
import com.hospital.entity.*;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public Bill createBill(BillDTO billDTO) {
        log.info("Creating bill for patient ID: {}", billDTO.getPatientId());

        Patient patient = patientRepository.findById(billDTO.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + billDTO.getPatientId()));

        Hospital hospital = hospitalRepository.findById(billDTO.getHospitalId())
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + billDTO.getHospitalId()));

        Bill bill = new Bill();
        bill.setBillNumber(generateBillNumber(hospital.getId()));
        bill.setPatient(patient);
        bill.setHospital(hospital);
        bill.setBillDate(billDTO.getBillDate() != null ? billDTO.getBillDate() : LocalDateTime.now());
        bill.setDueDate(billDTO.getDueDate() != null ? billDTO.getDueDate() : LocalDateTime.now().plusDays(30));
        bill.setStatus(billDTO.getStatus() != null ? billDTO.getStatus() : Bill.BillStatus.PENDING);
        bill.setDiscountAmount(billDTO.getDiscountAmount() != null ? billDTO.getDiscountAmount() : BigDecimal.ZERO);
        bill.setNotes(billDTO.getNotes());

        Bill savedBill = billRepository.save(bill);

        // Create bill items
        List<BillItem> billItems = billDTO.getBillItems().stream()
            .map(itemDTO -> createBillItem(itemDTO, savedBill))
            .collect(Collectors.toList());

        billItemRepository.saveAll(billItems);
        savedBill.setBillItems(billItems);

        // Calculate totals
        savedBill.calculateTotals();

        return billRepository.save(savedBill);
    }

    @Transactional
    public Bill createConsultationBill(Long appointmentId) {
        log.info("Creating consultation bill for appointment ID: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        // Check if bill already exists for this appointment
        List<BillItem> existingItems = billItemRepository.findByAppointmentId(appointmentId);
        if (!existingItems.isEmpty()) {
            throw new IllegalStateException("Bill already exists for appointment ID: " + appointmentId);
        }

        BillDTO billDTO = new BillDTO();
        billDTO.setPatientId(appointment.getPatient().getId());
        billDTO.setHospitalId(appointment.getDoctor().getHospital().getId());
        billDTO.setNotes("Consultation bill for appointment: " + appointment.getId());

        // Create consultation bill item
        BillItemDTO consultationItem = new BillItemDTO();
        consultationItem.setServiceType(BillItem.ServiceType.CONSULTATION);
        consultationItem.setServiceName("Doctor Consultation - " + appointment.getDoctor().getName());
        consultationItem.setDescription("Consultation with " + appointment.getDoctor().getSpecialization());
        consultationItem.setQuantity(1);
        consultationItem.setUnitPrice(getConsultationFee(appointment.getDoctor()));
        consultationItem.setTaxPercentage(new BigDecimal("18.00"));
        consultationItem.setAppointmentId(appointmentId);

        billDTO.setBillItems(List.of(consultationItem));

        return createBill(billDTO);
    }

    @Transactional
    public Bill createLabBill(Long patientId, Long hospitalId, List<BillItemDTO> labItems) {
        log.info("Creating lab bill for patient ID: {} with {} items", patientId, labItems.size());

        BillDTO billDTO = new BillDTO();
        billDTO.setPatientId(patientId);
        billDTO.setHospitalId(hospitalId);
        billDTO.setNotes("Laboratory services bill");

        // Set service type for all items
        labItems.forEach(item -> {
            item.setServiceType(BillItem.ServiceType.LAB_TEST);
            if (item.getTaxPercentage() == null) {
                item.setTaxPercentage(new BigDecimal("18.00"));
            }
        });

        billDTO.setBillItems(labItems);

        return createBill(billDTO);
    }

    @Transactional
    public Bill createPharmacyBill(Long patientId, Long hospitalId, List<BillItemDTO> pharmacyItems) {
        log.info("Creating pharmacy bill for patient ID: {} with {} items", patientId, pharmacyItems.size());

        BillDTO billDTO = new BillDTO();
        billDTO.setPatientId(patientId);
        billDTO.setHospitalId(hospitalId);
        billDTO.setNotes("Pharmacy services bill");

        // Set service type for all items
        pharmacyItems.forEach(item -> {
            item.setServiceType(BillItem.ServiceType.PHARMACY);
            if (item.getTaxPercentage() == null) {
                item.setTaxPercentage(new BigDecimal("5.00")); // Lower tax for medicines
            }
        });

        billDTO.setBillItems(pharmacyItems);

        return createBill(billDTO);
    }

    @Transactional
    public Bill updateBillStatus(Long billId, Bill.BillStatus status) {
        log.info("Updating bill status for bill ID: {} to {}", billId, status);

        Bill bill = billRepository.findById(billId)
            .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        bill.setStatus(status);
        return billRepository.save(bill);
    }

    @Transactional
    public Bill addPayment(Long billId, BigDecimal paymentAmount, Bill.PaymentMethod paymentMethod, String paymentReference) {
        log.info("Adding payment of {} to bill ID: {}", paymentAmount, billId);

        Bill bill = billRepository.findById(billId)
            .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        BigDecimal currentPaid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = currentPaid.add(paymentAmount);

        bill.setPaidAmount(newPaidAmount);
        bill.setPaymentMethod(paymentMethod);
        bill.setPaymentReference(paymentReference);

        // Update status based on payment
        BigDecimal totalAmount = bill.getTotalAmount();
        if (newPaidAmount.compareTo(totalAmount) >= 0) {
            bill.setStatus(Bill.BillStatus.PAID);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            bill.setStatus(Bill.BillStatus.PARTIALLY_PAID);
        }

        return billRepository.save(bill);
    }

    public List<Bill> getBillsByPatient(Long patientId) {
        return billRepository.findByPatientId(patientId);
    }

    public List<Bill> getBillsByHospital(Long hospitalId) {
        return billRepository.findByHospitalId(hospitalId);
    }

    public List<Bill> getPendingBills(Long hospitalId) {
        return billRepository.findByHospitalIdAndStatus(hospitalId, Bill.BillStatus.PENDING);
    }

    public List<Bill> getOverdueBills() {
        return billRepository.findOverdueBills(LocalDateTime.now());
    }


    private BillItem createBillItem(BillItemDTO itemDTO, Bill bill) {
        BillItem item = new BillItem();
        item.setBill(bill);
        item.setServiceType(itemDTO.getServiceType());
        item.setServiceName(itemDTO.getServiceName());
        item.setDescription(itemDTO.getDescription());
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getUnitPrice());
        item.setDiscountPercentage(itemDTO.getDiscountPercentage());
        item.setDiscountAmount(itemDTO.getDiscountAmount());
        item.setTaxPercentage(itemDTO.getTaxPercentage());
        item.setAppointmentId(itemDTO.getAppointmentId());
        item.setMedicalRecordId(itemDTO.getMedicalRecordId());
        item.setLabTestId(itemDTO.getLabTestId());
        item.setPharmacyItemId(itemDTO.getPharmacyItemId());

        return item;
    }

    private String generateBillNumber(Long hospitalId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return String.format("BILL-%d-%s", hospitalId, timestamp);
    }

    private BigDecimal getConsultationFee(Doctor doctor) {
        // Default consultation fees based on specialization
        // In a real application, this would come from a service catalog
        switch (doctor.getSpecialization().toUpperCase()) {
            case "GENERAL PHYSICIAN":
                return new BigDecimal("500.00");
            case "CARDIOLOGIST":
                return new BigDecimal("1000.00");
            case "NEUROLOGIST":
                return new BigDecimal("1200.00");
            case "ORTHOPEDIC":
                return new BigDecimal("800.00");
            case "PEDIATRICIAN":
                return new BigDecimal("600.00");
            default:
                return new BigDecimal("700.00");
        }
    }
}
