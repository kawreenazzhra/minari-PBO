package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Basic CRUD operations
    List<Product> findByNameContainingIgnoreCase(String name);

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description,
            Pageable pageable);

    // Find by category
    List<Product> findByCategory(ProductCategory category);

    // Find by category name
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    // Find by category ID
    List<Product> findByCategoryId(Long categoryId);

    // Price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // In stock
    List<Product> findByStockQuantityGreaterThan(Integer quantity);

    // Find by minimum rating
    @Query("SELECT p FROM Product p WHERE p.averageRating >= :minRating")
    List<Product> findByMinRating(@Param("minRating") Double minRating);

    // Search by name or description
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByNameOrDescription(@Param("query") String query);

    // Find featured products (high rating, in stock)
    @Query("SELECT p FROM Product p WHERE p.averageRating >= 4.0 AND p.stockQuantity > 0 ORDER BY p.averageRating DESC")
    List<Product> findFeaturedProducts();

    // Find new arrivals
    List<Product> findTop10ByOrderByCreatedAtDesc();

    // Find by multiple IDs
    List<Product> findByIdIn(List<Long> ids);

    // Active products
    List<Product> findByIsActiveTrue();

    // Count by category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    // Count active products
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = :isActive")
    Long countByIsActive(@Param("isActive") boolean isActive);

    // Count active products - boolean
    long countByIsActiveTrue();

    // Count products with stock less than specified quantity
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity < :quantity")
    Long countByStockQuantityLessThan(@Param("quantity") int quantity);

    // Count products with exact stock quantity
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = :quantity")
    Long countByStockQuantity(@Param("quantity") int quantity);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "DELETE FROM products WHERE id = :id", nativeQuery = true)
    void deleteNative(@Param("id") Long id);
}