package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByMaintenanceRequestId(Long maintenanceRequestId);

}