package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.WorkUpdateRequestDto;
import com.maintenance.fixFlow.dto.WorkUpdateResponseDto;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.entity.WorkUpdate;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.WorkUpdateMapper;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import com.maintenance.fixFlow.repository.WorkUpdateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkUpdateService {

    private final WorkUpdateRepository workUpdateRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    public WorkUpdateService(
            WorkUpdateRepository workUpdateRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository) {

        this.workUpdateRepository = workUpdateRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
    }

    public WorkUpdateResponseDto createWorkUpdate(
            WorkUpdateRequestDto dto) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor =
                userRepository
                        .findById(dto.getVendorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        WorkUpdate workUpdate =
                WorkUpdateMapper.toEntity(
                        dto,
                        maintenanceRequest,
                        vendor);

        WorkUpdate savedWorkUpdate =
                workUpdateRepository.save(workUpdate);

        return WorkUpdateMapper.toResponseDto(savedWorkUpdate);
    }

    public WorkUpdateResponseDto getWorkUpdateById(Long id) {

        WorkUpdate workUpdate =
                workUpdateRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work update not found with id: " + id
                                ));

        return WorkUpdateMapper.toResponseDto(workUpdate);
    }

    public List<WorkUpdateResponseDto> getAllWorkUpdates() {

        List<WorkUpdate> list =
                workUpdateRepository.findAll();

        List<WorkUpdateResponseDto> res =
                new ArrayList<>();

        for (WorkUpdate workUpdate : list) {
            res.add(WorkUpdateMapper.toResponseDto(workUpdate));
        }

        return res;
    }

    public WorkUpdateResponseDto updateWorkUpdate(
            WorkUpdateRequestDto dto,
            Long id) {

        WorkUpdate workUpdate =
                workUpdateRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work update not found with id: " + id
                                ));

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor =
                userRepository
                        .findById(dto.getVendorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        workUpdate.setMessage(dto.getMessage());
        workUpdate.setMaintenanceRequest(maintenanceRequest);
        workUpdate.setVendor(vendor);

        WorkUpdate savedWorkUpdate =
                workUpdateRepository.save(workUpdate);

        return WorkUpdateMapper.toResponseDto(savedWorkUpdate);
    }

    public String deleteWorkUpdate(Long id) {

        WorkUpdate workUpdate =
                workUpdateRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work update not found with id: " + id
                                ));

        workUpdateRepository.delete(workUpdate);

        return "Work update deleted";
    }

    public List<WorkUpdateResponseDto> getWorkUpdatesByMaintenanceRequestId(
            Long maintenanceRequestId) {

        List<WorkUpdate> list =
                workUpdateRepository
                        .findByMaintenanceRequestId(maintenanceRequestId);

        List<WorkUpdateResponseDto> res =
                new ArrayList<>();

        for (WorkUpdate workUpdate : list) {
            res.add(WorkUpdateMapper.toResponseDto(workUpdate));
        }

        return res;
    }

    public List<WorkUpdateResponseDto> getWorkUpdatesByVendorId(
            Long vendorId) {

        List<WorkUpdate> list =
                workUpdateRepository.findByVendorId(vendorId);

        List<WorkUpdateResponseDto> res =
                new ArrayList<>();

        for (WorkUpdate workUpdate : list) {
            res.add(WorkUpdateMapper.toResponseDto(workUpdate));
        }

        return res;
    }
}