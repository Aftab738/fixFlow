package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Assignment;
import com.maintenance.fixFlow.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByMaintenanceRequestId(Long maintenanceRequestId);

    List<Assignment> findByVendorId(Long vendorId);

    List<Assignment> findByStatus(AssignmentStatus status);

    List<Assignment> findByVendorIdAndStatus(
            Long vendorId,
            AssignmentStatus status
    );
}