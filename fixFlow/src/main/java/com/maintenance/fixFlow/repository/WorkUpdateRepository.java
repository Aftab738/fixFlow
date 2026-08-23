package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.WorkUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkUpdateRepository extends JpaRepository<WorkUpdate,Long> {

    List<WorkUpdate> findByMaintenanceRequestId(Long maintenanceRequestId);

    List<WorkUpdate> findByVendorId(Long vendorId);
}
