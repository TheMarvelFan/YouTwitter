package com.mthree.backend.repositories;

import java.util.List;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mthree.backend.models.Tweet;
import com.mthree.backend.models.User;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    @NonNull
    Page<Tweet> findAll(@NonNull Pageable pageable);

    List<Tweet> findByOwner(User owner);

    Page<Tweet> findAllByOwner(User owner, Pageable pageable);
}
