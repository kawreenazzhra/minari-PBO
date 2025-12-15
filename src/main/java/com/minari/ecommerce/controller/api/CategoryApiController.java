package com.minari.ecommerce.controller.api;

import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.service.ProductCategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for Category API
 * Handles all category-related API endpoints for frontend
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryApiController {

    private static final Logger log = LoggerFactory.getLogger(CategoryApiController.class);

    private final ProductCategoryService categoryService;

    public CategoryApiController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/categories - Get all categories
     */
    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        try {
            log.info("Fetching all categories");
            List<ProductCategory> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error fetching categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/categories/{id} - Get category by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Long id) {
        try {
            log.info("Fetching category with id: {}", id);
            return categoryService.getCategoryById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/categories/active - Get all active categories
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProductCategory>> getActiveCategories() {
        try {
            log.info("Fetching all active categories");
            List<ProductCategory> categories = categoryService.getActiveCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error fetching active categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
