package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.RatingRequestDto;
import com.maintenance.fixFlow.dto.RatingResponseDto;
import com.maintenance.fixFlow.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public RatingResponseDto create(
            @Valid @RequestBody RatingRequestDto dto) {
        return ratingService.createRating(dto);
    }

    @GetMapping("/{id}")
    public RatingResponseDto getById(@PathVariable Long id) {
        return ratingService.getRatingById(id);
    }

    @GetMapping("/getAll")
    public List<RatingResponseDto> getAll() {
        return ratingService.getAllRatings();
    }

    @PutMapping("/{id}")
    public RatingResponseDto update(
            @Valid @RequestBody RatingRequestDto dto,
            @PathVariable Long id) {
        return ratingService.updateRating(dto, id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        return ratingService.deleteRating(id);
    }

    @GetMapping("/maintenanceRequestId/{maintenanceRequestId}")
    public Optional<RatingResponseDto> getByMaintenanceRequestId(
            @PathVariable Long maintenanceRequestId) {
        return ratingService.getRatingByMaintenanceRequestId(
                maintenanceRequestId);
    }

    @GetMapping("/vendorId/{vendorId}")
    public List<RatingResponseDto> getByVendorId(
            @PathVariable Long vendorId) {
        return ratingService.getRatingsByVendorId(vendorId);
    }

    @GetMapping("/tenantId/{tenantId}")
    public List<RatingResponseDto> getByTenantId(
            @PathVariable Long tenantId) {
        return ratingService.getRatingsByTenantId(tenantId);
    }
}