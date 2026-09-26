package com.maintenance.fixFlow.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@Getter
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "author_id",nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "maintenance_request_id",nullable = false)
    private MaintenanceRequest maintenanceRequest;

}
