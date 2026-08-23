package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {

    Optional<Rating> findByMaintenanceRequestId(Long maintenanceRequestId);

    List<Rating> findByVendorId(Long vendorId);

    List<Rating> findByTenantId(Long tenantId);
}
