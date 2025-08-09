package com.hospital.service;

import com.hospital.dto.ServiceCatalogDTO;
import com.hospital.entity.Hospital;
import com.hospital.entity.Department;
import com.hospital.entity.ServiceCatalog;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.ServiceCatalogRepository;
import com.hospital.repository.HospitalRepository;
import com.hospital.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public ServiceCatalog createService(ServiceCatalogDTO serviceDTO) {
        log.info("Creating service catalog entry: {}", serviceDTO.getServiceName());

        Hospital hospital = hospitalRepository.findById(serviceDTO.getHospitalId())
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + serviceDTO.getHospitalId()));

        Department department = null;
        if (serviceDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(serviceDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + serviceDTO.getDepartmentId()));
        }

        ServiceCatalog service = new ServiceCatalog();
        service.setServiceCode(serviceDTO.getServiceCode());
        service.setServiceName(serviceDTO.getServiceName());
        service.setDescription(serviceDTO.getDescription());
        service.setServiceType(serviceDTO.getServiceType());
        service.setCategory(serviceDTO.getCategory());
        service.setUnitPrice(serviceDTO.getUnitPrice());
        service.setTaxPercentage(serviceDTO.getTaxPercentage());
        service.setIsActive(serviceDTO.getIsActive());
        service.setHospital(hospital);
        service.setDepartment(department);

        return serviceCatalogRepository.save(service);
    }

    @Transactional
    public ServiceCatalog updateService(Long serviceId, ServiceCatalogDTO serviceDTO) {
        log.info("Updating service catalog entry with ID: {}", serviceId);

        ServiceCatalog service = serviceCatalogRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        if (serviceDTO.getServiceCode() != null) {
            service.setServiceCode(serviceDTO.getServiceCode());
        }
        if (serviceDTO.getServiceName() != null) {
            service.setServiceName(serviceDTO.getServiceName());
        }
        if (serviceDTO.getDescription() != null) {
            service.setDescription(serviceDTO.getDescription());
        }
        if (serviceDTO.getServiceType() != null) {
            service.setServiceType(serviceDTO.getServiceType());
        }
        if (serviceDTO.getCategory() != null) {
            service.setCategory(serviceDTO.getCategory());
        }
        if (serviceDTO.getUnitPrice() != null) {
            service.setUnitPrice(serviceDTO.getUnitPrice());
        }
        if (serviceDTO.getTaxPercentage() != null) {
            service.setTaxPercentage(serviceDTO.getTaxPercentage());
        }
        if (serviceDTO.getIsActive() != null) {
            service.setIsActive(serviceDTO.getIsActive());
        }

        return serviceCatalogRepository.save(service);
    }

    public List<ServiceCatalog> getServicesByHospital(Long hospitalId) {
        return serviceCatalogRepository.findByHospitalIdAndIsActive(hospitalId, true);
    }

    public List<ServiceCatalog> getServicesByType(Long hospitalId, ServiceCatalog.ServiceType serviceType) {
        return serviceCatalogRepository.findActiveServicesByHospitalAndType(hospitalId, serviceType);
    }

    public List<ServiceCatalog> getServicesByCategory(Long hospitalId, ServiceCatalog.ServiceCategory category) {
        return serviceCatalogRepository.findByHospitalIdAndCategory(hospitalId, category);
    }

    public List<ServiceCatalog> getServicesByDepartment(Long departmentId) {
        return serviceCatalogRepository.findByDepartmentIdAndIsActive(departmentId, true);
    }

    public List<ServiceCatalog> searchServices(Long hospitalId, String serviceName) {
        return serviceCatalogRepository.findByHospitalAndServiceNameContaining(hospitalId, serviceName);
    }

    public ServiceCatalog getServiceByCode(String serviceCode) {
        return serviceCatalogRepository.findByServiceCode(serviceCode)
            .orElseThrow(() -> new ResourceNotFoundException("Service not found with code: " + serviceCode));
    }

    @Transactional
    public void deactivateService(Long serviceId) {
        log.info("Deactivating service with ID: {}", serviceId);

        ServiceCatalog service = serviceCatalogRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        service.setIsActive(false);
        serviceCatalogRepository.save(service);
    }
}
