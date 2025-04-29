package com.mthree.backend.services.interfaces;

import org.springframework.data.domain.Page;

import com.mthree.backend.models.Playlist;
import com.mthree.backend.models.requests.PlaylistUpdateRequest;

public interface PlaylistService {
    Page<Playlist> getCreatedPlaylists(int page, int size);
    Playlist createPlaylist(Playlist playlist);
    Playlist updatePlaylist(Long playlistId, PlaylistUpdateRequest playlistReq);
    Playlist getPlaylist(Long playlistId);
    boolean deletePlaylist(Long playlistId);
    Page<Playlist> getUserPlaylists(Long userId, int page, int size);
    Playlist addVideoToPlaylist(Long playlistId, Long videoId);
    Playlist removeVideoFromPlaylist(Long playlistId, Long videoId);
}
