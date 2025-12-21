package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    /**
     * Cari variant berdasarkan product id, size, dan color
     */
    Optional<ProductVariant> findByProductIdAndSizeAndColor(Long productId, String size, String color);
    
    /**
     * Cari semua variant untuk satu product
     */
    List<ProductVariant> findByProductId(Long productId);
    
    /**
     * Cari semua variant yang aktif untuk satu product
     */
    List<ProductVariant> findByProductIdAndIsActive(Long productId, Boolean isActive);
    
    /**
     * Cari variant berdasarkan SKU
     */
    Optional<ProductVariant> findBySku(String sku);
    
    /**
     * Cari variant dengan stock > 0
     */
    List<ProductVariant> findByProductIdAndStockQuantityGreaterThan(Long productId, Integer stock);
}
