package com.mthree.backend.models;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlists")
@Data
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToMany
    @JoinTable(
            name = "playlistvideos",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private Set<Video> videos = new HashSet<>();
}
