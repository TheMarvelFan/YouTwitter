package com.mthree.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "playlistvideos")
@Data
@NoArgsConstructor
public class PlaylistVideo {
    @Id
    @ManyToOne
    @JoinColumn(name = "playlistId")
    private Playlist playlist;

    @Id
    @ManyToOne
    @JoinColumn(name = "videoId")
    private Video video;
}
