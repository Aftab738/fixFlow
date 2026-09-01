package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.CommentRequestDto;
import com.maintenance.fixFlow.dto.CommentResponseDto;
import com.maintenance.fixFlow.entity.Comment;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;

public class CommentMapper {

    public static Comment toEntity(
            CommentRequestDto dto,
            User author,
            MaintenanceRequest maintenanceRequest) {

        Comment comment = new Comment();

        comment.setMessage(dto.getMessage());
        comment.setAuthor(author);
        comment.setMaintenanceRequest(maintenanceRequest);

        return comment;
    }

    public static CommentResponseDto toResponseDto(Comment comment) {

        CommentResponseDto dto = new CommentResponseDto();

        dto.setId(comment.getId());
        dto.setMessage(comment.getMessage());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorName(comment.getAuthor().getName());
        }

        if (comment.getMaintenanceRequest() != null) {
            dto.setMaintenanceRequestId(
                    comment.getMaintenanceRequest().getId()
            );
        }

        return dto;
    }
}