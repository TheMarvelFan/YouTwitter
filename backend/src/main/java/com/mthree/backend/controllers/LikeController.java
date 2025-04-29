package com.mthree.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mthree.backend.models.Like;
import com.mthree.backend.models.Video;
import com.mthree.backend.services.interfaces.LikeService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggle/v/{videoId}")
    public ResponseEntity<ResponseType<Like>> toggleLikeVideo(
            @PathVariable Long videoId
    ) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Invalid video ID"
            );
        }

        Like like = likeService.toggleLikeVideo(videoId);

        if (like == null) {
            throw new ErrorType(
                    500,
                    "Error liking video"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                    "Video liked successfully",
                    like,
                    200
                )
        );
    }

    @PostMapping("/toggle/c/{commentId}")
    public ResponseEntity<ResponseType<Like>> toggleLikeComment(
            @PathVariable Long commentId
    ) {
        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Invalid comment ID"
            );
        }

        Like like = likeService.toggleLikeComment(commentId);

        if (like == null) {
            throw new ErrorType(
                    500,
                    "Error liking comment"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Comment liked successfully",
                        like,
                        200
                )
        );
    }

    @PostMapping("/toggle/t/{tweetId}")
    public ResponseEntity<ResponseType<Like>> toggleLikeTweet(
            @PathVariable Long tweetId
    ) {
        if (tweetId == null || tweetId <= 0) {
            throw new ErrorType(
                    400,
                    "Invalid tweet ID"
            );
        }

        Like like = likeService.toggleLikeTweet(tweetId);

        if (like == null) {
            throw new ErrorType(
                    500,
                    "Error liking tweet"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Tweet liked successfully",
                        like,
                        200
                )
        );
    }

    @GetMapping("/videos")
    public ResponseEntity<ResponseType<Page<Video>>> getLikedVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Video> videos = likeService.getLikedVideos(page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Liked videos fetched successfully",
                        videos,
                        200
                )
        );
    }

    // future score: can make a /tweets route to get all liked tweets
    // future score: can make a /comments/{videoId} route to get all liked comments on specific video
}
