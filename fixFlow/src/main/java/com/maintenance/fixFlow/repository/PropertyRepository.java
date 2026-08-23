package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property,Long> {
}
