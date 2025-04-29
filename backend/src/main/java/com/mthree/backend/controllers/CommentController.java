package com.mthree.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mthree.backend.models.Comment;
import com.mthree.backend.models.requests.StringRequest;
import com.mthree.backend.services.interfaces.CommentService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<ResponseType<Page<Comment>>> getComments(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Video ID must not be null or less than 1"
            );
        }

        Page<Comment> comments = commentService.getComments(videoId, page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                    "Comments fetched successfully",
                    comments,
                    200
                )
        );
    }

    @PostMapping("/{videoId}")
    public ResponseEntity<ResponseType<Comment>> addComment(
            @PathVariable Long videoId,
            @RequestBody StringRequest content
    ) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Video ID must not be null or less than 1"
            );
        }

        if (content == null || content.getField().isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide comment content"
            );
        }

        Comment newComment = commentService.addComment(videoId, content.getField());

        if (newComment == null) {
            throw new ErrorType(
                    400,
                    "Failed to add comment"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                    "Comment added successfully",
                    newComment,
                    200
                )
        );
    }

    @GetMapping("/c/{commentId}")
    public ResponseEntity<ResponseType<Comment>> getComment(
            @PathVariable Long commentId
    ) {
        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Comment ID must not be null or less than 1"
            );
        }

        Comment updatedComment = commentService.getComment(commentId);

        if (updatedComment == null) {
            throw new ErrorType(
                    400,
                    "Failed to fetch comment"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                    "Comment fetched successfully",
                    updatedComment,
                    200
                )
        );
    }

    @PatchMapping("/c/{commentId}")
    public ResponseEntity<ResponseType<Comment>> updateComment(
            @PathVariable Long commentId,
            @RequestBody StringRequest content
    ) {
        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Video ID must not be null or less than 1"
            );
        }

        if (content == null || content.getField().isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide comment content"
            );
        }

        Comment updatedComment = commentService.updateComment(commentId, content.getField());

        if (updatedComment == null) {
            throw new ErrorType(
                    400,
                    "Failed to update comment"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                    "Comment updated successfully",
                    updatedComment,
                    200
                )
        );
    }

    @DeleteMapping("/c/{commentId}")
    public ResponseEntity<ResponseType<Comment>> deleteComment(
            @PathVariable Long commentId
    ) {
        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Comment ID must not be null or less than 1"
            );
        }

        boolean deleted = commentService.deleteComment(commentId);

        if (!deleted) {
            throw new ErrorType(
                    400,
                    "Failed to delete comment"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                    "Comment with id " + commentId + " deleted successfully",
                    null,
                    200
                )
        );
    }
}
