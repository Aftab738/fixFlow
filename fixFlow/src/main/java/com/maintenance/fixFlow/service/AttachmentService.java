package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.AttachmentRequestDto;
import com.maintenance.fixFlow.dto.AttachmentResponseDto;
import com.maintenance.fixFlow.entity.Attachment;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.mapper.AttachmentMapper;
import com.maintenance.fixFlow.repository.AttachmentRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            MaintenanceRequestRepository maintenanceRequestRepository) {

        this.attachmentRepository = attachmentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public AttachmentResponseDto createAttachment(
            AttachmentRequestDto dto) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElse(null);

        Attachment attachment =
                AttachmentMapper.toEntity(dto, maintenanceRequest);

        Attachment savedAttachment =
                attachmentRepository.save(attachment);

        return AttachmentMapper.toResponseDto(savedAttachment);
    }

    public AttachmentResponseDto getAttachmentById(Long id) {

        Optional<Attachment> attachment =
                attachmentRepository.findById(id);

        if (attachment.isPresent()) {
            return AttachmentMapper.toResponseDto(
                    attachment.get());
        }

        return null;
    }

    public List<AttachmentResponseDto> getAllAttachments() {

        List<Attachment> list =
                attachmentRepository.findAll();

        List<AttachmentResponseDto> res =
                new ArrayList<>();

        for (Attachment attachment : list) {
            res.add(AttachmentMapper.toResponseDto(attachment));
        }

        return res;
    }

    public AttachmentResponseDto updateAttachment(
            AttachmentRequestDto dto,
            Long id) {

        Optional<Attachment> existingAttachment =
                attachmentRepository.findById(id);

        if (existingAttachment.isPresent()) {

            Attachment attachment =
                    existingAttachment.get();

            MaintenanceRequest maintenanceRequest =
                    maintenanceRequestRepository
                            .findById(dto.getMaintenanceRequestId())
                            .orElse(null);

            attachment.setName(dto.getName());
            attachment.setContentType(dto.getContentType());
            attachment.setSize(dto.getSize());
            attachment.setDescription(dto.getDescription());
            attachment.setMaintenanceRequest(maintenanceRequest);

            Attachment savedAttachment =
                    attachmentRepository.save(attachment);

            return AttachmentMapper.toResponseDto(
                    savedAttachment);
        }

        return null;
    }

    public String deleteAttachment(Long id) {

        Optional<Attachment> attachment =
                attachmentRepository.findById(id);

        if (attachment.isPresent()) {
            attachmentRepository.delete(attachment.get());
            return "Attachment Deleted";
        }

        return "Attachment not found";
    }

    public List<AttachmentResponseDto> getAttachmentsByMaintenanceRequestId(
            Long maintenanceRequestId) {

        List<Attachment> list =
                attachmentRepository
                        .findByMaintenanceRequestId(maintenanceRequestId);

        List<AttachmentResponseDto> res =
                new ArrayList<>();

        for (Attachment attachment : list) {
            res.add(AttachmentMapper.toResponseDto(attachment));
        }

        return res;
    }
}