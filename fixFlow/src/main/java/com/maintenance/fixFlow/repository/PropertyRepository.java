package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property,Long> {

}
