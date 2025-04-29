package com.mthree.backend.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.mthree.backend.services.interfaces.CloudinaryService;
import com.mthree.backend.utils.ErrorType;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            Map map = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "resource_type", "auto"
            ));

            return (String) map.get("url");
        } catch (IOException e) {
            throw new ErrorType(
                    500,
                    "Error uploading file to Cloudinary: " + e.getMessage()
            );
        }
    }
}
