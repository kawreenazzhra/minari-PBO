package com.minari.ecommerce.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description is too long")
    private String description;
    
    @NotBlank(message = "Category image is required")
    private MultipartFile image;
    
    private Long parentCategoryId;
    private Integer displayOrder = 0;
    private Boolean isActive = true;
}