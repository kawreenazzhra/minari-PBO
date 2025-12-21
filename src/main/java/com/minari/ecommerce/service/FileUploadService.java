package com.minari.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.allowed-types}")
    private String allowedTypes;

    // Max file size in bytes (10MB = 10485760 bytes)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public String uploadProductImage(MultipartFile file) throws IOException {
        return uploadFile(file, "products");
    }

    public String uploadCategoryImage(MultipartFile file) throws IOException {
        return uploadFile(file, "categories");
    }

    public String uploadUserImage(MultipartFile file) throws IOException {
        return uploadFile(file, "users");
    }

    private String uploadFile(MultipartFile file, String subdirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + allowedTypes);
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of " + MAX_FILE_SIZE + " bytes");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // Create directory path
        Path uploadPath = Paths.get(uploadDir, subdirectory).toAbsolutePath();
        Files.createDirectories(uploadPath);

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());

        // Return relative path for storing in database
        return "/uploads/" + subdirectory + "/" + uniqueFilename;
    }

    public void deleteFile(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        // Remove /uploads/ prefix to get relative path
        String relativePath = filePath.replace("/uploads/", "");
        Path fullPath = Paths.get(uploadDir, relativePath).toAbsolutePath();

        if (Files.exists(fullPath)) {
            Files.delete(fullPath);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
