package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.WorkUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkUpdateRepository extends JpaRepository<WorkUpdate,Long> {

    List<WorkUpdate> findByMaintenanceRequestId(Long maintenanceRequestId);

    List<WorkUpdate> findByVendorId(Long vendorId);
}
