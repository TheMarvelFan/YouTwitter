package com.mthree.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mthree.backend.models.Comment;
import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;
import com.mthree.backend.repositories.CommentRepository;
import com.mthree.backend.repositories.VideoRepository;
import com.mthree.backend.services.interfaces.CommentService;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.utils.ErrorType;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserService userService;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            VideoRepository videoRepository,
            UserService userService
    ) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    public Page<Comment> getComments(Long videoId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Video ID is invalid"
            );
        }

        Video video = this.videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Video not found"
                )
        );

        return this.commentRepository.findByVideo(video, pageable);
    }

    public Comment addComment(Long videoId, String content) {
        User user = this.userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    401,
                    "Please log in to leave a comment"
            );
        }

        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Video ID is invalid"
            );
        }

        Video video = this.videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Video not found"
                )
        );

        if (content == null || content.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Content is invalid"
            );
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setVideo(video);
        comment.setOwner(user);

        return this.commentRepository.save(comment);
    }

    public Comment getComment(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Comment ID is invalid"
            );
        }

        return this.commentRepository.findById(commentId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Comment not found"
                )
        );
    }

    public Comment updateComment(Long commentId, String content) {
        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Comment ID is invalid"
            );
        }

        if (content == null || content.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Content is invalid"
            );
        }

        User user = this.userService.getCurrentUser();

        Comment comment = this.commentRepository.findById(commentId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Comment not found"
                )
        );

        if (!comment.getOwner().getId().equals(user.getId())) {
            throw new ErrorType(
                    403,
                    "You are not authorized to update this comment"
            );
        }

        comment.setContent(content);

        return this.commentRepository.save(comment);
    }

    public boolean deleteComment(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Comment ID is invalid"
            );
        }

        User user = this.userService.getCurrentUser();

        Comment comment = this.commentRepository.findById(commentId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Comment not found"
                )
        );

        if (!comment.getOwner().getId().equals(user.getId())) {
            throw new ErrorType(
                    403,
                    "You are not authorized to update this comment"
            );
        }

        this.commentRepository.delete(comment);

        return true;
    }
}
