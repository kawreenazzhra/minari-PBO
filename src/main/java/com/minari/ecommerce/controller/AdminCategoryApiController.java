package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.service.ProductCategoryService;
import com.minari.ecommerce.service.FileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * API Controller for Category CRUD via JavaScript fetch
 * Handles POST (create/update) and DELETE operations with JSON responses
 */
@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryApiController {
    
    private final ProductCategoryService categoryService;
    private final FileUploadService fileUploadService;
    
    public AdminCategoryApiController(ProductCategoryService categoryService,
                                     FileUploadService fileUploadService) {
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
    }
    
    @PostMapping(consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer displayOrder,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) MultipartFile image) {
        
        try {
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Category name is required"));
            }
            
            ProductCategory category = new ProductCategory();
            category.setName(name);
            category.setDescription(description);
            category.setDisplayOrder(displayOrder != null ? displayOrder : 0);
            category.setIsActive(isActive);
            
            // Generate slug
            String slug = name.toLowerCase().replaceAll("\\s+", "-");
            category.setSlug(slug);
            
            // Handle image upload
            if (image != null && !image.isEmpty()) {
                try {
                    String imagePath = fileUploadService.uploadCategoryImage(image);
                    category.setImageUrl(imagePath);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "error", "Image upload failed: " + e.getMessage()));
                }
            }
            
            categoryService.createCategory(category);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Category created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @PostMapping(value = "/{id}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer displayOrder,
            @RequestParam(required = false, defaultValue = "false") Boolean isActive,
            @RequestParam(required = false) MultipartFile image) {
        
        try {
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Category name is required"));
            }
            
            ProductCategory category = categoryService.getCategoryById(id)
                    .orElse(null);
            
            if (category == null) {
                return ResponseEntity.notFound().build();
            }
            
            category.setName(name);
            category.setDescription(description);
            category.setDisplayOrder(displayOrder != null ? displayOrder : 0);
            category.setIsActive(isActive);
            
            // Only update slug if name changed
            if (!category.getSlug().equals(name.toLowerCase().replaceAll("\\s+", "-"))) {
                category.setSlug(name.toLowerCase().replaceAll("\\s+", "-"));
            }
            
            // Handle image upload - only if new image provided
            if (image != null && !image.isEmpty()) {
                try {
                    String imagePath = fileUploadService.uploadCategoryImage(image);
                    category.setImageUrl(imagePath);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "error", "Image upload failed: " + e.getMessage()));
                }
            }
            // If no new image provided, keep existing image (do nothing)
            
            ProductCategory updated = categoryService.updateCategory(id, category);
            
            if (updated != null) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Category updated successfully!"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Category deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
