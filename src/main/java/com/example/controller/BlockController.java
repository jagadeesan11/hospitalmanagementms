package com.example.controller;

import com.example.entity.Block;
import com.example.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Block Management", description = "APIs for managing hospital blocks")
@Log4j2
public class BlockController {
    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping("/hospitals/{hospitalId}/blocks")
    @Operation(summary = "Create a new block in a hospital")
    public ResponseEntity<Block> createBlock(
            @PathVariable Long hospitalId,
            @RequestBody Block block) {
        log.info("Request received to create block in hospital ID: {}", hospitalId);
        return ResponseEntity.ok(blockService.createBlock(hospitalId, block));
    }

    @GetMapping("/blocks/{id}")
    @Operation(summary = "Get block by ID")
    public ResponseEntity<Block> getBlock(@PathVariable Long id) {
        log.info("Request received to fetch block with ID: {}", id);
        return ResponseEntity.ok(blockService.getBlock(id));
    }

    @GetMapping("/hospitals/{hospitalId}/blocks")
    @Operation(summary = "Get all blocks in a hospital")
    public ResponseEntity<List<Block>> getBlocksByHospital(@PathVariable Long hospitalId) {
        log.info("Request received to fetch blocks for hospital ID: {}", hospitalId);
        return ResponseEntity.ok(blockService.getBlocksByHospital(hospitalId));
    }

    @GetMapping("/blocks")
    @Operation(summary = "Get blocks by floor number")
    public ResponseEntity<List<Block>> getBlocksByFloor(@RequestParam Integer floorNumber) {
        log.info("Request received to fetch blocks for floor number: {}", floorNumber);
        return ResponseEntity.ok(blockService.getBlocksByFloor(floorNumber));
    }

    @PutMapping("/blocks/{id}")
    @Operation(summary = "Update block information")
    public ResponseEntity<Block> updateBlock(
            @PathVariable Long id,
            @RequestBody Block block) {
        log.info("Request received to update block with ID: {}", id);
        return ResponseEntity.ok(blockService.updateBlock(id, block));
    }

    @DeleteMapping("/blocks/{id}")
    @Operation(summary = "Delete block")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long id) {
        log.info("Request received to delete block with ID: {}", id);
        blockService.deleteBlock(id);
        return ResponseEntity.noContent().build();
    }
}
