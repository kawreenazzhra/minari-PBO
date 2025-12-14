package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Customer extends User {
    
    @Column(name = "loyalty_points", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer loyaltyPoints = 0;
    
    @Column(name = "newsletter_subscribed", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean newsletterSubscribed = false;
    
    @Column(name = "member_since")
    protected LocalDateTime memberSince;
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShoppingCart shoppingCart;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductReview> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> savedAddresses = new ArrayList<>();

    // Constructors
    public Customer() {}

    public Customer(Long id, String email, String password, String fullName, String phone, UserRole role, Address shippingAddress, String avatarUrl, Boolean isActive, Boolean emailVerified, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLogin, Integer loyaltyPoints, Boolean newsletterSubscribed, LocalDateTime memberSince, ShoppingCart shoppingCart, List<Order> orders, List<ProductReview> reviews, List<Address> savedAddresses) {
        super(id, email, password, fullName, phone, role, shippingAddress, avatarUrl, isActive, emailVerified, createdAt, updatedAt, lastLogin);
        this.loyaltyPoints = loyaltyPoints;
        this.newsletterSubscribed = newsletterSubscribed;
        this.memberSince = memberSince;
        this.shoppingCart = shoppingCart;
        this.orders = orders;
        this.reviews = reviews;
        this.savedAddresses = savedAddresses;
    }

    // Getters and Setters
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Boolean getNewsletterSubscribed() {
        return newsletterSubscribed;
    }

    public void setNewsletterSubscribed(Boolean newsletterSubscribed) {
        this.newsletterSubscribed = newsletterSubscribed;
    }

    public LocalDateTime getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDateTime memberSince) {
        this.memberSince = memberSince;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
    }

    public List<Address> getSavedAddresses() {
        return savedAddresses;
    }

    public void setSavedAddresses(List<Address> savedAddresses) {
        this.savedAddresses = savedAddresses;
    }
    
    // Business methods
    public void addProductToCart(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (!product.isInStock()) throw new IllegalStateException("Product is out of stock");
        if (!product.canFulfillOrder(quantity)) {
            throw new IllegalStateException("Not enough stock available");
        }
        
        shoppingCart.addItem(product, quantity);
    }
    
    public boolean hasItemsInCart() {
        return shoppingCart != null && !shoppingCart.getItems().isEmpty();
    }
    
    public double getCartTotal() {
        return shoppingCart != null ? shoppingCart.getTotalAmount() : 0.0;
    }

    public void logout() {
        this.setLastLogin(java.time.LocalDateTime.now());
    }

    public boolean login(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}