package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.WorkUpdateRequestDto;
import com.maintenance.fixFlow.dto.WorkUpdateResponseDto;
import com.maintenance.fixFlow.service.WorkUpdateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WorkUpdateController {

    private final WorkUpdateService workUpdateService;

    public WorkUpdateController(WorkUpdateService workUpdateService) {
        this.workUpdateService = workUpdateService;
    }

    @PostMapping
    public WorkUpdateResponseDto create(
            @Valid @RequestBody WorkUpdateRequestDto dto) {
        return workUpdateService.createWorkUpdate(dto);
    }

    @GetMapping("/{id}")
    public WorkUpdateResponseDto getById(@PathVariable Long id) {
        return workUpdateService.getWorkUpdateById(id);
    }

    @GetMapping("/getAll")
    public List<WorkUpdateResponseDto> getAll() {
        return workUpdateService.getAllWorkUpdates();
    }

    @PutMapping("/{id}")
    public WorkUpdateResponseDto update(
            @Valid @RequestBody WorkUpdateRequestDto dto,
            @PathVariable Long id) {
        return workUpdateService.updateWorkUpdate(dto, id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        return workUpdateService.deleteWorkUpdate(id);
    }

    @GetMapping("/maintenanceRequestId/{maintenanceRequestId}")
    public List<WorkUpdateResponseDto> getByMaintenanceRequestId(
            @PathVariable Long maintenanceRequestId) {
        return workUpdateService.getWorkUpdatesByMaintenanceRequestId(maintenanceRequestId);
    }

    @GetMapping("/vendorId/{vendorId}")
    public List<WorkUpdateResponseDto> getByVendorId(
            @PathVariable Long vendorId) {
        return workUpdateService.getWorkUpdatesByVendorId(vendorId);
    }
}