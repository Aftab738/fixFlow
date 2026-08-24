package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.Attachment;
import com.maintenance.fixFlow.repository.AttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Attachment createAttachment(Attachment attachment){
        return attachmentRepository.save(attachment);
    }

    public Attachment getAttachmentById(Long id){
        return attachmentRepository.findById(id).orElse(null);
    }

    public List<Attachment> getAllAttachments(){
        return attachmentRepository.findAll();
    }

    public Attachment updateAttachment(Attachment attachment,Long id){
        Optional<Attachment> existingAttachment=
                attachmentRepository.findById(id);

        if(existingAttachment.isPresent()){
            Attachment at=existingAttachment.get();

            at.setName(attachment.getName());
            at.setSize(attachment.getSize());
            at.setDescription(attachment.getDescription());
            at.setContentType(attachment.getContentType());
            at.setMaintenanceRequest(attachment.getMaintenanceRequest());
            at.setStoragePath(attachment.getStoragePath());

            return attachmentRepository.save(at);

        }
        return null;
    }

    public String deleteAttachment(Long id){
        Optional<Attachment> existingAttachment=
                attachmentRepository.findById(id);

        if(existingAttachment.isPresent()){
            attachmentRepository.delete(existingAttachment.get());
            return "Attachment Deleted";
        }
        return "Attachment not found";

    }

    public List<Attachment> getAttachmentsByMaintenanceRequestId
            (Long maintenanceRequestId){
        return attachmentRepository.findByMaintenanceRequestId(maintenanceRequestId);
    }

}
