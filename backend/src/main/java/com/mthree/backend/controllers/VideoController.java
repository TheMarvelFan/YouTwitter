package com.mthree.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mthree.backend.models.Video;
import com.mthree.backend.services.interfaces.VideoService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/videos")
public class    VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseType<Page<Video>>> getAllVideos(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        Page<Video> videos = videoService.getAllVideos(page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Videos fetched successfully",
                        videos,
                        200
                )
        );
    }

    @GetMapping("/current")
    public ResponseEntity<ResponseType<Page<Video>>> getCurrentUserVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Video> videos = videoService.getCurrentUserVideos(page, size);

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Videos fetched successfully",
                        videos,
                        200
                )
        );
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<ResponseType<Video>> getVideoById( // when implementing history, add video to requested user's history
                                                             // in future
            @PathVariable Long videoId
    ) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video id"
            );
        }

        Video video = videoService.getVideoById(videoId);

        if (video == null) {
            throw new ErrorType(
                    404,
                    "Video not found"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Video fetched successfully",
                        video,
                        200
                )
        );
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<ResponseType<String>> deleteVideo(
            @PathVariable Long videoId
    ) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video id"
            );
        }

        System.out.println("Id received is: " + videoId);

        boolean deleted = videoService.deleteVideo(videoId);

        if (!deleted) {
            throw new ErrorType(
                    404,
                    "Error deleting video"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Video deleted successfully",
                        null,
                        200
                )
        );
    }

    @PatchMapping("/{videoId}")
    public ResponseEntity<ResponseType<Video>> updateVideo(
            @PathVariable Long videoId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video id"
            );
        }

        if ((title == null || title.isEmpty()) && (description == null || description.isEmpty()) && (thumbnail == null)) {
            throw new ErrorType(
                    400,
                    "Please provide at least one field to update"
            );
        }

        Video updatedVideo = videoService.updateVideo(videoId, title, description, thumbnail);

        if (updatedVideo == null) {
            throw new ErrorType(
                    404,
                    "Error updating video"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Video updated successfully",
                        updatedVideo,
                        200
                )
        );
    }

    @PatchMapping("/toggle/publish/{videoId}")
    public ResponseEntity<ResponseType<Video>> togglePublish(
            @PathVariable Long videoId
    ) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video id"
            );
        }

        boolean status = videoService.togglePublish(videoId);

        return ResponseEntity.ok(
                new ResponseType<>(
                        status ? "Video published successfully" : "Video unpublished successfully",
                        null,
                        200
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<ResponseType<Video>> createVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestPart("videoFile") MultipartFile videoFile,
            @RequestPart("thumbnail") MultipartFile thumbnail
    ) {
        if (title == null || title.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video title"
            );
        }

        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Video File: " + videoFile.getName());
        System.out.println("Thumbnail: " + thumbnail.getName());

        if (description == null || description.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video description"
            );
        }

        if (videoFile == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video file"
            );
        }

        if (thumbnail == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid thumbnail"
            );
        }

        Video createdVideo = videoService.createVideo(title, description, videoFile, thumbnail);

        if (createdVideo == null) {
            throw new ErrorType(
                    400,
                    "Error creating video"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Video created successfully",
                        createdVideo,
                        200
                )
        );
    }
}
