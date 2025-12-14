package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    
    public ProductService(ProductRepository productRepository, 
                         ProductCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
    
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<ProductCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public ProductCategory saveCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Statistics methods
    public long getTotalProducts() {
        return productRepository.count();
    }

    public long getActiveProducts() {
        return productRepository.countByIsActive(true);
    }

    public long getLowStockProducts() {
        return productRepository.countByStockQuantityLessThan(10);
    }

    public long getOutOfStockProducts() {
        return productRepository.countByStockQuantity(0);
    }
}