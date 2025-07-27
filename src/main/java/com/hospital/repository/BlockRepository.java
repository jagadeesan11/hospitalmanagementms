package com.hospital.repository;

import com.hospital.entity.Block;
import com.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByHospital(Hospital hospital);
    List<Block> findByFloorNumber(Integer floorNumber);
}
