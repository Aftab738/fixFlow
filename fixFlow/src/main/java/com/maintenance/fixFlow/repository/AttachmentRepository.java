package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByMaintenanceRequestId(Long maintenanceRequestId);

}