package com.mthree.backend.services;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mthree.backend.models.Playlist;
import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;
import com.mthree.backend.models.requests.PlaylistUpdateRequest;
import com.mthree.backend.repositories.PlaylistRepository;
import com.mthree.backend.repositories.UserRepository;
import com.mthree.backend.repositories.VideoRepository;
import com.mthree.backend.services.interfaces.PlaylistService;

import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.utils.ErrorType;

@Service
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final UserService userService;

    public PlaylistServiceImpl(
            PlaylistRepository playlistRepository,
            UserRepository userRepository,
            VideoRepository videoRepository,
            UserService userService
    ) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    public Page<Playlist> getCreatedPlaylists(int page, int size) {
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not authenticated"
            );
        }

        return playlistRepository.findByOwner(user, PageRequest.of(page, size));
    }

    public Playlist createPlaylist(Playlist playlist) {
        if (playlist == null) {
            throw new ErrorType(
                    400,
                    "Please provide playlist details"
            );
        }

        User user = userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not authenticated"
            );
        }

        if (playlist.getDescription() == null || playlist.getDescription().isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide playlist description"
            );
        }

        if (playlist.getName() == null || playlist.getName().isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide playlist name"
            );
        }

        playlist.setOwner(user);

        return playlistRepository.save(playlist);
    }

    public Playlist updatePlaylist(Long playlistId, PlaylistUpdateRequest playlistReq) {
        if (playlistReq == null) {
            throw new ErrorType(
                    400,
                    "Please provide playlist details"
            );
        }

        if (playlistId == null || playlistId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide playlist ID"
            );
        }

        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "Playlist not found"
                )
        );

        User currentUser = userService.getCurrentUser();

        if (!Objects.equals(playlist.getOwner().getId(), currentUser.getId())) {
            throw new ErrorType(
                    400,
                    "Only playlist owner can modify it"
            );
        }

        if (playlistReq.getDescription() != null && !playlistReq.getDescription().isEmpty()) {
            playlist.setDescription(playlistReq.getDescription());
        }

        if (playlistReq.getName() != null && !playlistReq.getName().isEmpty()) {
            playlist.setName(playlistReq.getName());
        }

        return playlistRepository.save(playlist);
    }

    public Playlist getPlaylist(Long playlistId) {
        if (playlistId == null) {
            throw new ErrorType(
                    400,
                    "Please provide playlist ID"
            );
        }

        return playlistRepository.findById(playlistId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "Playlist not found"
                )
        );
    }

    public boolean deletePlaylist(Long playlistId) {
        if (playlistId == null || playlistId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide playlist ID"
            );
        }

        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "Playlist not found"
                )
        );

        User currentUser = userService.getCurrentUser();

        if (!Objects.equals(playlist.getOwner().getId(), currentUser.getId())) {
            throw new ErrorType(
                    400,
                    "Only playlist owner can delete it"
            );
        }

        playlistRepository.delete(playlist);

        return true;
    }

    public Page<Playlist> getUserPlaylists(Long userId, int page, int size) {
        if (userId == null || userId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid user ID"
            );
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "User not found"
                )
        );

        return playlistRepository.findByOwner(user, PageRequest.of(page, size));
    }

    public Playlist addVideoToPlaylist(Long playlistId, Long videoId) {
        if (playlistId == null || playlistId <= 0 || videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide valid playlist and video IDs"
            );
        }

        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "Video not found"
                )
        );

        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "Playlist not found"
                )
        );

        User currentUser = userService.getCurrentUser();

        if (!Objects.equals(playlist.getOwner().getId(), currentUser.getId())) {
            throw new ErrorType(
                    400,
                    "Only playlist owner can add videos to it"
            );
        }

        if (playlist.getVideos().contains(video)) {
            throw new ErrorType(
                    400,
                    "Video already exists in the playlist"
            );
        }

        if (!Objects.equals(video.getOwner().getId(), currentUser.getId())) {
            throw new ErrorType(
                    400,
                    "Only video owner can add it to playlists"
            );
        }

        playlist.getVideos().add(video);

        return playlistRepository.save(playlist);
    }

    public Playlist removeVideoFromPlaylist(Long playlistId, Long videoId) {
        if (playlistId == null || videoId == null || playlistId <= 0 || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide valid playlist and video IDs"
            );
        }

        if (videoRepository.findById(videoId).isEmpty()) {
            throw new ErrorType(
                    400,
                    "Video not found"
            );
        }

        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "Playlist not found"
                )
        );

        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        400,
                        "Video not found"
                )
        );

        User currentUser = userService.getCurrentUser();

        if (!Objects.equals(playlist.getOwner().getId(), currentUser.getId())) {
            throw new ErrorType(
                    400,
                    "Only playlist owner can remove videos from it"
            );
        }

        if (!playlist.getVideos().contains(video)) {
            throw new ErrorType(
                    400,
                    "Video not found in the playlist"
            );
        }

        if (!Objects.equals(video.getOwner().getId(), currentUser.getId())) {
            throw new ErrorType(
                    400,
                    "Only video owner can remove it from playlists"
            );
        }

        playlist.getVideos().removeIf(v -> v.getId().equals(videoId));

        return playlistRepository.save(playlist);
    }
}
