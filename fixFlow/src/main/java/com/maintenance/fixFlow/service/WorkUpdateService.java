package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.dto.WorkUpdateRequestDto;
import com.maintenance.fixFlow.dto.WorkUpdateResponseDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.BusinessException;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.WorkUpdateMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import com.maintenance.fixFlow.repository.WorkUpdateRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkUpdateService {

    private final WorkUpdateRepository workUpdateRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    private final AssignmentRepository assignmentRepository;
    private final NotificationService notificationService;

    public WorkUpdateService(
            WorkUpdateRepository workUpdateRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository, AssignmentRepository assignmentRepository, NotificationService notificationService) {

        this.workUpdateRepository = workUpdateRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public WorkUpdateResponseDto createWorkUpdate(
            WorkUpdateRequestDto dto) {

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        if (maintenanceRequest.getStatus() == MaintenanceStatus.COMPLETED
                || maintenanceRequest.getStatus() == MaintenanceStatus.CANCELLED
                || maintenanceRequest.getStatus() == MaintenanceStatus.REJECTED) {

            throw new BusinessException(
                    "The maintenance request has already been "
                            + maintenanceRequest.getStatus()
            );
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean vendorRole = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendorRole = true;
            }
        }

        User vendor;

        if (vendorRole) {
            vendor = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            List<Assignment> assignments =
                    assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequest.getId()
                    );

            boolean assigned = false;

            for (Assignment a : assignments) {
                if (a.getVendor().getId().equals(vendor.getId())
                        && (a.getStatus() == AssignmentStatus.PENDING
                        || a.getStatus() == AssignmentStatus.ACCEPTED)) {

                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                throw new AccessDeniedException(
                        "Vendor is not assigned to this maintenance request"
                );
            }

        }
        else if (manager) {
            vendor = userRepository.findById(dto.getVendorId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with id: "
                                            + dto.getVendorId()
                            ));

            if (vendor.getRole() != Role.VENDOR) {
                throw new BusinessException("Selected user is not a vendor");
            }

            List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequest.getId()
                    );

            boolean assigned = false;

            for (Assignment a : assignments) {
                if (a.getVendor().getId().equals(vendor.getId())
                        && (a.getStatus() == AssignmentStatus.PENDING
                        || a.getStatus() == AssignmentStatus.ACCEPTED)) {

                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                throw new BusinessException(
                        "Vendor is not assigned to this maintenance request"
                );
            }

        }
        else {
            throw new AccessDeniedException(
                    "You are not allowed to create a Work Update"
            );
        }

        if (vendor.getRole() != Role.VENDOR) {
            throw new BusinessException("Selected user is not a vendor");
        }

        WorkUpdate workUpdate = WorkUpdateMapper.toEntity(
                        dto,
                        maintenanceRequest,
                        vendor
                );

        WorkUpdate savedWorkUpdate = workUpdateRepository.save(workUpdate);

        User tenant = maintenanceRequest.getReportedBy();

        NotificationRequestDto n = new NotificationRequestDto();
        n.setMessage(
                "New work update added to your maintenance request."
        );
        n.setType(NotificationType.WORK_UPDATE);
        n.setRead(false);
        n.setUserId(tenant.getId());

        notificationService.createNotification(n);

        return WorkUpdateMapper.toResponseDto(savedWorkUpdate);
    }

    public WorkUpdateResponseDto getWorkUpdateById(Long id) {

        WorkUpdate workUpdate = workUpdateRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work update not found with id: " + id
                                ));

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        boolean manager=false;
        boolean vendorRole=false;

        for(var a:authentication.getAuthorities()){
            if(a.getAuthority().equals("ROLE_MANAGER")){
                manager=true;
            }
            else if(a.getAuthority().equals("ROLE_VENDOR")){
                vendorRole=true;
            }
        }

        User vendor=workUpdate.getVendor();
        if(vendorRole){
            if(!vendor.getEmail().equals(email)){
                throw new AccessDeniedException("You are not allowed to view this Work Update.");
            }
        }
        else if(manager){
            return WorkUpdateMapper.toResponseDto(workUpdate);
        }
        else {
            throw new AccessDeniedException("You are not allowed to view this Work Update.");
        }

        return WorkUpdateMapper.toResponseDto(workUpdate);
    }

    public List<WorkUpdateResponseDto> getAllWorkUpdates() {

        List<WorkUpdate> list = workUpdateRepository.findAll();

        List<WorkUpdateResponseDto> res = new ArrayList<>();

        for (WorkUpdate workUpdate : list) {
            res.add(WorkUpdateMapper.toResponseDto(workUpdate));
        }

        return res;
    }

    public WorkUpdateResponseDto updateWorkUpdate(
            WorkUpdateRequestDto dto,
            Long id) {

        WorkUpdate workUpdate = workUpdateRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work update not found with id: " + id
                                ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
        }

        if (!manager) {
            if (!workUpdate.getVendor().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to update this Work Update."
                );
            }
        }

        workUpdate.setMessage(dto.getMessage());

        WorkUpdate savedWorkUpdate = workUpdateRepository.save(workUpdate);

        return WorkUpdateMapper.toResponseDto(savedWorkUpdate);
    }

    public String deleteWorkUpdate(Long id) {

        WorkUpdate workUpdate = workUpdateRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Work update not found with id: " + id
                                ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
        }

        if (!manager) {
            if (!workUpdate.getVendor().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to delete this Work Update."
                );
            }
        }

        workUpdateRepository.delete(workUpdate);

        return "Work update deleted";
    }

    public List<WorkUpdateResponseDto> getWorkUpdatesByMaintenanceRequestId(
            Long maintenanceRequestId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean vendor = false;
        boolean tenant = false;
        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
            else if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
        }

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(maintenanceRequestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + maintenanceRequestId
                                ));

        if (tenant) {
            if (!maintenanceRequest.getReportedBy()
                    .getEmail().equals(email)) {

                throw new AccessDeniedException(
                        "You are not allowed to view these Work Updates."
                );
            }
        }
        else if (vendor) {
            List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequestId
                    );

            boolean allowed = false;

            for (var a : assignments) {
                if (a.getVendor().getEmail().equals(email)) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                throw new AccessDeniedException(
                        "You are not allowed to view these Work Updates."
                );
            }
        }
        else if (!manager) {
            throw new AccessDeniedException(
                    "You are not allowed to view these Work Updates."
            );
        }

        List<WorkUpdate> list = workUpdateRepository
                        .findByMaintenanceRequestId(maintenanceRequestId);

        List<WorkUpdateResponseDto> res = new ArrayList<>();

        for (WorkUpdate workUpdate : list) {
            res.add(WorkUpdateMapper.toResponseDto(workUpdate));
        }

        return res;
    }

    public List<WorkUpdateResponseDto> getWorkUpdatesByVendorId(
            Long vendorId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean vendorRole = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendorRole = true;
            }
        }

        if (vendorRole) {
            User vendor = userRepository.findByEmail(email).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!vendor.getId().equals(vendorId)) {
                throw new AccessDeniedException(
                        "You are not allowed to view these Work Updates."
                );
            }
        }
        else if (!manager) {
            throw new AccessDeniedException(
                    "You are not allowed to view these Work Updates."
            );
        }

        List<WorkUpdate> list = workUpdateRepository.findByVendorId(vendorId);

        List<WorkUpdateResponseDto> res =
                new ArrayList<>();

        for (WorkUpdate workUpdate : list) {
            res.add(WorkUpdateMapper.toResponseDto(workUpdate));
        }

        return res;
    }
}