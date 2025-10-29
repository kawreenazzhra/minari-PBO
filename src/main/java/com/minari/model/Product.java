package com.minari.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale.Category;

public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Category category;
    private String size;
    private String color;
    private String imageUrl;
    private List<Review> reviews;
    private boolean active;

    // Constructor
    public Product(String productId, String name, String description, double price, 
                   int stock, Category category, String size, String color, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.size = size;
        this.color = color;
        this.imageUrl = imageUrl;
        this.reviews = new ArrayList<>();
        this.active = true;
    }

    // Business Methods
    public void addReview(Review review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        reviews.add(review);
    }

    public boolean reduceStock(int quantity) {
        if (quantity <= 0 || quantity > stock) {
            return false;
        }
        this.stock -= quantity;
        return true;
    }

    public void increaseStock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
        }
    }

    public boolean isInStock() {
        return stock > 0 && active;
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

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Review> getReviews() {
        return new ArrayList<>(reviews); // Return copy untuk encapsulation
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("Product[ID: %s, Name: %s, Price: Rp %,.2f, Stock: %d, Category: %s]", 
                           productId, name, price, stock, category != null ? category.getName() : "Uncategorized");
    }
}
