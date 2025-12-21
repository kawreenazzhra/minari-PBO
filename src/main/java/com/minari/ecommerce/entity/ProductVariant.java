package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity untuk product variants (size, color, sku, stock)
 * Setiap product bisa punya banyak variant
 * Contoh: Dress bisa ada variant M-Red, M-Blue, L-Red, dll
 */
@Entity
@Table(name = "product_variants")
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "size", nullable = false)
    private String size; // XS, S, M, L, XL
    
    @Column(name = "color", nullable = false)
    private String color; // Red, Blue, White, etc
    
    @Column(name = "sku", unique = true)
    private String sku; // SKU unik per variant
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
    
    @Column(name = "price_adjustment")
    private Double priceAdjustment = 0.0; // +/- dari base price produk
    
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor
    public ProductVariant() {
    }
    
    public ProductVariant(Product product, String size, String color, Integer stockQuantity) {
        this.product = product;
        this.size = size;
        this.color = color;
        this.stockQuantity = stockQuantity;
        this.priceAdjustment = 0.0;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters & Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Double getPriceAdjustment() {
        return priceAdjustment;
    }
    
    public void setPriceAdjustment(Double priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility method
    public Double getFinalPrice() {
        if (product != null && product.getPrice() != null) {
            Double basePrice = product.getPrice();
            return priceAdjustment != null ? basePrice + priceAdjustment : basePrice;
        }
        return 0.0;
    }
    
    public boolean isInStock() {
        return stockQuantity > 0 && isActive;
    }
    
    @Override
    public String toString() {
        return "ProductVariant{" +
                "id=" + id +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
