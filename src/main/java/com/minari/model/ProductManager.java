package com.minari.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class ProductManager {
     private List<Product> products;
    private List<Category> categories;
    private int nextProductId;
    private int nextCategoryId;
    
    // Constructors
    public ProductManager() {
        this.products = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.nextProductId = 1;
        this.nextCategoryId = 1;
    }
    
    // Product Management Methods
    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        // Generate ID if not provided
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId("PROD_" + nextProductId++);
        }
        
        // Check for duplicate ID
        if (findProductById(product.getId()) != null) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " already exists");
        }
        
        products.add(product);
        System.out.println("Product added successfully: " + product.getName());
        return product;
    }
    
    public Product findProductById(String productId) {
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Product> findProductsByCategory(String categoryName) {
        return products.stream()
                .filter(product -> product.getCategory() != null && 
                                  product.getCategory().name().equalsIgnoreCase(categoryName))
                .collect(Collectors.toList());
    }
    
    public List<Product> findProductsByName(String name) {
        return products.stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
    
    public List<Product> getProductsInStock() {
        return products.stream()
                .filter(Product::isInStock)
                .collect(Collectors.toList());
    }
    
    public Product updateProduct(String productId, Product updatedProduct) {
        Product existingProduct = findProductById(productId);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
        
        // Update fields
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setSize(updatedProduct.getSize());
        existingProduct.setColor(updatedProduct.getColor());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        
        System.out.println("Product updated successfully: " + existingProduct.getName());
        return existingProduct;
    }
    
    public boolean deleteProduct(String productId) {
        Product product = findProductById(productId);
        if (product != null) {
            products.remove(product);
            System.out.println("Product deleted successfully: " + productId);
            return true;
        }
        System.out.println("Product not found: " + productId);
        return false;
    }
    
    public void reduceStock(String productId, Integer quantity) {
        Product product = findProductById(productId);
        if (product != null) {
            product.reduceStock(quantity);
            System.out.println("Stock reduced for " + product.getName() + ". New stock: " + product.getStock());
        } else {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
    }
    
    public void increaseStock(String productId, Integer quantity) {
        Product product = findProductById(productId);
        if (product != null) {
            product.increaseStock(quantity);
            System.out.println("Stock increased for " + product.getName() + ". New stock: " + product.getStock());
        } else {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
    }
    
    public int getTotalProducts() {
        return products.size();
    }
    
    // Category Management Methods
    public Category addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        // Generate ID if not provided
        if (category.getId() == null ) {
            category.setId("CAT_" + nextCategoryId++);
        }
        
        // Check for duplicate name
        if (findCategoryByName(category.getName()) != null) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        
        categories.add(category);
        System.out.println("Category added successfully: " + category.getName());
        return category;
    }
    
    public Category findCategoryById(String categoryId) {
        return categories.stream()
                .filter(category -> category.getId().equals(categoryId))
                .findFirst()
                .orElse(null);
    }
    
    public Category findCategoryByName(String categoryName) {
        return categories.stream()
                .filter(category -> category.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElse(null);
    }
    
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        return products.stream()
                .filter(product -> product.getStock() != null && product.getStock() <= threshold)
                .collect(Collectors.toList());
    }
    
    public void displayAllProducts() {
        System.out.println("=== ALL PRODUCTS ===");
        if (products.isEmpty()) {
            System.out.println("No products available.");
        } else {
            for (Product product : products) {
                System.out.println("- " + product.getName() + " | Stock: " + product.getStock() + " | Price: $" + product.getPrice());
            }
        }
        System.out.println("Total: " + getTotalProducts() + " products");
    }
    
    public void displayAllCategories() {
        System.out.println("=== ALL CATEGORIES ===");
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
        } else {
            for (Category category : categories) {
                System.out.println("- " + category.getName() + " (" + category.getProducts().size() + " products)");
            }
        }
    }
}
