package com.hospital.repository;

import com.hospital.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    List<BillItem> findByBillId(Long billId);

    List<BillItem> findByServiceType(BillItem.ServiceType serviceType);

    List<BillItem> findByAppointmentId(Long appointmentId);

    List<BillItem> findByMedicalRecordId(Long medicalRecordId);

    List<BillItem> findByLabTestId(Long labTestId);

    List<BillItem> findByPharmacyItemId(Long pharmacyItemId);

    @Query("SELECT bi FROM BillItem bi WHERE bi.bill.hospital.id = :hospitalId AND bi.serviceType = :serviceType AND bi.createdAt BETWEEN :startDate AND :endDate")
    List<BillItem> findByHospitalAndServiceTypeAndDateRange(@Param("hospitalId") Long hospitalId,
                                                           @Param("serviceType") BillItem.ServiceType serviceType,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(bi.totalAmount) FROM BillItem bi WHERE bi.bill.hospital.id = :hospitalId AND bi.serviceType = :serviceType AND bi.createdAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByServiceType(@Param("hospitalId") Long hospitalId,
                                       @Param("serviceType") BillItem.ServiceType serviceType,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
}
