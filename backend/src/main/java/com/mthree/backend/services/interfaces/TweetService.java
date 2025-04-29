package com.mthree.backend.services.interfaces;

import org.springframework.data.domain.Page;

import com.mthree.backend.models.Tweet;

public interface TweetService {
    // Fetch all tweets with pagination
    Page<Tweet> getAllTweets(int page, int size);

    // Create a new tweet
    Tweet createTweet(String content);

    // Fetch tweets by user ID with pagination
    Page<Tweet> getUserTweets(Long userId, int page, int size);

    // Fetch all tweets by currently logged-in User with pagination
    Page<Tweet> getCurrentUserTweets(int page, int size);

    // Fetch a specific tweet by ID
    Tweet getTweet(Long tweetId);

    // Update a specific tweet by ID
    Tweet updateTweet(Long tweetId, String content);

    // Delete a specific tweet by ID
    boolean deleteTweet(Long tweetId);
}
