package com.example.repository;

import com.example.entity.Doctor;
import com.example.entity.Hospital;
import com.example.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByEmail(String email);

    List<Doctor> findByHospital(Hospital hospital);

    List<Doctor> findByDepartment(Department department);
}
