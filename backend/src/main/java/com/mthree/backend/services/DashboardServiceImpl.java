package com.mthree.backend.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mthree.backend.models.Comment;
import com.mthree.backend.models.Like;
import com.mthree.backend.models.LikeableType;
import com.mthree.backend.models.Playlist;
import com.mthree.backend.models.Tweet;
import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;
import com.mthree.backend.repositories.CommentRepository;
import com.mthree.backend.repositories.LikeRepository;
import com.mthree.backend.repositories.PlaylistRepository;
import com.mthree.backend.repositories.SubscriptionRepository;
import com.mthree.backend.repositories.TweetRepository;
import com.mthree.backend.repositories.VideoRepository;
import com.mthree.backend.services.interfaces.DashboardService;
import com.mthree.backend.models.Dashboard;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.utils.ErrorType;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final VideoRepository videoRepository;
    private final PlaylistRepository playlistRepository;
    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final LikeRepository likeRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public DashboardServiceImpl(
            VideoRepository videoRepository,
            PlaylistRepository playlistRepository,
            CommentRepository commentRepository,
            TweetRepository tweetRepository,
            LikeRepository likeRepository,
            SubscriptionRepository subscriptionRepository,
            UserService userService
    ) {
        this.videoRepository = videoRepository;
        this.playlistRepository = playlistRepository;
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.likeRepository = likeRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
    }

    public Page<Video> getVideos(int page, int size, String sort) {
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not logged in"
            );
        }

        return this.videoRepository.findByOwner(user, PageRequest.of(page, size, Sort.by(sort.substring(0, sort.indexOf(',')))));
    }

    public Dashboard getUserStats() {
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not logged in"
            );
        }

        List<Video> videos = this.videoRepository.findByOwner(user);

        long totalVideos = videos.size();

        long totalVideoViews = videos
                .stream()
                .mapToLong(Video::getViews)
                .sum();

        long totalVideoLikes = videos
                .stream()
                .mapToLong(video -> this.likeRepository.countByLikeableTypeAndLikeableId(LikeableType.VIDEO, video.getId()))
                .sum();

        long totalVideoComments = videos.stream()
                .mapToLong(this.commentRepository::countByVideo)
                .sum();

        List<Comment> comments = this.commentRepository.findByOwner(user);

        long commentsMadeCount = comments.size();

        long commentsMadeLikesCount = comments
                .stream()
                .mapToLong(comment -> this.likeRepository.countByLikeableTypeAndLikeableId(LikeableType.COMMENT, comment.getId()))
                .sum();

        List<Tweet> tweets = this.tweetRepository.findByOwner(user);

        long tweetLikesCount = tweets
                .stream()
                .mapToLong(tweet -> this.likeRepository.countByLikeableTypeAndLikeableId(LikeableType.TWEET, tweet.getId()))
                .sum();

        long tweetsCount = tweets.size();

        List<Like> likes = this.likeRepository.findByLikedBy(user);

        long tweetsLikedCount = likes
                .stream()
                .filter(like -> like.getLikeableType() == LikeableType.TWEET)
                .count();

        long videosLikedCount = likes
                .stream()
                .filter(like -> like.getLikeableType() == LikeableType.VIDEO)
                .count();

        long commentsLikedCount = likes
                .stream()
                .filter(like -> like.getLikeableType() == LikeableType.COMMENT)
                .count();

        long totalLikeCount = likes.size();

        long subscribersCount = this.subscriptionRepository.countBySubscriber(user);

        long subscribedToCount = this.subscriptionRepository.countByChannel(user);

        List<Playlist> playlists = this.playlistRepository.findByOwner(user);

        long playlistsCount = playlists.size();

        long totalCommentLikes = comments
                .stream()
                .mapToLong(comment -> this.likeRepository.countByLikeableTypeAndLikeableId(LikeableType.COMMENT, comment.getId()))
                .sum();

        return new Dashboard(
                totalVideos,
                totalVideoViews,
                totalVideoLikes,
                totalVideoComments,
                totalCommentLikes,
                videosLikedCount,
                commentsMadeCount,
                commentsMadeLikesCount,
                tweetLikesCount,
                tweetsCount,
                tweetsLikedCount,
                totalLikeCount,
                subscribersCount,
                subscribedToCount,
                playlistsCount,
                commentsLikedCount,
                playlists
        );
    }
}
