package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentRequestDto {

    private String name;

    private String contentType;

    private Long size;

    private String description;

    private String storagePath;

    private Long maintenanceRequestId;
}