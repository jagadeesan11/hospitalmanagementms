package com.hospital.repository;

import com.hospital.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);

    List<Bill> findByPatientId(Long patientId);

    List<Bill> findByPatientIdAndStatus(Long patientId, Bill.BillStatus status);

    List<Bill> findByHospitalId(Long hospitalId);

    List<Bill> findByHospitalIdAndStatus(Long hospitalId, Bill.BillStatus status);

    List<Bill> findByStatus(Bill.BillStatus status);

    List<Bill> findByBillDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Bill> findByHospitalIdAndBillDateBetween(Long hospitalId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT b FROM Bill b WHERE b.dueDate < :currentDate AND b.status IN ('PENDING', 'PARTIALLY_PAID')")
    List<Bill> findOverdueBills(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Bill b WHERE b.patient.id = :patientId AND b.status IN ('PENDING', 'PARTIALLY_PAID')")
    List<Bill> findPendingBillsByPatient(@Param("patientId") Long patientId);

    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.hospital.id = :hospitalId AND b.billDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByHospitalAndDateRange(@Param("hospitalId") Long hospitalId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.hospital.id = :hospitalId AND b.status = :status")
    Long countBillsByHospitalAndStatus(@Param("hospitalId") Long hospitalId, @Param("status") Bill.BillStatus status);

    @Query("SELECT SUM(b.paidAmount) FROM Bill b WHERE b.hospital.id = :hospitalId AND b.billDate BETWEEN :startDate AND :endDate")
    Double getTotalCollectionsByHospitalAndDateRange(@Param("hospitalId") Long hospitalId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(b.balanceAmount) FROM Bill b WHERE b.hospital.id = :hospitalId AND b.status IN ('PENDING', 'PARTIALLY_PAID')")
    Double getTotalOutstandingByHospital(@Param("hospitalId") Long hospitalId);

    @Query("SELECT b FROM Bill b WHERE b.hospital.id = :hospitalId AND b.paymentMethod = :paymentMethod AND b.billDate BETWEEN :startDate AND :endDate")
    List<Bill> findByHospitalAndPaymentMethodAndDateRange(@Param("hospitalId") Long hospitalId,
                                                         @Param("paymentMethod") Bill.PaymentMethod paymentMethod,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
}
