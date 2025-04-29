package com.mthree.backend.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.mthree.backend.models.Video;

public interface VideoService {
    Page<Video> getAllVideos(int page, int size);
    Page<Video> getCurrentUserVideos(int page, int size);
    Video getVideoById(Long videoId);
    Video updateVideo(Long videoId, String title, String description, MultipartFile thumbnail);
    boolean deleteVideo(Long videoId);
    Video createVideo(String title, String description, MultipartFile videoFile, MultipartFile thumbnail);
    boolean togglePublish(Long videoId);
}
