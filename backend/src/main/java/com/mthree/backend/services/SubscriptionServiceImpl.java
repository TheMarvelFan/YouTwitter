package com.mthree.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mthree.backend.models.Subscription;
import com.mthree.backend.models.User;
import com.mthree.backend.repositories.SubscriptionRepository;
import com.mthree.backend.repositories.UserRepository;
import com.mthree.backend.services.interfaces.SubscriptionService;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.utils.ErrorType;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            UserService userService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Subscription getSubscribedToThisChannel(Long channelId) {
        if (channelId == null || channelId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Channel ID"
            );
        }

        User channel = this.userRepository.findById(channelId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Channel not found"
                )
        );

        User user = this.userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    401,
                    "User not authenticated"
            );
        }

        return this.subscriptionRepository.findByChannelAndSubscriber(
                channel,
                user
        ).orElse(null);
    }

    public boolean toggleSubscription(Long channelId) {
        if (channelId == null || channelId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Channel ID"
            );
        }

        User channel = this.userRepository.findById(channelId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Channel not found"
                )
        );

        User user = this.userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    401,
                    "User not authenticated"
            );
        }

        Subscription subscription = this.subscriptionRepository.findByChannelAndSubscriber(
                channel,
                user
        ).orElse(null);

        if (subscription == null) {
            Subscription sub = new Subscription();
            sub.setChannel(channel);
            sub.setSubscriber(user);

            subscriptionRepository.save(sub);

            return true;
        } else {
            subscriptionRepository.delete(subscription);
            return false;
        }
    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid User ID"
            );
        }

        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "User not found"
                )
        );

        return subscriptionRepository.findAllBySubscriber(user);
    }

    @Override
    public List<Subscription> getChannelSubscribers(Long channelId) {
        if (channelId == null || channelId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Channel ID"
            );
        }

        User user = userRepository.findById(channelId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Channel not found"
                )
        );

        return subscriptionRepository.findAllByChannel(user);
    }
}

