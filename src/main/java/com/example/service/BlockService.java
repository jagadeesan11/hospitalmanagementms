package com.example.service;

import com.example.entity.Block;
import com.example.entity.Hospital;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.BlockRepository;
import com.example.repository.HospitalRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class BlockService {
    private final BlockRepository blockRepository;
    private final HospitalRepository hospitalRepository;

    public BlockService(BlockRepository blockRepository, HospitalRepository hospitalRepository) {
        this.blockRepository = blockRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Block createBlock(Long hospitalId, Block block) {
        log.info("Creating new block for hospital ID: {}", hospitalId);
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));

        block.setHospital(hospital);
        return blockRepository.save(block);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Block getBlock(Long blockId) {
        log.info("Fetching block with ID: {}", blockId);
        return blockRepository.findById(blockId)
            .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + blockId));
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Block> getBlocksByHospital(Long hospitalId) {
        log.info("Fetching blocks for hospital ID: {}", hospitalId);
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
        return blockRepository.findByHospital(hospital);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Block> getBlocksByFloor(Integer floorNumber) {
        log.info("Fetching blocks for floor number: {}", floorNumber);
        return blockRepository.findByFloorNumber(floorNumber);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Block updateBlock(Long blockId, Block blockDetails) {
        log.info("Updating block with ID: {}", blockId);
        Block block = getBlock(blockId);
        block.setName(blockDetails.getName());
        block.setDescription(blockDetails.getDescription());
        block.setFloorNumber(blockDetails.getFloorNumber());
        return blockRepository.save(block);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void deleteBlock(Long blockId) {
        log.info("Deleting block with ID: {}", blockId);
        Block block = getBlock(blockId);
        blockRepository.delete(block);
    }
}
