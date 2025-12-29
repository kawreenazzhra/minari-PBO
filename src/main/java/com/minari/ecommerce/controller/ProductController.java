package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final com.minari.ecommerce.repository.ProductReviewRepository reviewRepository;

    public ProductController(ProductService productService, com.minari.ecommerce.repository.ProductReviewRepository reviewRepository) {
        this.productService = productService;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public String listProducts(@RequestParam(required = false) String search,
            @RequestParam(required = false) Long category,
            Model model) {
        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("searchQuery", search);
        } else if (category != null) {
            products = productService.getProductsByCategory(category);
            productService.getCategoryById(category)
                    .ifPresent(c -> {
                        model.addAttribute("selectedCategory", c.getName());
                        model.addAttribute("categoryObj", c);
                    });
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());

        // Create simplified category objects for navbar to avoid JSON
        // recursion/overhead
        List<java.util.Map<String, Object>> navCategories = productService.getAllCategories().stream()
                .filter(cat -> cat.getIsActive() != null && cat.getIsActive())
                .map(cat -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", cat.getId());
                    map.put("name", cat.getName());
                    map.put("imageUrl", cat.getImageUrl());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("navCategories", navCategories);

        model.addAttribute("pageTitle", "Products - Minari");

        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isEmpty()) {
            return "redirect:/products";
        }

        Product p = product.get();
        model.addAttribute("product", p);
        model.addAttribute("pageTitle", p.getName() + " - Minari");

        // Fetch reviews
        List<com.minari.ecommerce.entity.ProductReview> reviews = reviewRepository.findByProductId(p.getId());
        model.addAttribute("reviews", reviews);

        // Calculate average rating if needed or let frontend handle it
        double avgRating = reviews.stream()
                .mapToInt(com.minari.ecommerce.entity.ProductReview::getRating)
                .average()
                .orElse(0.0);
        model.addAttribute("averageRating", avgRating);
        model.addAttribute("reviewCount", reviews.size());

        return "products/detail";
    }
}