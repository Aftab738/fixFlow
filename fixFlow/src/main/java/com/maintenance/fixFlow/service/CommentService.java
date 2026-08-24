package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.Comment;
import com.maintenance.fixFlow.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long id){
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }

    public Comment updateComment(Comment comment,Long id){
        Optional<Comment> existingComment=commentRepository.findById(id);

        if(existingComment.isPresent()){
            Comment c=existingComment.get();

            c.setAuthor(comment.getAuthor());
            c.setMessage(comment.getMessage());
            c.setMaintenanceRequest(comment.getMaintenanceRequest());

            return commentRepository.save(c);
        }
        return null;
    }

    public String deleteComment(Long id){
        Optional<Comment> existingComment=commentRepository.findById(id);

        if(existingComment.isPresent()){
            commentRepository.deleteById(id);
            return "Comment Deleted";
        }
        return "Comment can not be deleted";
    }

    public List<Comment> getCommentsByMaintenanceRequestId(Long maintenanceReqId){
        return commentRepository.findByMaintenanceRequestId(maintenanceReqId);
    }

    public List<Comment> getCommentsByAuthorId(Long authorId){
        return commentRepository.findByAuthorId(authorId);
    }


}
