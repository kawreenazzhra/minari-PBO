package com.minari.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${app.upload.dir}")
    private String uploadDir;
    
    private final List<String> allowedImageTypes = Arrays.asList(
        "image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp"
    );
    
    private final long maxFileSize = 10 * 1024 * 1024; // 10MB
    
    public String storeProductImage(MultipartFile file) throws IOException {
        return storeFile(file, "products");
    }
    
    public String storeCategoryImage(MultipartFile file) throws IOException {
        return storeFile(file, "categories");
    }
    
    public String storeUserAvatar(MultipartFile file) throws IOException {
        return storeFile(file, "users");
    }
    
    public String storePromotionBanner(MultipartFile file) throws IOException {
        return storeFile(file, "promotions");
    }
    
    private String storeFile(MultipartFile file, String subdirectory) throws IOException {
        // Validasi file
        validateFile(file);
        
        // Generate nama file unik
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Tentukan path tujuan
        Path targetLocation = Paths.get(uploadDir)
                .resolve(subdirectory)
                .resolve(uniqueFilename);
        
        // Simpan file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Return URL relatif
        return "/uploads/" + subdirectory + "/" + uniqueFilename;
    }
    
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        
        // Validasi tipe file
        String contentType = file.getContentType();
        if (contentType == null || !allowedImageTypes.contains(contentType.toLowerCase())) {
            throw new IOException("Invalid file type. Allowed types: " + allowedImageTypes);
        }
        
        // Validasi ukuran file
        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds limit (10MB)");
        }
        
        // Validasi nama file
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename.contains("..")) {
            throw new IOException("Filename contains invalid path sequence");
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex).toLowerCase();
        }
        return ".jpg"; // Default extension
    }
    
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return;
        }
        
        String relativePath = fileUrl.substring("/uploads/".length());
        Path filePath = Paths.get(uploadDir).resolve(relativePath);
        
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
    
    public String resizeAndStoreImage(MultipartFile file, String subdirectory, int width, int height) throws IOException {
        // Store original first
        String originalUrl = storeFile(file, subdirectory);
        
        // TODO: Implement image resizing using ImageIO or Thumbnailator
        // For now, return the original URL
        return originalUrl;
    }
}