package com.mthree.backend.services;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mthree.backend.models.User;
import com.mthree.backend.models.Video;
import com.mthree.backend.repositories.VideoRepository;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.services.interfaces.VideoService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.services.interfaces.CloudinaryService;
import com.mthree.backend.services.interfaces.VideoDetailsService;

@Service
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final CloudinaryService cloudinaryService;
    private final VideoDetailsService videoDetailsService;
    private final UserService userService;

    private int ownerHasWatchedOwnVideo = 0;

    public VideoServiceImpl(
            VideoRepository videoRepository,
            CloudinaryService cloudinaryService,
            VideoDetailsService videoDetailsService,
            UserService userService
    ) {
        this.videoRepository = videoRepository;
        this.cloudinaryService = cloudinaryService;
        this.videoDetailsService = videoDetailsService;
        this.userService = userService;
    }

    public Page<Video> getAllVideos(int page, int size) {
        return this.videoRepository.findAllByisPublished(true, PageRequest.of(page, size));
    }

    public Page<Video> getCurrentUserVideos(int page, int size) {
        User currentUser = this.userService.getCurrentUser();

        if (currentUser == null) {
            throw new ErrorType(
                    401,
                    "Please login to view your videos"
            );
        }

        return this.videoRepository.findAllByOwner(currentUser, PageRequest.of(page, size));
    }

    public Video getVideoById(Long videoId) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video ID"
            );
        }

        Video video = this.videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Video not found"
                )
        );

        User currentUser = this.userService.getCurrentUser();

        if (!video.isPublished()) { // don't return video if it is private to anyone except owner of video
            if (video.getOwner().getId().equals(currentUser.getId())) {
                ownerHasWatchedOwnVideo += 1;
                if (ownerHasWatchedOwnVideo <= 1) { // allow video owner to increase own video's number of views only once
                    video.setViews(video.getViews() + 1);
                    videoRepository.save(video);
                }
                return video;
            } else {
                throw new ErrorType(
                        403,
                        "This video is private"
                );
            }
        }

        if (video.getOwner().getId().equals(currentUser.getId())) {
            ownerHasWatchedOwnVideo += 1;
            if (ownerHasWatchedOwnVideo <= 1) {
                video.setViews(video.getViews() + 1);
                videoRepository.save(video);
            }
            return video;
        }

        video.setViews(video.getViews() + 1); // in every other case increment video views by one

        return this.videoRepository.save(video);
    }

    public Video updateVideo(Long videoId, String title, String description, MultipartFile thumbnail) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video ID"
            );
        }

        Video existingVideo = this.videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Video not found"
                )
        );

        User user = this.userService.getCurrentUser();

        if (!user.getId().equals(existingVideo.getOwner().getId())) {
            throw new ErrorType(
                    403,
                    "You are not allowed to update this video"
            );
        }

        if (title != null && !title.isEmpty()) {
            existingVideo.setTitle(title);
        }

        if (description != null && !description.isEmpty()) {
            existingVideo.setDescription(description);
        }

        if (thumbnail != null) {
            String thumbnailUrl = cloudinaryService.upload(thumbnail);
            existingVideo.setThumbnail(thumbnailUrl);
        }

        return this.videoRepository.save(existingVideo);
    }

    public boolean deleteVideo(Long videoId) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video ID"
            );
        }

        Video video = this.videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Video not found"
                )
        );

        User user = this.userService.getCurrentUser();

        if (!user.getId().equals(video.getOwner().getId())) {
            throw new ErrorType(
                    403,
                    "You are not allowed to delete this video"
            );
        }

        return true;
    }

    public boolean togglePublish(Long videoId) {
        if (videoId == null || videoId <= 0) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video ID"
            );
        }

        Video video = this.videoRepository.findById(videoId).orElseThrow(
                () -> new ErrorType(
                        404,
                        "Video not found"
                )
        );

        User user = this.userService.getCurrentUser();

        if (!user.getId().equals(video.getOwner().getId())) {
            throw new ErrorType(
                    403,
                    "You are not allowed to publish or unpublish this video"
            );
        }

        video.setPublished(!video.isPublished());

        videoRepository.save(video);

        return video.isPublished();
    }

    public Video createVideo(String title, String description, MultipartFile videoFile, MultipartFile thumbnail) {
        User user = this.userService.getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    403,
                    "Please log in to upload a video"
            );
        }

        if (title == null || title.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid video title"
            );
        }

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

        Video video = new Video();

        video.setTitle(title);
        video.setDescription(description);

        String videoUrl = cloudinaryService.upload(videoFile);
        video.setVideoFile(videoUrl);

        String thumbnailUrl = cloudinaryService.upload(thumbnail);
        video.setThumbnail(thumbnailUrl);

        // set the owner of the video
         video.setOwner(user);

        video.setPublished(false); // set the video to private by default

        long duration;
        try {
            duration = (long) this.videoDetailsService.getVideoDuration(videoFile);
        } catch (IOException e) {
            throw new ErrorType(
                    500,
                    "Error getting video duration: " + e.getMessage()
            );
        }

        video.setDuration(duration);

        return this.videoRepository.save(video);
    }
}
