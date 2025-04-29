package com.mthree.backend.services.interfaces;

import java.util.List;

import com.mthree.backend.models.Subscription;

public interface SubscriptionService {
    Subscription getSubscribedToThisChannel(Long channelId);

    boolean toggleSubscription(Long channelId);

    List<Subscription> getUserSubscriptions(Long userId);

    List<Subscription> getChannelSubscribers(Long channelId);
}
