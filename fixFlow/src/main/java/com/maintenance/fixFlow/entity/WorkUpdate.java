package com.maintenance.fixFlow.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "work_updates")
@Entity
public class WorkUpdate {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "maintenance_request_id",nullable = false)
    private MaintenanceRequest maintenanceRequest;

    @ManyToOne
    @JoinColumn(name = "vendor_id",nullable = false)
    private User vendor;

}
