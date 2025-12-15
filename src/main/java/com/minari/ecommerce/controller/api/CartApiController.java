package com.minari.ecommerce.controller.api;

import com.minari.ecommerce.dto.CartSessionItem;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.service.ProductService;
import com.minari.ecommerce.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CartApiController {

    private final ShoppingCartService cartService;
    private final ProductService productService;
    private static final String GUEST_CART_SESSION_KEY = "guestCart";

    public CartApiController(ShoppingCartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    // ==========================================
    // GUEST CART API
    // ==========================================

    @PostMapping("/guest/cart")
    public ResponseEntity<?> addToGuestCart(@RequestBody Map<String, Object> payload, HttpSession session) {
        try {
            Long productId = Long.valueOf(payload.get("product_id").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());
            String name = (String) payload.get("name");
            String image = (String) payload.get("image");

            // Validate product
            Optional<Product> productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Product not found"));
            }
            Product p = productOpt.get();

            List<CartSessionItem> guestCart = getGuestCart(session);

            // Check if exists
            boolean found = false;
            for (CartSessionItem item : guestCart) {
                if (item.getProductId().equals(productId)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            if (!found) {
                CartSessionItem newItem = new CartSessionItem(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        "One Size",
                        "Default",
                        quantity,
                        image != null && !image.isEmpty() ? image : p.getImageUrl());
                guestCart.add(newItem);
            }

            session.setAttribute(GUEST_CART_SESSION_KEY, guestCart);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Item added to cart",
                    "cart_count", guestCart.size()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PatchMapping("/guest-cart/{id}")
    public ResponseEntity<?> updateGuestCart(@PathVariable Long id, @RequestBody Map<String, Integer> payload,
            HttpSession session) {
        List<CartSessionItem> guestCart = getGuestCart(session);
        int quantity = payload.getOrDefault("quantity", 1);

        for (CartSessionItem item : guestCart) {
            if (item.getProductId().equals(id)) {
                item.setQuantity(quantity);
                break;
            }
        }
        session.setAttribute(GUEST_CART_SESSION_KEY, guestCart);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/guest-cart/{id}")
    public ResponseEntity<?> removeGuestCart(@PathVariable Long id, HttpSession session) {
        List<CartSessionItem> guestCart = getGuestCart(session);
        guestCart.removeIf(item -> item.getProductId().equals(id));
        session.setAttribute(GUEST_CART_SESSION_KEY, guestCart);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @SuppressWarnings("unchecked")
    private List<CartSessionItem> getGuestCart(HttpSession session) {
        List<CartSessionItem> cart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }

    // ==========================================
    // USER CART API
    // ==========================================

    @PostMapping("/cart")
    public ResponseEntity<?> addToUserCart(@RequestBody Map<String, Object> payload, Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));

        try {
            Long productId = Long.valueOf(payload.get("product_id").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            cartService.addToCart(principal.getName(), productId, quantity);

            int count = cartService.getCartForUser(principal.getName()).getItems().size();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Item added to cart",
                    "cart_count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PatchMapping("/cart/{id}")
    public ResponseEntity<?> updateUserCart(@PathVariable Long id, @RequestBody Map<String, Integer> payload,
            Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).build();

        int quantity = payload.getOrDefault("quantity", 1);
        try {
            // ID here is treated as Product ID to match guest and service logic
            cartService.updateCartItemQuantity(principal.getName(), id, quantity);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{id}")
    public ResponseEntity<?> removeUserCart(@PathVariable Long id, Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).build();
        try {
            cartService.removeFromCart(principal.getName(), id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
