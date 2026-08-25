package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentResponseDto {

    private Long id;

    private String name;

    private String contentType;

    private Long size;

    private String description;

    private String storagePath;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long maintenanceRequestId;
}