package com.mthree.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mthree.backend.models.Playlist;
import com.mthree.backend.models.requests.PlaylistUpdateRequest;
import com.mthree.backend.services.interfaces.PlaylistService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseType<Page<Playlist>>> getCreatedPlaylists(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        System.out.println("Request received loud and clear");
        Page<Playlist> playlists = playlistService.getCreatedPlaylists(page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                        "User playlists fetched successfully",
                        playlists,
                        200
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<ResponseType<Playlist>> createPlaylist(
            @RequestBody Playlist playlist
    ) {
        if (playlist == null) {
            throw new ErrorType(
                    400,
                    "Please provide playlist details"
            );
        }

        Playlist newPlaylist = playlistService.createPlaylist(playlist);

        if (newPlaylist == null) {
            throw new ErrorType(
                    500,
                    "Error creating playlist"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Playlist created successfully",
                        newPlaylist,
                        200
                )
        );
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<ResponseType<Playlist>> getPlaylist(
            @PathVariable Long playlistId
    ) {
        if (playlistId == null || playlistId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid playlist ID"
            );
        }

        Playlist playlist = playlistService.getPlaylist(playlistId);

        if (playlist == null) {
            throw new ErrorType(
                    500,
                    "Error fetching playlist"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Playlist fetched successfully",
                        playlist,
                        200
                )
        );
    }

    @PatchMapping("/{playlistId}")
    public ResponseEntity<ResponseType<Playlist>> updatePlaylist(
            @PathVariable Long playlistId,
            @RequestBody PlaylistUpdateRequest playlistReq
    ) {
        if (playlistId == null || playlistId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid playlist ID"
            );
        }

        if (playlistReq == null) {
            throw new ErrorType(
                    400,
                    "Please provide playlist details"
            );
        }

        Playlist updatedPlaylist = playlistService.updatePlaylist(playlistId, playlistReq);

        if (updatedPlaylist == null) {
            throw new ErrorType(
                    500,
                    "Error updating playlist"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Playlist updated successfully",
                        updatedPlaylist,
                        200
                )
        );
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<ResponseType<Void>> deletePlaylist(
            @PathVariable Long playlistId
    ) {
        if (playlistId == null || playlistId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid playlist ID"
            );
        }

        boolean deleted = playlistService.deletePlaylist(playlistId);

        if (!deleted) {
            throw new ErrorType(
                    500,
                    "Error deleting playlist"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Playlist deleted successfully",
                        null,
                        200
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseType<Page<Playlist>>> getUserPlaylists(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (userId == null || userId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid user ID"
            );
        }

        Page<Playlist> playlists = playlistService.getUserPlaylists(userId, page, size);

        if (playlists == null) {
            throw new ErrorType(
                    500,
                    "Error fetching user playlists"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Channel playlists fetched successfully",
                        playlists,
                        200
                )
        );
    }

    @PatchMapping("/add/{playlistId}/{videoId}")
    public ResponseEntity<ResponseType<Playlist>> addVideoToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long videoId
    ) {
        if (playlistId == null || playlistId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid playlist ID"
            );
        }

        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video ID"
            );
        }

        Playlist updatedPlaylist = playlistService.addVideoToPlaylist(playlistId, videoId);

        if (updatedPlaylist == null) {
            throw new ErrorType(
                    500,
                    "Error adding video to playlist"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Video added to playlist successfully",
                        updatedPlaylist,
                        200
                )
        );
    }

    @PatchMapping("/remove/{playlistId}/{videoId}")
    public ResponseEntity<ResponseType<Playlist>> removeVideoFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long videoId
    ) {
        if (playlistId == null || playlistId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid playlist ID"
            );
        }

        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video ID"
            );
        }

        Playlist updatedPlaylist = playlistService.removeVideoFromPlaylist(playlistId, videoId);

        if (updatedPlaylist == null) {
            throw new ErrorType(
                    500,
                    "Error removing video from playlist"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Video removed from playlist successfully",
                        updatedPlaylist,
                        200
                )
        );
    }
}
