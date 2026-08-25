package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;

    private String message;

    private LocalDateTime createdAt;

    private Long authorId;

    private String authorName;

    private Long maintenanceRequestId;
}