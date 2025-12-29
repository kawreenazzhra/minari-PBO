package com.minari.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Cloudinary cloudinary;

    @Autowired
    public FileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Max file size handled by controller/servlet config, but we can double check here or just let Cloudinary handle it.
    // Cloudinary free tier has limits too, but usually generous for individual images (10MB is fine).

    public String uploadProductImage(MultipartFile file) throws IOException {
        return uploadFile(file, "minari/products");
    }

    public String uploadCategoryImage(MultipartFile file) throws IOException {
        return uploadFile(file, "minari/categories");
    }

    public String uploadUserImage(MultipartFile file) throws IOException {
        return uploadFile(file, "minari/users");
    }

    private String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Upload to Cloudinary
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", UUID.randomUUID().toString(),
                    "resource_type", "auto"
                ));

        // Return the secure URL
        return (String) uploadResult.get("secure_url");
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // Extract public ID from URL
            // URL format example: https://res.cloudinary.com/demo/image/upload/v1/minari/products/filename.jpg
            // We need: minari/products/filename
            
            String publicId = extractPublicIdFromUrl(fileUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            // Log error but don't stop execution
            System.err.println("Error deleting file from Cloudinary: " + e.getMessage());
        }
    }
    
    private String extractPublicIdFromUrl(String url) {
        try {
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }
            
            // Get part after /upload/ and version number (e.g., v123456789/)
            String path = url.substring(uploadIndex + 8);
            
            // Skip version number if present (starts with v followed by digits and /)
            if (path.matches("^v\\d+/.*")) {
                path = path.substring(path.indexOf('/') + 1);
            }
            
            // Remove extension
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex != -1) {
                path = path.substring(0, dotIndex);
            }
            
            return path;
        } catch (Exception e) {
            return null;
        }
    }
}
