package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.MaintenanceRequestRequestDto;
import com.maintenance.fixFlow.dto.MaintenanceRequestResponseDto;
import com.maintenance.fixFlow.entity.MaintenanceCategory;
import com.maintenance.fixFlow.entity.MaintenancePriority;
import com.maintenance.fixFlow.entity.MaintenanceStatus;
import com.maintenance.fixFlow.service.MaintenanceRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenanceRequest")
public class MaintenanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    public MaintenanceRequestController(MaintenanceRequestService maintenanceRequestService) {
        this.maintenanceRequestService = maintenanceRequestService;
    }

    @PostMapping
    public MaintenanceRequestResponseDto create
            (@Valid @RequestBody MaintenanceRequestRequestDto dto){
        return maintenanceRequestService.createMaintenanceRequest(dto);
    }

    @GetMapping("/{id}")
    public MaintenanceRequestResponseDto getById(@PathVariable Long id){
        return maintenanceRequestService.getMaintenanceRequestById(id);
    }

    @GetMapping("/getAll")
    public List<MaintenanceRequestResponseDto> getAll(){
        return maintenanceRequestService.getAllRequests();
    }

    @PutMapping("/{id}")
    public MaintenanceRequestResponseDto update(@Valid @RequestBody MaintenanceRequestRequestDto dto,
                                                   @PathVariable Long id){
        return maintenanceRequestService.updateMaintenanceRequest(dto,id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
         maintenanceRequestService.deleteMaintenanceRequest(id);
    }

    @GetMapping("/status/{status}")
    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByStatus(@PathVariable MaintenanceStatus status){
        return maintenanceRequestService.getMaintenanceRequestsByStatus(status);
    }

    @GetMapping("/priority/{priority}")
    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByPriority(
            @PathVariable MaintenancePriority priority) {

        return maintenanceRequestService.getMaintenanceRequestsByPriority(priority);
    }

    @GetMapping("/category/{category}")
    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByCategory(
            @PathVariable MaintenanceCategory category) {

        return maintenanceRequestService.getMaintenanceRequestsByCategory(category);
    }

    @GetMapping("/unitId/{unitId}")
    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByUnitId(@PathVariable Long unitId){
        return maintenanceRequestService.getMaintenanceRequestsByUnitId(unitId);
    }

    @GetMapping("/reportedById/{reportedById}")
    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByReportedById(
            @PathVariable Long reportedById){
        return maintenanceRequestService.getMaintenanceRequestsByReportedById(reportedById);
    }

}
