package com.minari.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String categoryId;
    private String id;
    private String name;
    private String description;
    private List<Product> products = new ArrayList<>();
    
    // Constructors
    public Category() {}
    
    public Category(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    // Business Methods
    public void addProduct(Product product) {
        if (!products.contains(product)) {
            products.add(product);
        }
    }
    
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }
    
    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<Product> getProducts() { return new ArrayList<>(products); }
    public void setProducts(List<Product> products) { this.products = products; }
}
