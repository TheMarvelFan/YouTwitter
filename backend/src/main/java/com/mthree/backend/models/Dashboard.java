package com.mthree.backend.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dashboard {
    long totalVideos;
    long totalVideoViews;
    long totalVideoLikes;
    long totalVideoComments;
    long totalCommentLikes;
    long videosLikedCount;
    long commentsLikedCount;
    long commentsMadeCount;
    long commentsMadeLikesCount;
    long tweetLikesCount;
    long tweetsCount;
    long tweetsLikedCount;
    long totalLikeCount;
    long subscribersCount;
    long subscribedToCount;
    long playlistsCount;
//    long totalVideosWatchedCount; // add this when implementing the video watch history
    List<Playlist> playlists;
}
