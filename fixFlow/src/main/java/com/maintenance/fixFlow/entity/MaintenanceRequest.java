package com.maintenance.fixFlow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Table(name = "maintenance_requests")
@Entity
public class MaintenanceRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenancePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceCategory category;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "reported_by_id", nullable = false)
    private User reportedBy;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

}
