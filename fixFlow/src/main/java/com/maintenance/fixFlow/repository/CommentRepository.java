package com.maintenance.fixFlow.repository;

import com.maintenance.fixFlow.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {


    List<Comment> findByMaintenanceRequestId(Long maintenanceRequestId);

    List<Comment> findByAuthorId(Long authorId);
}
