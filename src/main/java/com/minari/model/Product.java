package com.minari.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale.Category;

public class Product {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String size;
    private String color;
    private String imageUrl;
    private Boolean active = true;
    private Category category;
    private List<Review> reviews;  
  
    // Constructors
    public Product() {}
    
    public Product(String id, String name, String description, Double price, Integer stock, 
                   Category category, String size, String color, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.size = size;
        this.color = color;
        this.imageUrl = imageUrl;
    }
    
    // Business Methods
    public void reduceStock(Integer quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
        }
    }
    
    public void increaseStock(Integer quantity) {
        this.stock += quantity;
    }
    
    public Boolean isInStock() {
        return this.stock > 0 && this.active;
    }

        public double calculateDiscount(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            return price;
        }
        return price * (1 - discountPercentage / 100);
    }

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        
        double totalRating = 0.0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }
    
    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
