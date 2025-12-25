package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.RegisteredCustomer;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.entity.WishlistItem;
import com.minari.ecommerce.service.UserService;
import com.minari.ecommerce.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistRestController {

    private final WishlistService wishlistService;
    private final com.minari.ecommerce.repository.UserRepository userRepository;

    public WishlistRestController(WishlistService wishlistService, com.minari.ecommerce.repository.UserRepository userRepository) {
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
    }

    private RegisteredCustomer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user instanceof RegisteredCustomer) {
            return (RegisteredCustomer) user;
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getWishlist() {
        RegisteredCustomer customer = getCurrentCustomer();
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        List<WishlistItem> items = wishlistService.getWishlistByCustomer(customer.getId());
        
        // Transform to simplified DTO to avoid recursion/lazy loading issues
        List<Map<String, Object>> response = items.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("productId", item.getProduct().getId());
            map.put("productName", item.getProduct().getName());
            map.put("productImage", item.getProduct().getImageUrl());
            map.put("productPrice", item.getProduct().getPrice());
            // Add product object as nested for JS compatibility if needed
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", item.getProduct().getId());
            productMap.put("name", item.getProduct().getName());
            productMap.put("imageUrl", item.getProduct().getImageUrl());
            productMap.put("price", item.getProduct().getPrice());
            productMap.put("stockQuantity", item.getProduct().getStockQuantity()); // fixed prop name
            map.put("product", productMap);
            
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> addToWishlist(@RequestBody WishlistRequest request) {
        RegisteredCustomer customer = getCurrentCustomer();
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Please login first"));
        }

        try {
            boolean added = wishlistService.addToWishlist(customer.getId(), request.getProduct_id());
            int count = wishlistService.countWishlistItems(customer.getId());
            if (added) {
                 return ResponseEntity.ok(Map.of("success", true, "message", "Product added to wishlist", "wishlist_count", count));
            } else {
                 return ResponseEntity.ok(Map.of("success", false, "message", "Product already in wishlist", "wishlist_count", count));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId) {
        RegisteredCustomer customer = getCurrentCustomer();
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        try {
            wishlistService.removeFromWishlist(customer.getId(), productId);
            int count = wishlistService.countWishlistItems(customer.getId());
            return ResponseEntity.ok(Map.of("success", true, "message", "Product removed from wishlist", "wishlist_count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getWishlistCount() {
        RegisteredCustomer customer = getCurrentCustomer();
        if (customer == null) {
            return ResponseEntity.ok(Map.of("count", 0));
        }
        return ResponseEntity.ok(Map.of("count", wishlistService.countWishlistItems(customer.getId())));
    }
    
    // DTO class
    public static class WishlistRequest {
        private Long product_id;
        
        public Long getProduct_id() { return product_id; }
        public void setProduct_id(Long product_id) { this.product_id = product_id; }
    }
}
