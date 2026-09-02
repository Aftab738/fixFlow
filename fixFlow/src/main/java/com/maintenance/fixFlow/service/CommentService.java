package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.CommentRequestDto;
import com.maintenance.fixFlow.dto.CommentResponseDto;
import com.maintenance.fixFlow.entity.Comment;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.mapper.CommentMapper;
import com.maintenance.fixFlow.repository.CommentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public CommentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            MaintenanceRequestRepository maintenanceRequestRepository) {

        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public CommentResponseDto createComment(CommentRequestDto dto) {

        User author = userRepository.findById(dto.getAuthorId())
                .orElse(null);

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElse(null);

        Comment comment =
                CommentMapper.toEntity(dto, author, maintenanceRequest);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toResponseDto(savedComment);
    }

    public CommentResponseDto getCommentById(Long id) {

        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isPresent()) {
            return CommentMapper.toResponseDto(comment.get());
        }

        return null;
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

        Optional<Comment> existingComment =
                commentRepository.findById(id);

        if (existingComment.isPresent()) {

            Comment comment = existingComment.get();

            User author = userRepository.findById(dto.getAuthorId())
                    .orElse(null);

            MaintenanceRequest maintenanceRequest =
                    maintenanceRequestRepository
                            .findById(dto.getMaintenanceRequestId())
                            .orElse(null);

            comment.setMessage(dto.getMessage());
            comment.setAuthor(author);
            comment.setMaintenanceRequest(maintenanceRequest);

            Comment savedComment = commentRepository.save(comment);

            return CommentMapper.toResponseDto(savedComment);
        }

        return null;
    }

    public String deleteComment(Long id) {

        Optional<Comment> comment =
                commentRepository.findById(id);

        if (comment.isPresent()) {
            commentRepository.delete(comment.get());
            return "Comment Deleted";
        }

        return "Comment not found";
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