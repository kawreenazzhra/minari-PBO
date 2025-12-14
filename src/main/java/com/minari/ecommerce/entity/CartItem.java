package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart shoppingCart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "added_date")
    private LocalDateTime addedDate;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "saved_for_later", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean savedForLater = false;

    // Constructors
    public CartItem() {}

    public CartItem(Long id, ShoppingCart shoppingCart, Product product, Integer quantity, Double unitPrice, String notes, LocalDateTime addedDate, LocalDateTime updatedAt, Boolean savedForLater) {
        this.id = id;
        this.shoppingCart = shoppingCart;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.notes = notes;
        this.addedDate = addedDate;
        this.updatedAt = updatedAt;
        this.savedForLater = savedForLater;
    }

    public CartItem(ShoppingCart shoppingCart, Product product, int quantity) {
        this.shoppingCart = shoppingCart;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.addedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getSavedForLater() {
        return savedForLater;
    }

    public void setSavedForLater(Boolean savedForLater) {
        this.savedForLater = savedForLater;
    }
        // Business methods
    public void updateQuantity(int newQuantity) {
        setQuantity(newQuantity);
        updatedAt = LocalDateTime.now();
    }
    
    public void increaseQuantity(int amount) {
        this.quantity += amount;
        updatedAt = LocalDateTime.now();
    }
    
    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
        updatedAt = LocalDateTime.now();
    }
    
    public double getSubtotal() {
        return unitPrice * quantity;
    }
    
    public boolean isPriceChanged() {
        return !unitPrice.equals(product.getPrice());
    }
    
    public double getPriceDifference() {
        return product.getPrice() - unitPrice;
    }
    
    public double getDiscountedPrice() {
        return product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
    }
    
    public double getDiscountedSubtotal() {
        return getDiscountedPrice() * quantity;
    }
    
    @PrePersist
    protected void onCreate() {
        addedDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}