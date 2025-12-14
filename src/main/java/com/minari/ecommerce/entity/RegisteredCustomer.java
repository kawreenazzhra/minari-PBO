package com.minari.ecommerce.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "registered_customers")
@PrimaryKeyJoinColumn(name = "user_id")
public class RegisteredCustomer extends Customer {
    
    @Column(name = "is_logged_in", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isLoggedIn = false;
    
    @Column(name = "wishlist_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer wishlistCount = 0;
    
    @Column(name = "total_orders", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalOrders = 0;
    
    @Column(name = "total_spent", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double totalSpent = 0.0;
    
    @Override
    public boolean login(String username, String password) {
        boolean success = this.getEmail().equals(username) && 
                         this.getPassword().equals(password);
        if (success) {
            this.isLoggedIn = true;
            this.setLastLogin(java.time.LocalDateTime.now());
        }
        return success;
    }
    
    @Override
    public void logout() {
        this.isLoggedIn = false;
    }
    
    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            this.setLoyaltyPoints(this.getLoyaltyPoints() + points);
        }
    }
    
    @PrePersist
    protected void onMemberCreate() {
        memberSince = java.time.LocalDateTime.now();
    }
}