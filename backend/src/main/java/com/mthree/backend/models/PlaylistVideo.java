package com.mthree.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlistvideos")
@Data
@NoArgsConstructor
public class PlaylistVideo {

    @Id
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
}
