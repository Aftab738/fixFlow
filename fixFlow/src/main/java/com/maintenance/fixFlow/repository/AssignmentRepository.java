package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Assignment;
import com.maintenance.fixFlow.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByMaintenanceRequestId(Long maintenanceRequestId);

    List<Assignment> findByVendorId(Long vendorId);

    List<Assignment> findByStatus(AssignmentStatus status);

    List<Assignment> findByVendorIdAndStatus(
            Long vendorId,
            AssignmentStatus status
    );
}