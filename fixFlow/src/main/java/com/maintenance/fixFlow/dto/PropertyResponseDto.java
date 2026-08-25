package com.maintenance.fixFlow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class PropertyResponseDto {

    private Long id;

    private String name;

    private String address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
