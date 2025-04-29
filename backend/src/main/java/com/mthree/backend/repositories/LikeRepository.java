package com.mthree.backend.repositories;

import java.util.List;

import com.mthree.backend.models.Comment;
import com.mthree.backend.models.Like;
import com.mthree.backend.models.LikeableType;
import com.mthree.backend.models.Tweet;
import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Page<Like> findAll(Pageable pageable);

    List<Like> findByLikedBy(User likedBy);

    Like findByLikeableTypeAndLikeableIdAndLikedBy(LikeableType type, Long id, User user);

    long countByLikeableTypeAndLikeableId(LikeableType likeableType, Long likeableId);

    Page<Like> findByLikedByAndLikeableType(User likedBy, LikeableType likeableType, Pageable pageable);
}
