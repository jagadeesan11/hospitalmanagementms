package com.hospital.repository;

import com.hospital.entity.Doctor;
import com.hospital.entity.Hospital;
import com.hospital.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByEmail(String email);

    List<Doctor> findByHospital(Hospital hospital);

    List<Doctor> findByDepartment(Department department);
}
