package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews")
public class ProductReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "review_title")
    private String reviewTitle;
    
    @Column(name = "review_text", columnDefinition = "TEXT", nullable = false)
    private String reviewText;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(name = "is_verified_purchase", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerifiedPurchase = false;
    
    @Column(name = "is_approved", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isApproved = false;
    
    @Column(name = "helpful_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer helpfulCount = 0;
    
    @Column(name = "not_helpful_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer notHelpfulCount = 0;
    
    @Column(name = "report_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer reportCount = 0;
    
    @Column(name = "review_date")
    private LocalDateTime reviewDate;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ProductReview() {}

    public ProductReview(Long id, Product product, Customer customer, String reviewTitle, String reviewText, Integer rating, Boolean isVerifiedPurchase, Boolean isApproved, Integer helpfulCount, Integer notHelpfulCount, Integer reportCount, LocalDateTime reviewDate, LocalDateTime updatedAt) {
        this.id = id;
        this.product = product;
        this.customer = customer;
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.rating = rating;
        this.isVerifiedPurchase = isVerifiedPurchase;
        this.isApproved = isApproved;
        this.helpfulCount = helpfulCount;
        this.notHelpfulCount = notHelpfulCount;
        this.reportCount = reportCount;
        this.reviewDate = reviewDate;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getIsVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    public void setIsVerifiedPurchase(Boolean isVerifiedPurchase) {
        this.isVerifiedPurchase = isVerifiedPurchase;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public Integer getNotHelpfulCount() {
        return notHelpfulCount;
    }

    public void setNotHelpfulCount(Integer notHelpfulCount) {
        this.notHelpfulCount = notHelpfulCount;
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public boolean isPositiveReview() {
        return rating >= 4;
    }
    
    public String getRatingStars() {
        return "⭐".repeat(rating) + "☆".repeat(5 - rating);
    }
    
    public void markHelpful() {
        this.helpfulCount++;
    }
    
    public void markNotHelpful() {
        this.notHelpfulCount++;
    }
    
    public void report() {
        this.reportCount++;
    }
    
    @PrePersist
    protected void onCreate() {
        reviewDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}