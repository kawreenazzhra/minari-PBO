package com.minari.ecommerce.controller.api;

import com.minari.ecommerce.entity.WishlistItem;
import com.minari.ecommerce.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WishlistRestController {

    private static final Logger log = LoggerFactory.getLogger(WishlistRestController.class);

    private final WishlistService wishlistService;

    public WishlistRestController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * GET /api/wishlist - Get all wishlist items for authenticated user
     */
    @GetMapping
    public ResponseEntity<?> getWishlist(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Not authenticated"));
        }

        try {
            String email = authentication.getName();
            List<WishlistItem> items = wishlistService.getWishlistForCustomer(email);
            
            // Transform to DTO for frontend - matching whislist.js expectations
            List<Map<String, Object>> itemDtos = items.stream()
                    .map(item -> {
                        Map<String, Object> dto = new HashMap<>();
                        // Product data
                        Map<String, Object> productMap = new HashMap<>();
                        productMap.put("id", item.getProduct().getId());
                        productMap.put("name", item.getProduct().getName());
                        productMap.put("price", item.getProduct().getPrice());
                        productMap.put("image_url", item.getProduct().getImageUrl());
                        
                        // Wishlist item data
                        dto.put("id", item.getId());
                        dto.put("wishlistId", item.getId());
                        dto.put("product", productMap);
                        dto.put("product_id", item.getProduct().getId());
                        dto.put("product_name", item.getProduct().getName());
                        dto.put("price", item.getProduct().getPrice());
                        dto.put("image_url", item.getProduct().getImageUrl());
                        dto.put("addedAt", item.getAddedAt());
                        return dto;
                    })
                    .toList();

            return ResponseEntity.ok(itemDtos);
        } catch (Exception e) {
            log.error("Error fetching wishlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching wishlist"));
        }
    }

    /**
     * POST /api/wishlist - Add product to wishlist
     */
    @PostMapping
    public ResponseEntity<?> addToWishlist(Authentication authentication,
                                          @RequestBody Map<String, Object> payload) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Not authenticated"));
        }

        try {
            String email = authentication.getName();
            Long productId = ((Number) payload.get("product_id")).longValue();

            WishlistItem item = wishlistService.addToWishlist(email, productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Added to wishlist");
            response.put("id", item.getId());
            response.put("product_id", item.getProduct().getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding to wishlist", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/wishlist/{id} - Remove item from wishlist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromWishlist(Authentication authentication,
                                               @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Not authenticated"));
        }

        try {
            wishlistService.removeWishlistItem(id);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Removed from wishlist"
            ));
        } catch (Exception e) {
            log.error("Error removing from wishlist", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/wishlist - Clear entire wishlist
     */
    @DeleteMapping
    public ResponseEntity<?> clearWishlist(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Not authenticated"));
        }

        try {
            String email = authentication.getName();
            wishlistService.clearWishlist(email);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Wishlist cleared"
            ));
        } catch (Exception e) {
            log.error("Error clearing wishlist", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * GET /api/wishlist/count - Get wishlist count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getWishlistCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Not authenticated"));
        }

        try {
            String email = authentication.getName();
            long count = wishlistService.getWishlistCount(email);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", count
            ));
        } catch (Exception e) {
            log.error("Error getting wishlist count", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * GET /api/wishlist/check/{productId} - Check if product is in wishlist
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkIfInWishlist(Authentication authentication,
                                              @PathVariable Long productId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Not authenticated"));
        }

        try {
            String email = authentication.getName();
            boolean isInWishlist = wishlistService.isInWishlist(email, productId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "isInWishlist", isInWishlist
            ));
        } catch (Exception e) {
            log.error("Error checking wishlist", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
