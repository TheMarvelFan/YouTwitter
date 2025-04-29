package com.mthree.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mthree.backend.models.Comment;
import com.mthree.backend.models.Like;
import com.mthree.backend.models.LikeableType;
import com.mthree.backend.models.Tweet;
import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;
import com.mthree.backend.repositories.CommentRepository;
import com.mthree.backend.repositories.LikeRepository;
import com.mthree.backend.repositories.VideoRepository;
import com.mthree.backend.services.interfaces.LikeService;
import com.mthree.backend.repositories.TweetRepository;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.utils.ErrorType;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final UserService userService;

    public LikeServiceImpl(
            LikeRepository likeRepository,
            VideoRepository videoRepository,
            CommentRepository commentRepository,
            TweetRepository tweetRepository,
            UserService userService
    ) {
        this.likeRepository = likeRepository;
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.userService = userService;
    }

    public Like toggleLikeVideo(Long videoId) {
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not found"
            );
        }

        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Invalid video ID"
            );
        }

        Video video = this.videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Video not found"
                )
        );

        Like like = this.likeRepository.findByLikeableTypeAndLikeableIdAndLikedBy(LikeableType.VIDEO, video.getId(), user);

        if (like == null) {
            like = new Like();
            like.setLikeableType(LikeableType.VIDEO);
            like.setLikeableId(videoId);
            like.setLikedBy(user);

            return likeRepository.save(like);
        }

        likeRepository.delete(like);

        return null;
    }

    public Like toggleLikeComment(Long commentId) {
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not found"
            );
        }

        if (commentId == null || commentId <= 0) {
            throw new ErrorType(
                    400,
                    "Invalid comment ID"
            );
        }

        Comment comment = this.commentRepository.findById(commentId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Comment not found"
                )
        );

        Like like = this.likeRepository.findByLikeableTypeAndLikeableIdAndLikedBy(LikeableType.COMMENT, comment.getId(), user);

        if (like == null) {
            like = new Like();
            like.setLikeableType(LikeableType.COMMENT);
            like.setLikeableId(commentId);
            like.setLikedBy(user);

            return likeRepository.save(like);
        }

        likeRepository.delete(like);

        return null;
    }

    public Like toggleLikeTweet(Long tweetId) {
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not found"
            );
        }

        if (tweetId == null || tweetId <= 0) {
            throw new ErrorType(
                    400,
                    "Invalid tweet ID"
            );
        }

        Tweet tweet = this.tweetRepository.findById(tweetId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Tweet not found"
                )
        );

        Like like = this.likeRepository.findByLikeableTypeAndLikeableIdAndLikedBy(LikeableType.TWEET, tweet.getId(), user);

        if (like == null) {
            like = new Like();
            like.setLikeableType(LikeableType.TWEET);
            like.setLikeableId(tweetId);
            like.setLikedBy(user);

            return likeRepository.save(like);
        }

        likeRepository.delete(like);

        return null;
    }

    public Page<Video> getLikedVideos(int page, int size) {
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not found"
            );
        }
        Page<Like> likes = likeRepository.findByLikedByAndLikeableType(
                user,
                LikeableType.VIDEO,
                PageRequest.of(page, size)
        );

        if (likes.isEmpty()) {
            return null;
        }

        return likes.map(like -> {
            Long videoId = like.getLikeableId();
            return videoRepository.findById(videoId)
                    .orElse(null);
        });
    }
}
