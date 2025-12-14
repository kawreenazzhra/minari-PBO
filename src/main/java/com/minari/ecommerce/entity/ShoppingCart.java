package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;
    
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "total_amount", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double totalAmount = 0.0;
    
    @Column(name = "item_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer itemCount = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "abandoned_reminder_sent", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean abandonedReminderSent = false;

    // Constructors
    public ShoppingCart() {}

    public ShoppingCart(Long id, Customer customer, List<CartItem> items, String sessionId, Double totalAmount, Integer itemCount, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean abandonedReminderSent) {
        this.id = id;
        this.customer = customer;
        this.items = items;
        this.sessionId = sessionId;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.abandonedReminderSent = abandonedReminderSent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Double getTotalAmount() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
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

    public Boolean getAbandonedReminderSent() {
        return abandonedReminderSent;
    }

    public void setAbandonedReminderSent(Boolean abandonedReminderSent) {
        this.abandonedReminderSent = abandonedReminderSent;
    }
    
    // Business methods
    public void addItem(Product product, int quantity) {
        CartItem existingItem = findItemByProduct(product);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(this, product, quantity);
            items.add(newItem);
        }
        updateCartTotals();
        updatedAt = LocalDateTime.now();
    }
    
    public boolean removeItem(Product product) {
        boolean removed = items.removeIf(item -> item.getProduct().equals(product));
        if (removed) {
            updateCartTotals();
            updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    public void updateItemQuantity(Product product, int newQuantity) {
        CartItem item = findItemByProduct(product);
        if (item != null) {
            if (newQuantity <= 0) {
                removeItem(product);
            } else {
                item.setQuantity(newQuantity);
                updateCartTotals();
                updatedAt = LocalDateTime.now();
            }
        }
    }
    
    public void clear() {
        items.clear();
        updateCartTotals();
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getTotalQuantity() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }
    
    private void updateCartTotals() {
        this.itemCount = items.size();
        this.totalAmount = getTotalAmount();
    }
    
    private CartItem findItemByProduct(Product product) {
        return items.stream()
                   .filter(item -> item.getProduct().equals(product))
                   .findFirst()
                   .orElse(null);
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