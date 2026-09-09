package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.MaintenanceRequestRequestDto;
import com.maintenance.fixFlow.dto.MaintenanceRequestResponseDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.MaintenanceRequestMapper;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UnitRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;

    public MaintenanceRequestService(
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository,
            UnitRepository unitRepository) {

        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
    }

    public MaintenanceRequestResponseDto createMaintenanceRequest(
            MaintenanceRequestRequestDto dto) {

        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Unit not found with id: " + dto.getUnitId()
                        ));

        User user = userRepository.findById(dto.getReportedById())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + dto.getReportedById()
                        ));

        MaintenanceRequest maintenanceRequest =
                MaintenanceRequestMapper.toEntity(dto, user, unit);

        MaintenanceRequest mr =
                maintenanceRequestRepository.save(maintenanceRequest);

        return MaintenanceRequestMapper.toResponseDto(mr);
    }

    public MaintenanceRequestResponseDto getMaintenanceRequestById(Long id) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: " + id
                                ));

        return MaintenanceRequestMapper.toResponseDto(maintenanceRequest);
    }

    public List<MaintenanceRequestResponseDto> getAllRequests() {

        List<MaintenanceRequest> list =
                maintenanceRequestRepository.findAll();

        List<MaintenanceRequestResponseDto> res =
                new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }

        return res;
    }

    public MaintenanceRequestResponseDto updateMaintenanceRequest(
            MaintenanceRequestRequestDto dto, Long id) {

        MaintenanceRequest mr =
                maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: " + id
                                ));

        User user = userRepository.findById(dto.getReportedById())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + dto.getReportedById()
                        ));

        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Unit not found with id: " + dto.getUnitId()
                        ));

        mr.setUnit(unit);
        mr.setCategory(dto.getCategory());
        mr.setDescription(dto.getDescription());
        mr.setPriority(dto.getPriority());
        mr.setTitle(dto.getTitle());
        mr.setReportedBy(user);
        mr.setStatus(dto.getStatus());

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.save(mr);

        return MaintenanceRequestMapper.toResponseDto(maintenanceRequest);
    }

    public String deleteMaintenanceRequest(Long id) {

        MaintenanceRequest maintenanceReq =
                maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: " + id
                                ));

        maintenanceRequestRepository.delete(maintenanceReq);

        return "Deleted";
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByStatus(
            MaintenanceStatus status) {

        List<MaintenanceRequest> list =
                maintenanceRequestRepository.findByStatus(status);

        List<MaintenanceRequestResponseDto> res =
                new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }

        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByPriority(
            MaintenancePriority priority) {

        List<MaintenanceRequest> list =
                maintenanceRequestRepository.findByPriority(priority);

        List<MaintenanceRequestResponseDto> res =
                new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }

        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByCategory(
            MaintenanceCategory category) {

        List<MaintenanceRequest> list =
                maintenanceRequestRepository.findByCategory(category);

        List<MaintenanceRequestResponseDto> res =
                new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }

        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByUnitId(
            Long unitId) {

        List<MaintenanceRequest> list =
                maintenanceRequestRepository.findByUnitId(unitId);

        List<MaintenanceRequestResponseDto> res =
                new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }

        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByReportedById(
            Long userId) {

        List<MaintenanceRequest> list =
                maintenanceRequestRepository.findByReportedById(userId);

        List<MaintenanceRequestResponseDto> res =
                new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }

        return res;
    }
}