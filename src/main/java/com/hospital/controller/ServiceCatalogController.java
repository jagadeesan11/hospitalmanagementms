package com.hospital.controller;

import com.hospital.dto.ServiceCatalogDTO;
import com.hospital.entity.ServiceCatalog;
import com.hospital.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-catalog")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Service Catalog", description = "APIs for managing hospital service catalog")
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @PostMapping
    @Operation(summary = "Create a new service in catalog")
    public ResponseEntity<ServiceCatalog> createService(@Valid @RequestBody ServiceCatalogDTO serviceDTO) {
        log.info("Request received to create service: {}", serviceDTO.getServiceName());
        ServiceCatalog service = serviceCatalogService.createService(serviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @PutMapping("/{serviceId}")
    @Operation(summary = "Update an existing service")
    public ResponseEntity<ServiceCatalog> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceCatalogDTO serviceDTO) {
        log.info("Request received to update service ID: {}", serviceId);
        ServiceCatalog service = serviceCatalogService.updateService(serviceId, serviceDTO);
        return ResponseEntity.ok(service);
    }

    @GetMapping("/hospital/{hospitalId}")
    @Operation(summary = "Get all active services for a hospital")
    public ResponseEntity<List<ServiceCatalog>> getServicesByHospital(@PathVariable Long hospitalId) {
        log.info("Request received to get services for hospital ID: {}", hospitalId);
        List<ServiceCatalog> services = serviceCatalogService.getServicesByHospital(hospitalId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/hospital/{hospitalId}/type/{serviceType}")
    @Operation(summary = "Get services by hospital and type")
    public ResponseEntity<List<ServiceCatalog>> getServicesByType(
            @PathVariable Long hospitalId,
            @PathVariable ServiceCatalog.ServiceType serviceType) {
        log.info("Request received to get {} services for hospital ID: {}", serviceType, hospitalId);
        List<ServiceCatalog> services = serviceCatalogService.getServicesByType(hospitalId, serviceType);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/hospital/{hospitalId}/category/{category}")
    @Operation(summary = "Get services by hospital and category")
    public ResponseEntity<List<ServiceCatalog>> getServicesByCategory(
            @PathVariable Long hospitalId,
            @PathVariable ServiceCatalog.ServiceCategory category) {
        log.info("Request received to get {} services for hospital ID: {}", category, hospitalId);
        List<ServiceCatalog> services = serviceCatalogService.getServicesByCategory(hospitalId, category);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get services by department")
    public ResponseEntity<List<ServiceCatalog>> getServicesByDepartment(@PathVariable Long departmentId) {
        log.info("Request received to get services for department ID: {}", departmentId);
        List<ServiceCatalog> services = serviceCatalogService.getServicesByDepartment(departmentId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/search")
    @Operation(summary = "Search services by name")
    public ResponseEntity<List<ServiceCatalog>> searchServices(
            @RequestParam Long hospitalId,
            @RequestParam String serviceName) {
        log.info("Request received to search services with name: {} for hospital ID: {}", serviceName, hospitalId);
        List<ServiceCatalog> services = serviceCatalogService.searchServices(hospitalId, serviceName);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/code/{serviceCode}")
    @Operation(summary = "Get service by code")
    public ResponseEntity<ServiceCatalog> getServiceByCode(@PathVariable String serviceCode) {
        log.info("Request received to get service with code: {}", serviceCode);
        ServiceCatalog service = serviceCatalogService.getServiceByCode(serviceCode);
        return ResponseEntity.ok(service);
    }

    @PutMapping("/{serviceId}/deactivate")
    @Operation(summary = "Deactivate a service")
    public ResponseEntity<Void> deactivateService(@PathVariable Long serviceId) {
        log.info("Request received to deactivate service ID: {}", serviceId);
        serviceCatalogService.deactivateService(serviceId);
        return ResponseEntity.ok().build();
    }
}
