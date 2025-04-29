package com.mthree.backend.services.interfaces;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface VideoDetailsService {
    double getVideoDuration(MultipartFile videoUrl) throws IOException;
}
