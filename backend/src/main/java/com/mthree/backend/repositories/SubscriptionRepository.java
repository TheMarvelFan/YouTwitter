package com.mthree.backend.repositories;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mthree.backend.models.Subscription;
import com.mthree.backend.models.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @NonNull
    Page<Subscription> findAll(@NonNull Pageable pageable);

    List<Subscription> findAllByChannel(User channel);

    List<Subscription> findAllBySubscriber(User subscriber);

    Subscription findBySubscriberAndChannel(User subscriber, User channel);

    long countBySubscriber(User subscriber);

    long countByChannel(User channel);

    Optional<Subscription> findByChannelAndSubscriber(User channel, User subscriber);
}
