package com.mthree.backend.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String upload(MultipartFile file);
}
