package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.AttachmentRequestDto;
import com.maintenance.fixFlow.dto.AttachmentResponseDto;
import com.maintenance.fixFlow.service.AttachmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    public AttachmentResponseDto create(
            @Valid @RequestBody AttachmentRequestDto dto) {
        return attachmentService.createAttachment(dto);
    }

    @GetMapping("/{id}")
    public AttachmentResponseDto getById(@PathVariable Long id) {
        return attachmentService.getAttachmentById(id);
    }

    @GetMapping("/getAll")
    public List<AttachmentResponseDto> getAll() {
        return attachmentService.getAllAttachments();
    }

    @PutMapping("/{id}")
    public AttachmentResponseDto update(
            @Valid @RequestBody AttachmentRequestDto dto,
            @PathVariable Long id) {
        return attachmentService.updateAttachment(dto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
         attachmentService.deleteAttachment(id);
    }

    @GetMapping("/maintenanceRequestId/{maintenanceRequestId}")
    public List<AttachmentResponseDto> getByMaintenanceRequestId(
            @PathVariable Long maintenanceRequestId) {
        return attachmentService.getAttachmentsByMaintenanceRequestId(maintenanceRequestId);
    }
}