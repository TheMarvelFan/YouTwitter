package com.mthree.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mthree.backend.models.Tweet;
import com.mthree.backend.models.User;
import com.mthree.backend.repositories.TweetRepository;
import com.mthree.backend.repositories.UserRepository;
import com.mthree.backend.services.interfaces.TweetService;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.utils.ErrorType;

@Service
public class TweetServiceImpl implements TweetService {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public TweetServiceImpl(
            TweetRepository tweetRepository,
            UserRepository userRepository,
            UserService userService
    ) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Page<Tweet> getAllTweets(int page, int size) {
        return this.tweetRepository.findAll(PageRequest.of(page, size));
    }

    public Tweet createTweet(String content) {
        User user = userService.getCurrentUser();

        if (content == null || content.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide valid contentContent"
            );
        }

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not authenticated"
            );
        }

        Tweet newTweet = new Tweet();

        newTweet.setContent(content);
        newTweet.setOwner(user);

        return this.tweetRepository.save(newTweet);
    }

    public Page<Tweet> getUserTweets(Long userId, int page, int size) {
        if (userId == null || userId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid user ID"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "User not found"
                )
        );

        return this.tweetRepository.findAllByOwner(user, pageable);
    }

    public Tweet getTweet(Long tweetId) {
        if (tweetId == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid tweet ID"
            );
        }

        return this.tweetRepository.findById(tweetId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Tweet not found"
                )
        );
    }

    public Tweet updateTweet(Long tweetId, String content) {
        if (tweetId == null || tweetId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid tweet ID"
            );
        }

        if (content == null || content.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide valid content"
            );
        }

        User user = this.userService.getCurrentUser();

        Tweet existingTweet = this.tweetRepository.findById(tweetId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Tweet not found"
                )
        );

        if (!existingTweet.getOwner().getId().equals(user.getId())) {
            throw new ErrorType(
                    403,
                    "You are not authorized to update this tweet"
            );
        }

        existingTweet.setContent(content);

        return this.tweetRepository.save(existingTweet);
    }

    public boolean deleteTweet(Long tweetId) {
        if (tweetId == null || tweetId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid tweet ID"
            );
        }

        Tweet existingTweet = this.tweetRepository.findById(tweetId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Tweet not found"
                )
        );

        User user = this.userService.getCurrentUser();

        if (!existingTweet.getOwner().getId().equals(user.getId())) {
            throw new ErrorType(
                    403,
                    "You are not authorized to delete this tweet"
            );
        }

        this.tweetRepository.deleteById(tweetId);

        return true;
    }

    public Page<Tweet> getCurrentUserTweets(int page, int size) {
        User user = this.userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not authenticated"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return this.tweetRepository.findAllByOwner(user, pageable);
    }
}
