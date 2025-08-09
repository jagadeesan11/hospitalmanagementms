package com.hospital.repository;

import com.hospital.entity.Department;
import com.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByHospital(Hospital hospital);
    List<Department> findByBlockId(Long blockId);

    // Method to check for duplicate department name within a hospital
    Optional<Department> findByNameAndHospitalId(String name, Long hospitalId);
    boolean existsByNameAndHospitalId(String name, Long hospitalId);

    // Method to check for duplicate department name within a hospital excluding current department (for updates)
    boolean existsByNameAndHospitalIdAndIdNot(String name, Long hospitalId, Long id);
}
