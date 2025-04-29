package com.mthree.backend.services.interfaces;

import org.springframework.data.domain.Page;

import com.mthree.backend.models.Comment;

public interface CommentService {
    // Method to get comments for a video
    Page<Comment> getComments(Long videoId, int page, int size);

    // Method to add a comment to a video
    Comment addComment(Long videoId, String content);

    // Method to delete a comment
    boolean deleteComment(Long commentId);

    // Method to update a comment
    Comment updateComment(Long commentId, String content);

    // Method to get a comment by ID
    Comment getComment(Long commentId);
}
