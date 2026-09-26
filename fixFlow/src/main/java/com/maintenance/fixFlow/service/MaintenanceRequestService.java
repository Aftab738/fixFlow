package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.*;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.BusinessException;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.MaintenanceRequestMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UnitRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final AssignmentRepository assignmentRepository;

    public MaintenanceRequestService(
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository,
            UnitRepository unitRepository, AssignmentRepository assignmentRepository) {

        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public MaintenanceRequestResponseDto createMaintenanceRequest(
            MaintenanceRequestCreateDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
                break;
            }
        }
        if (!tenant) {
            throw new AccessDeniedException(
                    "Only tenants can create a Maintenance Request"
            );
        }

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with email: " + email
                ));

        if (user.getUnit() == null) {
            throw new BusinessException(
                    "User is not assigned to a unit"
            );
        }

        Unit unit = user.getUnit();

        MaintenanceRequest maintenanceRequest = MaintenanceRequestMapper.toEntity(dto, user, unit);

        MaintenanceRequest mr = maintenanceRequestRepository.save(maintenanceRequest);

        return MaintenanceRequestMapper.toResponseDto(mr);
    }

    public MaintenanceRequestResponseDto adminCreateMaintenanceRequest(
            MaintenanceRequestAdminCreateDto dto) {

        User user = userRepository.findById(dto.getReportedById()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with id: " + dto.getReportedById()
                ));

        Unit unit = unitRepository.findById(dto.getUnitId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Unit not found with id: " + dto.getUnitId()
                ));

        if (user.getRole() != Role.TENANT) {
            throw new BusinessException(
                    "Selected user is not a tenant"
            );
        }

        if (user.getUnit() == null || !user.getUnit().getId().equals(unit.getId())) {
            throw new BusinessException(
                    "User does not belong to the selected unit"
            );
        }

        MaintenanceRequest maintenanceRequest = MaintenanceRequestMapper.toEntity(dto, user, unit);

        MaintenanceRequest mr = maintenanceRequestRepository.save(maintenanceRequest);

        return MaintenanceRequestMapper.toResponseDto(mr);
    }

    public MaintenanceRequestResponseDto getMaintenanceRequestById(Long id) {

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: " + id
                                ));
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        boolean manager=false;
        boolean tenant=false;
        boolean vendor=false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
            else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
        }

        if(manager){
            return MaintenanceRequestMapper.toResponseDto(maintenanceRequest);
        }

        if(tenant){
            if(!maintenanceRequest.getReportedBy().getEmail().equals(email)){
                throw new AccessDeniedException("You are not allowed to view this Maintenance Request");
            }
        }

        if (vendor) {
            List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(id);

            boolean allowed = false;

            for (Assignment assignment : assignments) {
                if (assignment.getVendor().getEmail().equals(email)) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                throw new AccessDeniedException(
                        "You are not allowed to view this Maintenance Request"
                );
            }
        }

        return MaintenanceRequestMapper.toResponseDto(maintenanceRequest);
    }

    public List<MaintenanceRequestResponseDto> getAllRequests() {

        List<MaintenanceRequest> list = maintenanceRequestRepository.findAll();

        List<MaintenanceRequestResponseDto> res =
                new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }

        return res;
    }

    public MaintenanceRequestResponseDto updateMaintenanceRequest(
            MaintenanceRequestUpdateDto dto, Long id) {

        MaintenanceRequest mr = maintenanceRequestRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Maintenance request not found with id: " + id
                ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
                break;
            }
        }

        if (!tenant) {
            throw new AccessDeniedException(
                    "Only tenants can update a Maintenance Request"
            );
        }

        if (!mr.getReportedBy().getEmail().equals(email)) {
            throw new AccessDeniedException(
                    "You are not allowed to update this Maintenance Request"
            );
        }

        mr.setCategory(dto.getCategory());
        mr.setDescription(dto.getDescription());
        mr.setPriority(dto.getPriority());
        mr.setTitle(dto.getTitle());

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.save(mr);

        return MaintenanceRequestMapper.toResponseDto(maintenanceRequest);
    }

    public MaintenanceRequestResponseDto adminUpdateMaintenanceRequest(
            MaintenanceRequestAdminUpdateDto dto, Long id) {

        MaintenanceRequest mr = maintenanceRequestRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Maintenance request not found with id: " + id
                ));

        User user = userRepository.findById(dto.getReportedById()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with id: "
                                + dto.getReportedById()
                ));

        Unit unit = unitRepository.findById(dto.getUnitId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Unit not found with id: "
                                + dto.getUnitId()
                ));

        if (user.getUnit() == null || !user.getUnit().getId().equals(unit.getId())) {
            throw new BusinessException(
                    "User does not belong to the selected unit"
            );
        }

        if (user.getRole() != Role.TENANT) {
            throw new BusinessException(
                    "Selected user is not a tenant"
            );
        }

        mr.setUnit(unit);
        mr.setCategory(dto.getCategory());
        mr.setDescription(dto.getDescription());
        mr.setPriority(dto.getPriority());
        mr.setTitle(dto.getTitle());
        mr.setReportedBy(user);

        MaintenanceStatus oldStatus = mr.getStatus();
        MaintenanceStatus newStatus = dto.getStatus();

        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new BusinessException(
                    "Invalid status transition: "
                            + oldStatus + " to " + newStatus
            );
        }

        mr.setStatus(newStatus);

        if (oldStatus == MaintenanceStatus.IN_PROGRESS && newStatus == MaintenanceStatus.COMPLETED) {
            mr.setCompletedAt(LocalDateTime.now());
        }

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.save(mr);

        return MaintenanceRequestMapper.toResponseDto(maintenanceRequest);
    }

    public void deleteMaintenanceRequest(Long id) {

        MaintenanceRequest mr = maintenanceRequestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Maintenance request not found with id: " + id
                        ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        if (tenant) {
            if (!mr.getReportedBy().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to delete this Maintenance Request"
                );
            }

        }
        else if (!manager) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this Maintenance Request"
            );
        }

        maintenanceRequestRepository.delete(mr);
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByStatus(
            MaintenanceStatus status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        List<MaintenanceRequest> list = maintenanceRequestRepository.findByStatus(status);

        List<MaintenanceRequestResponseDto> res = new ArrayList<>();

        for (MaintenanceRequest request : list) {
            if (tenant) {

                if (request.getReportedBy().getEmail().equals(email)) {
                    res.add(MaintenanceRequestMapper.toResponseDto(request));
                }

            }
            else if (vendor) {
                List<Assignment> assignments =
                        assignmentRepository.findByMaintenanceRequestId(
                                request.getId()
                        );

                for (Assignment assignment : assignments) {

                    if (assignment.getVendor().getEmail().equals(email)) {
                        res.add(MaintenanceRequestMapper.toResponseDto(request));
                        break;
                    }
                }
            }
            else {
                // Manager
                res.add(MaintenanceRequestMapper.toResponseDto(request));
            }
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByPriority(
            MaintenancePriority priority) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        List<MaintenanceRequest> list = maintenanceRequestRepository.findByPriority(priority);

        List<MaintenanceRequestResponseDto> res = new ArrayList<>();

        for (MaintenanceRequest request : list) {
            if (tenant) {
                if (request.getReportedBy().getEmail().equals(email)) {
                    res.add(MaintenanceRequestMapper.toResponseDto(request));
                }

            }
            else if (vendor) {
                List<Assignment> assignments =
                        assignmentRepository.findByMaintenanceRequestId(
                                request.getId()
                        );

                for (Assignment assignment : assignments) {

                    if (assignment.getVendor().getEmail().equals(email)) {
                        res.add(MaintenanceRequestMapper.toResponseDto(request));
                        break;
                    }
                }

            }
            else {
                // Manager
                res.add(MaintenanceRequestMapper.toResponseDto(request));
            }
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByCategory(
            MaintenanceCategory category) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        List<MaintenanceRequest> list = maintenanceRequestRepository.findByCategory(category);

        List<MaintenanceRequestResponseDto> res = new ArrayList<>();

        for (MaintenanceRequest request : list) {
            if (tenant) {
                if (request.getReportedBy().getEmail().equals(email)) {
                    res.add(MaintenanceRequestMapper.toResponseDto(request));
                }

            }
            else if (vendor) {
                List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(
                                request.getId()
                        );

                for (Assignment assignment : assignments) {
                    if (assignment.getVendor().getEmail().equals(email)) {
                        res.add(MaintenanceRequestMapper.toResponseDto(request));
                        break;
                    }
                }

            }
            else {
                // Manager
                res.add(MaintenanceRequestMapper.toResponseDto(request));
            }
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByUnitId(
            Long unitId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        List<MaintenanceRequest> list = maintenanceRequestRepository.findByUnitId(unitId);

        List<MaintenanceRequestResponseDto> res = new ArrayList<>();

        for (MaintenanceRequest request : list) {
            if (tenant) {
                if (request.getReportedBy().getEmail().equals(email)) {
                    res.add(MaintenanceRequestMapper.toResponseDto(request));
                }

            }
            else if (vendor) {

                List<Assignment> assignments =
                        assignmentRepository.findByMaintenanceRequestId(
                                request.getId()
                        );

                for (Assignment assignment : assignments) {
                    if (assignment.getVendor().getEmail().equals(email)) {
                        res.add(MaintenanceRequestMapper.toResponseDto(request));
                        break;
                    }
                }
            }
            else {
                // Manager
                res.add(MaintenanceRequestMapper.toResponseDto(request));
            }
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByReportedById(
            Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        if (tenant) {
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!user.getId().equals(userId)) {
                throw new AccessDeniedException(
                        "You are not allowed to view these Maintenance Requests"
                );
            }
        }

        if (!manager && !tenant) {
            throw new AccessDeniedException(
                    "You are not allowed to view Maintenance Requests by user"
            );
        }

        List<MaintenanceRequest> list = maintenanceRequestRepository.findByReportedById(userId);

        List<MaintenanceRequestResponseDto> res = new ArrayList<>();

        for (MaintenanceRequest m : list) {
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }
        return res;
    }

    private boolean isValidStatusTransition(MaintenanceStatus oldStatus, MaintenanceStatus newStatus) {

        if (oldStatus == newStatus) return true;

        if (oldStatus == MaintenanceStatus.SUBMITTED
                && newStatus == MaintenanceStatus.UNDER_REVIEW) return true;

        if (oldStatus == MaintenanceStatus.SUBMITTED
                && newStatus == MaintenanceStatus.ASSIGNED) return true;

        if (oldStatus == MaintenanceStatus.UNDER_REVIEW
                && newStatus == MaintenanceStatus.ASSIGNED) return true;

        if (oldStatus == MaintenanceStatus.ASSIGNED
                && newStatus == MaintenanceStatus.IN_PROGRESS) return true;

        if (oldStatus == MaintenanceStatus.IN_PROGRESS
                && newStatus == MaintenanceStatus.COMPLETED) return true;

        if (oldStatus == MaintenanceStatus.SUBMITTED
                && newStatus == MaintenanceStatus.CANCELLED) return true;

        if (oldStatus == MaintenanceStatus.UNDER_REVIEW
                && newStatus == MaintenanceStatus.CANCELLED) return true;

        if (oldStatus == MaintenanceStatus.ASSIGNED
                && newStatus == MaintenanceStatus.CANCELLED) return true;

        if (oldStatus == MaintenanceStatus.IN_PROGRESS
                && newStatus == MaintenanceStatus.CANCELLED) return true;

        if (oldStatus == MaintenanceStatus.UNDER_REVIEW
                && newStatus == MaintenanceStatus.REJECTED) return true;

        if (oldStatus == MaintenanceStatus.ASSIGNED
                && newStatus == MaintenanceStatus.REJECTED) return true;

        return false;
    }
}