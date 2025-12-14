package com.minari.ecommerce.controller;

import com.minari.ecommerce.dto.CartSessionItem;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.service.ShoppingCartService;
import com.minari.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    private final ShoppingCartService cartService;
    private final ProductService productService;
    private static final String GUEST_CART_SESSION_KEY = "guestCart";
    
    public CartController(ShoppingCartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }
    
    @GetMapping
    public String viewCart(Model model, Principal principal, HttpSession session) {
        if (principal != null) {
            // Authenticated user - use database cart
            model.addAttribute("cart", cartService.getCartForUser(principal.getName()));
            model.addAttribute("isGuest", false);
        } else {
            // Guest user - use session cart
            List<CartSessionItem> guestCart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
            if (guestCart == null) {
                guestCart = new ArrayList<>();
            }
            model.addAttribute("cart", new CartSessionData(guestCart));
            model.addAttribute("isGuest", true);
        }
        return "cart/view";
    }
    
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") int quantity,
                           Principal principal,
                           HttpSession session) {
        if (principal != null) {
            // Authenticated user - use database cart
            cartService.addToCart(principal.getName(), productId, quantity);
        } else {
            // Guest user - use session cart
            Optional<Product> product = productService.getProductById(productId);
            if (product.isPresent()) {
                Product p = product.get();
                CartSessionItem item = new CartSessionItem(
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    "One Size",  // Simplified - no variant selection
                    "Default",   // Simplified - no color selection
                    quantity,
                    p.getImageUrl()
                );
                
                // Add to session cart
                List<CartSessionItem> guestCart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
                if (guestCart == null) {
                    guestCart = new ArrayList<>();
                    session.setAttribute(GUEST_CART_SESSION_KEY, guestCart);
                }
                
                // Check if item already in cart
                boolean found = false;
                for (CartSessionItem cartItem : guestCart) {
                    if (cartItem.getProductId().equals(productId)) {
                        cartItem.setQuantity(cartItem.getQuantity() + quantity);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    guestCart.add(item);
                }
            }
        }
        return "redirect:/cart";
    }
    
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, 
                                Principal principal,
                                HttpSession session) {
        if (principal != null) {
            // Authenticated user
            cartService.removeFromCart(principal.getName(), productId);
        } else {
            // Guest user - remove from session cart
            List<CartSessionItem> guestCart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
            if (guestCart != null) {
                guestCart.removeIf(item -> item.getProductId().equals(productId));
            }
        }
        return "redirect:/cart";
    }
    
    // Helper class to standardize cart display for both guest and user carts
    public static class CartSessionData {
        private List<CartSessionItem> items;
        
        public CartSessionData(List<CartSessionItem> items) {
            this.items = items;
        }
        
        public List<CartSessionItem> getItems() {
            return items;
        }
        
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }
        
        public double getTotal() {
            if (items == null || items.isEmpty()) return 0;
            return items.stream().mapToDouble(CartSessionItem::getSubtotal).sum();
        }
    }
}