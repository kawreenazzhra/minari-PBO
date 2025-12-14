package com.minari.ecommerce.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;
    
    @Size(max = 500, message = "Short description is too long")
    private String shortDescription;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "1000000000", message = "Price is too high")
    private BigDecimal price;
    
    @DecimalMin(value = "0.00", message = "Discount price cannot be negative")
    private BigDecimal discountPrice;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
    
    private String brand;
    private BigDecimal weight;
    private String dimensions;
    
    // Main image
    @NotNull(message = "Main image is required")
    private MultipartFile mainImage;
    
    // Additional images (optional)
    private List<MultipartFile> additionalImages;
    
    private Boolean isFeatured = false;
    private Boolean isActive = true;
    
    // Variants
    private List<ProductVariantRequest> variants;

    public static class ProductVariantRequest {
        @NotBlank(message = "Variant name is required")
        private String name;
        
        @NotBlank(message = "Variant value is required")
        private String value;
        
        @DecimalMin(value = "0.00", message = "Additional price cannot be negative")
        private BigDecimal additionalPrice;
        
        @Min(value = 0, message = "Stock cannot be negative")
        private Integer stockQuantity;
        
        private String sku;
        private MultipartFile image;
    }
}