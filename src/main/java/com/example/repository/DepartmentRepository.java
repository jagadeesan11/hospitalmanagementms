package com.example.repository;

import com.example.entity.Department;
import com.example.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByHospital(Hospital hospital);
    List<Department> findByBlockId(Long blockId);
}
