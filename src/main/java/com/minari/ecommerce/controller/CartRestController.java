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
public class CartRestController {

    private final ShoppingCartService cartService;
    private final ProductService productService;
    private static final String GUEST_CART_SESSION_KEY = "guestCart";

    public CartRestController(ShoppingCartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    // Unified endpoint for Add to Cart (Guest & User)
    @PostMapping({"/cart", "/guest/cart"})
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload, Principal principal, HttpSession session) {
        System.out.println("DEBUG: Entering addToCart. Payload: " + payload);
        try {
            Long productId = Long.valueOf(payload.get("product_id").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            if (principal != null) {
                System.out.println("DEBUG: Adding to User Cart. User: " + principal.getName());
                cartService.addToCart(principal.getName(), productId, quantity);
            } else {
                System.out.println("DEBUG: Adding to Guest Session Cart. Session ID: " + session.getId());
                addToSessionCart(session, productId, quantity);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            // In a real app, calculate actual count. For now return 0 or mock to avoid error
            int count = getCartCount(principal, session);
            System.out.println("DEBUG: Cart count updated to: " + count);
            response.put("cart_count", count); 
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("DEBUG: Error in addToCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Update Quantity
    @PatchMapping({"/cart/{productId}", "/guest/cart/{productId}"})
    public ResponseEntity<?> updateQuantity(@PathVariable Long productId, @RequestBody Map<String, Object> payload, Principal principal, HttpSession session) {
        try {
            int quantity = Integer.parseInt(payload.get("quantity").toString());
            
            if (principal != null) {
                cartService.updateCartItemQuantity(principal.getName(), productId, quantity);
            } else {
                updateSessionCart(session, productId, quantity);
            }

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Remove Item
    @DeleteMapping({"/cart/{productId}", "/guest/cart/{productId}"})
    public ResponseEntity<?> removeItem(@PathVariable Long productId, Principal principal, HttpSession session) {
        try {
            if (principal != null) {
                cartService.removeFromCart(principal.getName(), productId);
            } else {
                removeFromSessionCart(session, productId);
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // --- Helpers for Guest Session ---

    private void addToSessionCart(HttpSession session, Long productId, int quantity) {
        List<CartSessionItem> guestCart = getSessionCart(session);
        Product p = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartSessionItem> existing = guestCart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            guestCart.add(new CartSessionItem(
                    p.getId(), p.getName(), p.getPrice(), "One Size", "Default", quantity, p.getImageUrl()
            ));
        }
    }

    private void updateSessionCart(HttpSession session, Long productId, int quantity) {
        List<CartSessionItem> guestCart = getSessionCart(session);
        if (quantity <= 0) {
            removeFromSessionCart(session, productId);
        } else {
            guestCart.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));
        }
    }

    private void removeFromSessionCart(HttpSession session, Long productId) {
        List<CartSessionItem> guestCart = getSessionCart(session);
        guestCart.removeIf(item -> item.getProductId().equals(productId));
    }

    private List<CartSessionItem> getSessionCart(HttpSession session) {
        List<CartSessionItem> cart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(GUEST_CART_SESSION_KEY, cart);
        }
        return cart;
    }
    
    @GetMapping("/cart/count")
    public ResponseEntity<?> cartCountApi(Principal principal, HttpSession session) {
        return ResponseEntity.ok(Map.of("count", getCartCount(principal, session)));
    }

    private int getCartCount(Principal principal, HttpSession session) {
        if (principal != null) {
             com.minari.ecommerce.entity.ShoppingCart userCart = cartService.getCartForUser(principal.getName());
             return (userCart != null && userCart.getItems() != null) ? userCart.getItems().size() : 0;
        } else {
             List<CartSessionItem> cart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
             return cart != null ? cart.size() : 0;
        }
    }
}
