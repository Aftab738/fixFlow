package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.AttachmentRequestDto;
import com.maintenance.fixFlow.dto.AttachmentResponseDto;
import com.maintenance.fixFlow.entity.Assignment;
import com.maintenance.fixFlow.entity.Attachment;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.AttachmentMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.AttachmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final AssignmentRepository assignmentRepository;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            MaintenanceRequestRepository maintenanceRequestRepository, AssignmentRepository assignmentRepository) {

        this.attachmentRepository = attachmentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public AttachmentResponseDto createAttachment(AttachmentRequestDto dto) {

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(()-> new ResourceNotFoundException(
                                "Maintenance request not found with id:"+dto.getMaintenanceRequestId())
                        );

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        boolean vendor=false;
        boolean tenant=false;
        boolean manager=false;

        for(var a:authentication.getAuthorities()){
            if(a.getAuthority().equals("ROLE_VENDOR")){
                vendor=true;
            }
            else if(a.getAuthority().equals("ROLE_TENANT")){
                tenant=true;
            }
            else if(a.getAuthority().equals("ROLE_MANAGER")){
                manager=true;
            }
        }

        if(tenant){
            if(!maintenanceRequest.getReportedBy().getEmail().equals(email)){
                throw new AccessDeniedException("You can not add an attachment to this Maintenance request.");
            }
        }

        else if(vendor) {
            List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(maintenanceRequest.getId());

            boolean allowed = false;
            for (Assignment as : assignments) {
                if (as.getVendor().getEmail().equals(email)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new AccessDeniedException("You can not add an attachment to this Maintenance request.");
            }
        }
        else if(!manager){
            throw new AccessDeniedException("You can not add an attachment to this Maintenance request.");
        }

        Attachment attachment = AttachmentMapper.toEntity(dto, maintenanceRequest);

        Attachment savedAttachment = attachmentRepository.save(attachment);

        return AttachmentMapper.toResponseDto(savedAttachment);
    }

    public AttachmentResponseDto getAttachmentById(Long id) {

        Attachment attachment = attachmentRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("Attachment not found with id:"+id));

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        boolean vendor=false;
        boolean tenant=false;
        boolean manager=false;

        for(var a:authentication.getAuthorities()){
            if(a.getAuthority().equals("ROLE_VENDOR")){
                vendor=true;
            }
            else if(a.getAuthority().equals("ROLE_TENANT")){
                tenant=true;
            }
            else if(a.getAuthority().equals("ROLE_MANAGER")){
                manager=true;
            }
        }
        if(manager){
            return AttachmentMapper.toResponseDto(attachment);
        }

        MaintenanceRequest maintenanceRequest=attachment.getMaintenanceRequest();

        if(tenant){
            if(!maintenanceRequest.getReportedBy().getEmail().equals(email)){
                throw new AccessDeniedException("You are not allowed to view this Attachment.");
            }
        }

        if(vendor){
            boolean allowed=false;
            List<Assignment> assignments=assignmentRepository.findByMaintenanceRequestId(maintenanceRequest.getId());

            for(var a:assignments){
                if(a.getVendor().getEmail().equals(email)){
                    allowed=true;
                    break;
                }
            }

            if(!allowed){
                throw new AccessDeniedException("You are not allowed to view this Attachment.");
            }
        }

        return AttachmentMapper.toResponseDto(attachment);
    }

    public List<AttachmentResponseDto> getAllAttachments() {

        List<Attachment> list =
                attachmentRepository.findAll();

        List<AttachmentResponseDto> res =
                new ArrayList<>();

        for (Attachment attachment : list) {
            res.add(AttachmentMapper.toResponseDto(attachment));
        }

        return res;
    }

    public AttachmentResponseDto updateAttachment(
            AttachmentRequestDto dto, Long id) {

        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attachment not found with id: " + id
                        ));

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

        MaintenanceRequest maintenanceRequest = attachment.getMaintenanceRequest();

        if (tenant) {
            if (!maintenanceRequest.getReportedBy().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to update this Attachment."
                );
            }

        }
        else if (vendor) {
            List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequest.getId()
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
                        "You are not allowed to update this Attachment."
                );
            }

        }
        else if (!manager) {

            throw new AccessDeniedException(
                    "You are not allowed to update this Attachment."
            );
        }

        attachment.setName(dto.getName());
        attachment.setContentType(dto.getContentType());
        attachment.setSize(dto.getSize());
        attachment.setDescription(dto.getDescription());

        Attachment savedAttachment = attachmentRepository.save(attachment);

        return AttachmentMapper.toResponseDto(savedAttachment);
    }

    public void deleteAttachment(Long id) {

        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attachment not found with id: " + id
                                ));

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

        MaintenanceRequest maintenanceRequest = attachment.getMaintenanceRequest();

        if (tenant) {
            if (!maintenanceRequest.getReportedBy().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to delete this Attachment."
                );
            }

        }
        else if (vendor) {
            List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequest.getId()
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
                        "You are not allowed to delete this Attachment."
                );
            }

        }
        else if (!manager) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this Attachment."
            );
        }

        attachmentRepository.delete(attachment);
    }

    public List<AttachmentResponseDto> getAttachmentsByMaintenanceRequestId(
            Long maintenanceRequestId) {

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        boolean vendor=false;
        boolean tenant=false;

        for(var a:authentication.getAuthorities()){
            if(a.getAuthority().equals("ROLE_VENDOR")){
                vendor=true;
            }
            else if(a.getAuthority().equals("ROLE_TENANT")){
                tenant=true;
            }
        }

        MaintenanceRequest maintenanceRequest=maintenanceRequestRepository.
                findById(maintenanceRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Maintenance request not found with id:"+maintenanceRequestId));

        if(tenant){
            if(!maintenanceRequest.getReportedBy().getEmail().equals(email)){
                throw new AccessDeniedException("You are not allowed to view this Attachment.");
            }
        }

        if(vendor){
            boolean allowed=false;
            List<Assignment> assignments=assignmentRepository.findByMaintenanceRequestId(maintenanceRequestId);

            for(var a:assignments){
                if(a.getVendor().getEmail().equals(email)){
                    allowed=true;
                    break;
                }
            }

            if(!allowed){
                throw new AccessDeniedException("You are not allowed to view this Attachment.");
            }
        }

        List<Attachment> list = attachmentRepository.findByMaintenanceRequestId(maintenanceRequestId);

        List<AttachmentResponseDto> res = new ArrayList<>();

        for (Attachment attachment : list) {
            res.add(AttachmentMapper.toResponseDto(attachment));
        }

        return res;
    }
}