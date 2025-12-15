package com.minari.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:10MB}")
    private String maxFileSize;

    @Value("${app.upload.max-request-size:100MB}")
    private String maxRequestSize;

    @PostConstruct
    public void init() {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("📁 Created upload directory: " + uploadPath);
            }

            // Create sub-directories
            String[] subdirs = { "products", "categories", "users", "banners", "promotions" };
            for (String subdir : subdirs) {
                Path subdirPath = uploadPath.resolve(subdir);
                if (!Files.exists(subdirPath)) {
                    Files.createDirectories(subdirPath);
                    System.out.println("📁 Created sub-directory: " + subdirPath);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        // Expose upload directory as static resource
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(Paths.get(uploadDir).toAbsolutePath().toUri().toString())
                .setCachePeriod(3600)
                .resourceChain(true);

        // Expose static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}