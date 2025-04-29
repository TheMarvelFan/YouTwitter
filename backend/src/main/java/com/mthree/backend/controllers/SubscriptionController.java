package com.mthree.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mthree.backend.models.Subscription;
import com.mthree.backend.services.interfaces.SubscriptionService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/c/check/{channelId}")
    public ResponseEntity<ResponseType<Subscription>> getSubscribedToThisChannel(
            @PathVariable Long channelId
    ) {
        if (channelId == null) {
            return ResponseEntity.badRequest().body(
                    new ResponseType<>(
                            "Channel ID is required",
                            null,
                            400
                    )
            );
        }

        Subscription subscription = subscriptionService.getSubscribedToThisChannel(channelId);

        if (subscription == null) {
            new ResponseType<>(
                    "Channel is not subscribed",
                    null,
                    200
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Channel is subscribed",
                        subscription,
                        200
                )
        );
    }

    @PostMapping("/c/{channelId}")
    public ResponseEntity<ResponseType<Subscription>> toggleSubscriptionToChannel(
            @PathVariable Long channelId
    ) {
        if (channelId == null) {
            throw new ErrorType(
                    400,
                    "Channel ID is required"
            );
        }

        boolean toggle = subscriptionService.toggleSubscription(channelId);

        return ResponseEntity.ok(
                new ResponseType<>(
                        toggle ? "Subscribed to channel successfully" : "Unsubscribed from channel successfully",
                        null,
                        200
                )
        );
    }

    @GetMapping("/u/{userId}")
    public ResponseEntity<ResponseType<List<Subscription>>> getUserSubscribedChannels(
            @PathVariable Long userId
    ) {
        if (userId == null) {
            throw new ErrorType(
                    400,
                    "User ID is required"
            );
        }

        List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(userId);

        if (subscriptions == null) {
            return ResponseEntity.ok(
                    new ResponseType<>(
                            "Subscriptions fetched successfully",
                            null,
                            200
                    )
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Subscriptions fetched successfully",
                        subscriptions,
                        200
                )
        );
    }

    @GetMapping("/c/{channelId}")
    public ResponseEntity<ResponseType<List<Subscription>>> getChannelSubscribers(
            @PathVariable Long channelId
    ) {
        if (channelId == null || channelId <= 0) {
            throw new ErrorType(
                    400,
                    "Enter a valid channel ID"
            );
        }

        List<Subscription> subscriptions = subscriptionService.getChannelSubscribers(channelId);

        if (subscriptions == null) {
            return ResponseEntity.ok(
                    new ResponseType<>(
                            "Subscriptions fetched successfully",
                            null,
                            200
                    )
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Subscriptions fetched successfully",
                        subscriptions,
                        200
                )
        );
    }
}
