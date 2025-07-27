package com.example.repository;

import com.example.entity.Block;
import com.example.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByHospital(Hospital hospital);
    List<Block> findByFloorNumber(Integer floorNumber);
}
