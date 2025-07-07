package com.mthree.backend.repositories;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @NonNull
    Page<Video> findAll(@NonNull Pageable pageable);

    Page<Video> findByOwner(User owner, Pageable pageable);

    List<Video> findByOwner(User owner);

    Page<Video> findAllByOwner(User owner, Pageable pageable);

    @NonNull
    Optional<Video> findById(@NonNull Long id);

    Page<Video> findAllByisPublished(boolean is_published, Pageable pageable);
}
