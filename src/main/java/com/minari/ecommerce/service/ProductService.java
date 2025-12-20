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
    private final com.minari.ecommerce.repository.OrderItemRepository orderItemRepository;
    private final com.minari.ecommerce.repository.CartItemRepository cartItemRepository;
    private final com.minari.ecommerce.repository.ProductReviewRepository productReviewRepository;
    private final com.minari.ecommerce.repository.ProductVariantRepository productVariantRepository;
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public ProductService(ProductRepository productRepository,
            ProductCategoryRepository categoryRepository,
            com.minari.ecommerce.repository.OrderItemRepository orderItemRepository,
            com.minari.ecommerce.repository.CartItemRepository cartItemRepository,
            com.minari.ecommerce.repository.ProductReviewRepository productReviewRepository,
            com.minari.ecommerce.repository.ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productReviewRepository = productReviewRepository;
        this.productVariantRepository = productVariantRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.searchByNameOrDescription(query);
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
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // 1. Unlink from Category explicitly (Managed Relationship)
            ProductCategory category = product.getCategory();
            if (category != null) {
                category.getProducts().remove(product);
                product.setCategory(null);
                categoryRepository.save(category); // Save the captured category, not product.getCategory() which is now
                                                   // null
            }

            // 2. Unlink from OrderItems (Preserve Order History)
            try {
                orderItemRepository.unlinkProduct(id);
            } catch (Exception e) {
                System.err.println("Failed to unlink product from order items: " + e.getMessage());
                // Fallback: Delete order items if unlink fails
                orderItemRepository.deleteByProductId(id);
            }

            // 3. Delete dependent entities (Cascade is usually enough, but explicit is
            // safer)
            cartItemRepository.deleteAll(cartItemRepository.findByProductId(product.getId()));
            productReviewRepository.deleteAll(productReviewRepository.findByProductId(product.getId()));
            productVariantRepository.deleteAll(productVariantRepository.findByProductId(product.getId()));

            // 4. Delete the Product (Native Query to bypass JPA Caching/Managed State)
            productRepository.deleteNative(id);

            // 5. Force Flush (although native query usually auto-flushes)
            productRepository.flush();

            // 6. Clear persistence context to prevent dirty check resurrection
            entityManager.clear();
        }
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