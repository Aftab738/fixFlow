package com.maintenance.fixFlow.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
@Table(name = "ratings")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Rating {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Integer score;

    private String comment;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "maintenance_request_id", nullable = false, unique = true)
    private MaintenanceRequest maintenanceRequest;

    @ManyToOne
    @JoinColumn(name = "vendor_id",nullable = false)
    private User vendor;

    @ManyToOne
    @JoinColumn(name = "tenant_id",nullable = false)
    private User tenant;

}
