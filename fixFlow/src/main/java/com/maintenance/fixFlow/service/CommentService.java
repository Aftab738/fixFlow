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

        User author = userRepository.findById(dto.getAuthorId()).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + dto.getAuthorId()
                        ));

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        Comment comment = CommentMapper.toEntity(dto, author, maintenanceRequest);

        Comment savedComment = commentRepository.save(comment);

        if (author.getRole() == Role.VENDOR) { //Notifies the user/tenant when a vendor makes any comment

            User tenant = maintenanceRequest.getReportedBy();

            NotificationRequestDto n = new NotificationRequestDto();
            n.setMessage("New comment added to your maintenance request.");
            n.setType(NotificationType.NEW_COMMENT);
            n.setRead(false);
            n.setUserId(tenant.getId());

            notificationService.createNotification(n);
        }

        if(author.getRole()==Role.TENANT){ //Tenant to Vendor
            List<Assignment> assignments =
                    assignmentRepository.findByMaintenanceRequestId(
                            maintenanceRequest.getId());

            Assignment currentAssignment = null;

            for (Assignment a : assignments) {

                if (a.getStatus() == AssignmentStatus.ACCEPTED ||
                        a.getStatus() == AssignmentStatus.PENDING) {

                    currentAssignment = a;
                    break;
                }
            }

            if (currentAssignment != null) {
                User vendor = currentAssignment.getVendor();

                NotificationRequestDto n=new NotificationRequestDto();

                n.setMessage("New comment added to your assigned maintenance request.");
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

        User author = userRepository.findById(dto.getAuthorId()).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + dto.getAuthorId()
                        ));

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        comment.setMessage(dto.getMessage());
        comment.setAuthor(author);
        comment.setMaintenanceRequest(maintenanceRequest);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toResponseDto(savedComment);
    }

    public String deleteComment(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comment not found with id: " + id
                        ));

        commentRepository.delete(comment);

        return "Comment Deleted";
    }

    public List<CommentResponseDto> getCommentsByMaintenanceRequestId(
            Long maintenanceRequestId) {

        List<Comment> list =
                commentRepository
                        .findByMaintenanceRequestId(maintenanceRequestId);

        List<CommentResponseDto> res = new ArrayList<>();

        for (Comment comment : list) {
            res.add(CommentMapper.toResponseDto(comment));
        }

        return res;
    }

    public List<CommentResponseDto> getCommentsByAuthorId(Long authorId) {

        List<Comment> list =
                commentRepository.findByAuthorId(authorId);

        List<CommentResponseDto> res = new ArrayList<>();

        for (Comment comment : list) {
            res.add(CommentMapper.toResponseDto(comment));
        }

        return res;
    }
}