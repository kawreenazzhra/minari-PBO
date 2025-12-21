package com.minari.ecommerce.dto;

import java.io.Serializable;

/**
 * DTO untuk menyimpan item cart guest di session
 * Serializable sehingga bisa disimpan di HttpSession
 */
public class CartSessionItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long productId;
    private String productName;
    private Double price;
    private String size;
    private String color;
    private Integer quantity;
    private String imageUrl;
    
    // Constructor
    public CartSessionItem() {
    }
    
    public CartSessionItem(Long productId, String productName, Double price, 
                          String size, String color, Integer quantity, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
    
    // Getters & Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
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
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    // Utility methods
    public Double getSubtotal() {
        return price != null && quantity != null ? price * quantity : 0.0;
    }
}
