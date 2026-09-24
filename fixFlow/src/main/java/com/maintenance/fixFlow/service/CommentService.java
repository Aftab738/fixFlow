package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.CommentRequestDto;
import com.maintenance.fixFlow.dto.CommentResponseDto;
import com.maintenance.fixFlow.dto.NotificationRequestDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.CommentMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.CommentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final NotificationService notificationService;

    public CommentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            MaintenanceRequestRepository maintenanceRequestRepository, AssignmentRepository assignmentRepository, NotificationService notificationService) {

        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.assignmentRepository = assignmentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public CommentResponseDto createComment(CommentRequestDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User author = userRepository.findByEmail(email).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        ));

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        if (author.getRole() == Role.TENANT
                && !author.getId().equals(
                maintenanceRequest.getReportedBy().getId())) {

            throw new AccessDeniedException(
                    "Tenant can comment only on their own maintenance requests"
            );
        }

        if (author.getRole() == Role.VENDOR) {
            List<Assignment> assignments =
                    assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequest.getId());

            boolean assigned = false;

            for (Assignment a : assignments) {
                if (a.getVendor().getId().equals(author.getId())
                        && (a.getStatus() == AssignmentStatus.ACCEPTED
                        || a.getStatus() == AssignmentStatus.PENDING)) {

                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                throw new AccessDeniedException(
                        "Vendor can comment only on maintenance requests assigned to them"
                );
            }
        }

        Comment comment = CommentMapper.toEntity(dto, author, maintenanceRequest);

        Comment savedComment = commentRepository.save(comment);

        if (author.getRole() == Role.VENDOR) {
            User tenant = maintenanceRequest.getReportedBy();

            NotificationRequestDto n = new NotificationRequestDto();
            n.setMessage(
                    "New comment added to your maintenance request."
            );
            n.setType(NotificationType.NEW_COMMENT);
            n.setRead(false);
            n.setUserId(tenant.getId());

            notificationService.createNotification(n);
        }

        if (author.getRole() == Role.TENANT) {

            List<Assignment> assignments =
                    assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequest.getId());

            Assignment currentAssignment = null;

            for (Assignment a : assignments) {

                if (a.getStatus() == AssignmentStatus.ACCEPTED
                        || a.getStatus() == AssignmentStatus.PENDING) {

                    currentAssignment = a;
                    break;
                }
            }

            if (currentAssignment != null) {

                User vendor = currentAssignment.getVendor();

                NotificationRequestDto n =
                        new NotificationRequestDto();

                n.setMessage(
                        "New comment added to your assigned maintenance request."
                );
                n.setType(NotificationType.NEW_COMMENT);
                n.setRead(false);
                n.setUserId(vendor.getId());

                notificationService.createNotification(n);
            }
        }

        return CommentMapper.toResponseDto(savedComment);
    }

    public CommentResponseDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comment not found with id: " + id
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

        if (manager) {
            return CommentMapper.toResponseDto(comment);
        }

        MaintenanceRequest maintenanceRequest = comment.getMaintenanceRequest();

        if (tenant) {
            if (!maintenanceRequest.getReportedBy().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to view this Comment."
                );
            }
        }

        if (vendor) {
            List<Assignment> assignments =
                    assignmentRepository.findByMaintenanceRequestId(
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
                        "You are not allowed to view this Comment."
                );
            }
        }
        return CommentMapper.toResponseDto(comment);
    }

    public List<CommentResponseDto> getAllComments() {

        List<Comment> list = commentRepository.findAll();
        List<CommentResponseDto> res = new ArrayList<>();

        for (Comment comment : list) {
            res.add(CommentMapper.toResponseDto(comment));
        }

        return res;
    }

    public CommentResponseDto updateComment(
            CommentRequestDto dto,
            Long id) {

        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comment not found with id: " + id
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
            if (!comment.getAuthor().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to update this Comment."
                );
            }
        }

        comment.setMessage(dto.getMessage());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toResponseDto(savedComment);
    }

    public String deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comment not found with id: " + id
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
            if (!comment.getAuthor().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to delete this Comment."
                );
            }
        }

        commentRepository.delete(comment);
        return "Comment Deleted";
    }

    public List<CommentResponseDto> getCommentsByMaintenanceRequestId(
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

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + maintenanceRequestId
                                ));

        if (tenant) {
            if (!maintenanceRequest.getReportedBy()
                    .getEmail().equals(email)) {

                throw new AccessDeniedException(
                        "You are not allowed to view these Comments."
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
                        "You are not allowed to view these Comments."
                );
            }
        }

        else if (!manager) {
            throw new AccessDeniedException(
                    "You are not allowed to view these Comments."
            );
        }

        List<Comment> list = commentRepository.findByMaintenanceRequestId(
                        maintenanceRequestId
                );

        List<CommentResponseDto> res = new ArrayList<>();

        for (Comment comment : list) {
            res.add(CommentMapper.toResponseDto(comment));
        }

        return res;
    }

    public List<CommentResponseDto> getCommentsByAuthorId(Long authorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
        }

        if (!manager) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!user.getId().equals(authorId)) {
                throw new AccessDeniedException(
                        "You are not allowed to view these Comments."
                );
            }
        }

        List<Comment> list = commentRepository.findByAuthorId(authorId);

        List<CommentResponseDto> res = new ArrayList<>();
        for (Comment comment : list) {
            res.add(CommentMapper.toResponseDto(comment));
        }

        return res;
    }
}