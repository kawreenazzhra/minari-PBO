package com.minari.ecommerce.controller;

import com.minari.ecommerce.dto.CartSessionItem;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.service.ProductService;
import com.minari.ecommerce.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CartApiController {

    private final ProductService productService;
    private final ShoppingCartService cartService;
    private static final String GUEST_CART_SESSION_KEY = "guestCart";

    public CartApiController(ProductService productService, ShoppingCartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    // === GUEST CART API ===

    @PostMapping("/guest/cart")
    public ResponseEntity<Map<String, Object>> addToGuestCart(@RequestBody Map<String, Object> payload, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long productId = Long.valueOf(payload.get("product_id").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            Optional<Product> productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Product not found");
                return ResponseEntity.badRequest().body(response);
            }

            Product p = productOpt.get();
            // Get or create guest cart
            List<CartSessionItem> guestCart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
            if (guestCart == null) {
                guestCart = new ArrayList<>();
                session.setAttribute(GUEST_CART_SESSION_KEY, guestCart);
            }

            // Check if item exists
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
                        p.getImageUrl()
                );
                guestCart.add(newItem);
            }

            response.put("success", true);
            response.put("cart_count", guestCart.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // === USER CART API ===

    @PostMapping("/cart")
    public ResponseEntity<Map<String, Object>> addToUserCart(@RequestBody Map<String, Object> payload, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Long productId = Long.valueOf(payload.get("product_id").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            cartService.addToCart(principal.getName(), productId, quantity);

            response.put("success", true);
            // Ideally should return actual count from DB, for now returning plain success
            response.put("cart_count", cartService.getCartForUser(principal.getName()).getItems().size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // === UPDATE / DELETE APIs (Matching cart.js logic) ===

    @PatchMapping({"/guest-cart/{id}", "/cart/{id}"})
    public ResponseEntity<Map<String, Object>> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> payload, Principal principal, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int quantity = payload.get("quantity");

        if (principal == null) {
            // Guest Update
            List<CartSessionItem> guestCart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
            if (guestCart != null) {
                guestCart.stream()
                        .filter(item -> item.getProductId().equals(id))
                        .findFirst()
                        .ifPresent(item -> item.setQuantity(quantity));
            }
        } else {
            // User Update
            cartService.updateCartItemQuantity(principal.getName(), id, quantity);
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping({"/guest-cart/{id}", "/cart/{id}"})
    public ResponseEntity<Map<String, Object>> removeItem(@PathVariable Long id, Principal principal, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            // Guest Remove
            List<CartSessionItem> guestCart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
            if (guestCart != null) {
                guestCart.removeIf(item -> item.getProductId().equals(id));
            }
        } else {
            // User Remove
            cartService.removeFromCart(principal.getName(), id);
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
