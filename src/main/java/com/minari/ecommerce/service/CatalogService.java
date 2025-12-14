package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {
    
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    
    public CatalogService(ProductRepository productRepository, ProductCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    // Implementasi Search interface dari class asli
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Product> searchProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }
    
    public List<Product> searchProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    public List<Product> searchProductsByRating(int minRating) {
        return productRepository.findByMinRating((double) minRating);
    }
    
    // Category management methods for AdminController
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public ProductCategory getCategoryById(Long categoryId) {
        Optional<ProductCategory> category = categoryRepository.findById(categoryId);
        return category.orElse(null);
    }
    
    public ProductCategory saveCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}