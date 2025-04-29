package com.mthree.backend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mthree.backend.models.Playlist;
import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Page<Playlist> findAll(Pageable pageable);

    Page<Playlist> findByOwner(User owner, Pageable pageable);

    List<Playlist> findByOwner(User owner);
}
