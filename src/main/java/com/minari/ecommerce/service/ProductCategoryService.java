package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.repository.ProductCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {
    
    private final ProductCategoryRepository categoryRepository;
    
    public ProductCategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public ProductCategory createCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }
    
    public ProductCategory updateCategory(Long id, ProductCategory categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(categoryDetails.getName());
            category.setSlug(categoryDetails.getSlug());
            category.setDescription(categoryDetails.getDescription());
            category.setImageUrl(categoryDetails.getImageUrl());
            category.setBannerUrl(categoryDetails.getBannerUrl());
            category.setMetaTitle(categoryDetails.getMetaTitle());
            category.setMetaDescription(categoryDetails.getMetaDescription());
            category.setMetaKeywords(categoryDetails.getMetaKeywords());
            category.setIsActive(categoryDetails.getIsActive());
            category.setDisplayOrder(categoryDetails.getDisplayOrder());
            return categoryRepository.save(category);
        }).orElse(null);
    }
    
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public Optional<ProductCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public List<ProductCategory> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }
    
    public Page<ProductCategory> searchCategories(String keyword, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            keyword, keyword, pageable);
    }
    
    public long getTotalCategories() {
        return categoryRepository.count();
    }
    
    public long getActiveCategories_count() {
        return categoryRepository.countByIsActiveTrue();
    }
}
