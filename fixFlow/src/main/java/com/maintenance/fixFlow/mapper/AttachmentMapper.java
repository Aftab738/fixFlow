package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.AttachmentRequestDto;
import com.maintenance.fixFlow.dto.AttachmentResponseDto;
import com.maintenance.fixFlow.entity.Attachment;
import com.maintenance.fixFlow.entity.MaintenanceRequest;

public class AttachmentMapper {

    public static Attachment toEntity(
            AttachmentRequestDto dto,
            MaintenanceRequest maintenanceRequest) {

        Attachment attachment = new Attachment();

        attachment.setName(dto.getName());
        attachment.setContentType(dto.getContentType());
        attachment.setSize(dto.getSize());
        attachment.setDescription(dto.getDescription());
        attachment.setMaintenanceRequest(maintenanceRequest);

        return attachment;
    }

    public static AttachmentResponseDto toResponseDto(
            Attachment attachment) {

        AttachmentResponseDto dto =
                new AttachmentResponseDto();

        dto.setId(attachment.getId());
        dto.setName(attachment.getName());
        dto.setContentType(attachment.getContentType());
        dto.setSize(attachment.getSize());
        dto.setDescription(attachment.getDescription());
        dto.setStoragePath(attachment.getStoragePath());
        dto.setCreatedAt(attachment.getCreatedAt());
        dto.setUpdatedAt(attachment.getUpdatedAt());

        if (attachment.getMaintenanceRequest() != null) {
            dto.setMaintenanceRequestId(
                    attachment.getMaintenanceRequest().getId()
            );
        }

        return dto;
    }
}