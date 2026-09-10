package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.CommentRequestDto;
import com.maintenance.fixFlow.dto.CommentResponseDto;
import com.maintenance.fixFlow.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public CommentResponseDto create(
            @Valid @RequestBody CommentRequestDto dto) {
        return commentService.createComment(dto);
    }

    @GetMapping("/{id}")
    public CommentResponseDto getById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @GetMapping("/getAll")
    public List<CommentResponseDto> getAll() {
        return commentService.getAllComments();
    }

    @PutMapping("/{id}")
    public CommentResponseDto update(
            @Valid @RequestBody CommentRequestDto dto,
            @PathVariable Long id) {
        return commentService.updateComment(dto, id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }

    @GetMapping("/maintenanceRequestId/{maintenanceRequestId}")
    public List<CommentResponseDto> getByMaintenanceRequestId(
            @PathVariable Long maintenanceRequestId) {
        return commentService.getCommentsByMaintenanceRequestId(maintenanceRequestId);
    }

    @GetMapping("/authorId/{authorId}")
    public List<CommentResponseDto> getByAuthorId(
            @PathVariable Long authorId) {
        return commentService.getCommentsByAuthorId(authorId);
    }
}