package com.mthree.backend.services;

import java.io.File;
import java.io.IOException;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mthree.backend.services.interfaces.VideoDetailsService;

@Service
public class VideoDetailsServiceImpl implements VideoDetailsService {
    public double getVideoDuration(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("video", file.getOriginalFilename());
        file.transferTo(tempFile);  // Convert MultipartFile to File

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile)) {
            grabber.start();
            double durationInSeconds = grabber.getLengthInTime() / 1_000_000.0; // Convert microseconds to seconds
            grabber.stop();
            tempFile.delete();  // Cleanup temp file
            return durationInSeconds;
        } catch (Exception e) {
            throw new IOException("Error processing video file", e);
        }
    }
}
