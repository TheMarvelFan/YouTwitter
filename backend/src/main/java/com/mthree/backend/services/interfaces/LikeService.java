package com.mthree.backend.services.interfaces;

import org.springframework.data.domain.Page;

import com.mthree.backend.models.Like;
import com.mthree.backend.models.Video;

public interface LikeService {
     Like toggleLikeVideo(Long videoId);
     Like toggleLikeComment(Long commentId);
     Like toggleLikeTweet(Long tweetId);
     Page<Video> getLikedVideos(int page, int size);
}
