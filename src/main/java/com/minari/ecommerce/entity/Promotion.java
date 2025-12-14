package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "promo_code", unique = true)
    private String promoCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
    
    @Column(name = "discount_value", nullable = false)
    private Double discountValue;
    
    @Column(name = "min_purchase_amount", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double minPurchaseAmount = 0.0;
    
    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "used_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer usedCount = 0;
    
    @Column(name = "customer_usage_limit")
    private Integer customerUsageLimit;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;
    
    @Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isPublic = true;
    
    @Column(name = "banner_image_url")
    private String bannerImageUrl;
    
    @Column(name = "applicable_categories")
    private String applicableCategories; // JSON array of category IDs
    
    @Column(name = "excluded_products")
    private String excludedProducts; // JSON array of product IDs
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT, BUY_X_GET_Y, FREE_SHIPPING
    }
    
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && now.isAfter(startDate) && now.isBefore(endDate);
    }
    
    public double applyDiscount(double originalPrice) {
        if (!isValid()) {
            return originalPrice;
        }
        
        if (discountType == DiscountType.PERCENTAGE) {
            double discount = originalPrice * (discountValue / 100);
            if (maxDiscountAmount != null && discount > maxDiscountAmount) {
                discount = maxDiscountAmount;
            }
            return originalPrice - discount;
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            return Math.max(0, originalPrice - discountValue);
        }
        return originalPrice;
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
}