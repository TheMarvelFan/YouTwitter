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

import com.mthree.backend.models.Tweet;
import com.mthree.backend.models.requests.StringRequest;
import com.mthree.backend.services.interfaces.TweetService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/tweets")
public class TweetController {
    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseType<Page<Tweet>>> getAllTweets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Tweet> tweets = tweetService.getAllTweets(page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Tweets fetched successfully",
                        tweets,
                        200
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<ResponseType<Tweet>> createTweet(
            @RequestBody StringRequest content
    ) {
        if (content == null || content.getField().isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide valid content"
            );
        }

        Tweet newTweet = tweetService.createTweet(content.getField());

        if (newTweet == null) {
            throw new ErrorType(
                    400,
                    "Error creating tweet"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Tweet created successfully",
                        newTweet,
                        200
                )
        );
    }

    @GetMapping("/current") // future scope
    public ResponseEntity<ResponseType<Page<Tweet>>> getCurrentUserTweets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Tweet> tweets = tweetService.getCurrentUserTweets(page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Current user tweets fetched successfully",
                        tweets,
                        200
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseType<Page<Tweet>>> getUserTweets(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (userId == null || userId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid user ID"
            );
        }

        Page<Tweet> tweets = tweetService.getUserTweets(userId, page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                        "User tweets fetched successfully",
                        tweets,
                        200
                )
        );
    }

    @GetMapping("/{tweetId}")
    public ResponseEntity<ResponseType<Tweet>> getTweet(
            @PathVariable Long tweetId
    ) {
        if (tweetId == null || tweetId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid tweet ID"
            );
        }

        Tweet tweet = tweetService.getTweet(tweetId);

        if (tweet == null) {
            throw new ErrorType(
                    404,
                    "Tweet not found"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Tweet fetched successfully",
                        tweet,
                        200
                )
        );
    }

    @PatchMapping("/{tweetId}")
    public ResponseEntity<ResponseType<Tweet>> updateTweet(
            @PathVariable Long tweetId,
            @RequestBody StringRequest content
    ) {
        System.out.println("Trying to modify tweet");

        if (tweetId == null || tweetId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid tweet ID"
            );
        }

        if (content == null || content.getField().isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide valid content"
            );
        }

        Tweet updatedTweet = tweetService.updateTweet(tweetId, content.getField());

        if (updatedTweet == null) {
            throw new ErrorType(
                    400,
                    "Error updating tweet"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Tweet updated successfully",
                        updatedTweet,
                        200
                )
        );
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<ResponseType<Void>> deleteTweet(
            @PathVariable Long tweetId
    ) {
        if (tweetId == null || tweetId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid tweet ID"
            );
        }

        boolean deleted = tweetService.deleteTweet(tweetId);

        if (!deleted) {
            throw new ErrorType(
                    500,
                    "Error deleting tweet"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Tweet deleted successfully",
                        null,
                        200
                )
        );
    }
}
