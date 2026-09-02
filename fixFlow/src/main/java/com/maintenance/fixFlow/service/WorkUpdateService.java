package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.WorkUpdateRequestDto;
import com.maintenance.fixFlow.dto.WorkUpdateResponseDto;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.entity.WorkUpdate;
import com.maintenance.fixFlow.mapper.WorkUpdateMapper;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import com.maintenance.fixFlow.repository.WorkUpdateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                        .orElse(null);

        User vendor =
                userRepository
                        .findById(dto.getVendorId())
                        .orElse(null);

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

        Optional<WorkUpdate> workUpdate =
                workUpdateRepository.findById(id);

        if (workUpdate.isPresent()) {
            return WorkUpdateMapper.toResponseDto(
                    workUpdate.get());
        }

        return null;
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

        Optional<WorkUpdate> existingWorkUpdate =
                workUpdateRepository.findById(id);

        if (existingWorkUpdate.isPresent()) {

            WorkUpdate workUpdate =
                    existingWorkUpdate.get();

            MaintenanceRequest maintenanceRequest =
                    maintenanceRequestRepository
                            .findById(dto.getMaintenanceRequestId())
                            .orElse(null);

            User vendor =
                    userRepository
                            .findById(dto.getVendorId())
                            .orElse(null);

            workUpdate.setMessage(dto.getMessage());
            workUpdate.setMaintenanceRequest(maintenanceRequest);
            workUpdate.setVendor(vendor);

            WorkUpdate savedWorkUpdate =
                    workUpdateRepository.save(workUpdate);

            return WorkUpdateMapper.toResponseDto(
                    savedWorkUpdate);
        }

        return null;
    }

    public String deleteWorkUpdate(Long id) {

        Optional<WorkUpdate> workUpdate =
                workUpdateRepository.findById(id);

        if (workUpdate.isPresent()) {
            workUpdateRepository.delete(workUpdate.get());
            return "Work update deleted";
        }

        return "Work update not found";
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