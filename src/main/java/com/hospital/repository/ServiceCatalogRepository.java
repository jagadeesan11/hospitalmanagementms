package com.hospital.repository;

import com.hospital.entity.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {

    Optional<ServiceCatalog> findByServiceCode(String serviceCode);

    List<ServiceCatalog> findByHospitalId(Long hospitalId);

    List<ServiceCatalog> findByHospitalIdAndIsActive(Long hospitalId, Boolean isActive);

    List<ServiceCatalog> findByServiceType(ServiceCatalog.ServiceType serviceType);

    List<ServiceCatalog> findByHospitalIdAndServiceType(Long hospitalId, ServiceCatalog.ServiceType serviceType);

    List<ServiceCatalog> findByCategory(ServiceCatalog.ServiceCategory category);

    List<ServiceCatalog> findByHospitalIdAndCategory(Long hospitalId, ServiceCatalog.ServiceCategory category);

    List<ServiceCatalog> findByDepartmentId(Long departmentId);

    List<ServiceCatalog> findByDepartmentIdAndIsActive(Long departmentId, Boolean isActive);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.hospital.id = :hospitalId AND sc.serviceName LIKE %:serviceName% AND sc.isActive = true")
    List<ServiceCatalog> findByHospitalAndServiceNameContaining(@Param("hospitalId") Long hospitalId,
                                                               @Param("serviceName") String serviceName);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.hospital.id = :hospitalId AND sc.serviceType = :serviceType AND sc.isActive = true ORDER BY sc.serviceName")
    List<ServiceCatalog> findActiveServicesByHospitalAndType(@Param("hospitalId") Long hospitalId,
                                                            @Param("serviceType") ServiceCatalog.ServiceType serviceType);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.hospital.id = :hospitalId AND sc.category = :category AND sc.isActive = true ORDER BY sc.unitPrice")
    List<ServiceCatalog> findActiveServicesByHospitalAndCategoryOrderByPrice(@Param("hospitalId") Long hospitalId,
                                                                            @Param("category") ServiceCatalog.ServiceCategory category);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.department.id = :departmentId AND sc.serviceType = :serviceType AND sc.isActive = true")
    List<ServiceCatalog> findByDepartmentAndServiceTypeAndIsActive(@Param("departmentId") Long departmentId,
                                                                  @Param("serviceType") ServiceCatalog.ServiceType serviceType);

    @Query("SELECT COUNT(sc) FROM ServiceCatalog sc WHERE sc.hospital.id = :hospitalId AND sc.isActive = true")
    Long countActiveServicesByHospital(@Param("hospitalId") Long hospitalId);

    @Query("SELECT DISTINCT sc.serviceType FROM ServiceCatalog sc WHERE sc.hospital.id = :hospitalId AND sc.isActive = true")
    List<ServiceCatalog.ServiceType> findDistinctServiceTypesByHospital(@Param("hospitalId") Long hospitalId);

    @Query("SELECT DISTINCT sc.category FROM ServiceCatalog sc WHERE sc.hospital.id = :hospitalId AND sc.serviceType = :serviceType AND sc.isActive = true")
    List<ServiceCatalog.ServiceCategory> findDistinctCategoriesByHospitalAndServiceType(@Param("hospitalId") Long hospitalId,
                                                                                       @Param("serviceType") ServiceCatalog.ServiceType serviceType);
}
