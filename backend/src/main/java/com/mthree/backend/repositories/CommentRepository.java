package com.mthree.backend.repositories;

import java.util.List;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mthree.backend.models.Comment;
import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @NonNull
    Page<Comment> findAll(@NonNull Pageable pageable);

    List<Comment> findByOwner(User owner);

    Page<Comment> findByVideo(Video video, Pageable pageable);

    Long countByVideo(Video video);
}
