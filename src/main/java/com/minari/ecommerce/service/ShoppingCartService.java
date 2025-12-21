package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.*;
import com.minari.ecommerce.repository.ShoppingCartRepository;
import com.minari.ecommerce.repository.CartItemRepository;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartService {
    
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public ShoppingCartService(ShoppingCartRepository cartRepository,
                             CartItemRepository cartItemRepository,
                             ProductRepository productRepository,
                             UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    
    public ShoppingCart getCartForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> createNewCart((Customer) user));
    }
    
    public void addToCart(String email, Long productId, int quantity) {
        ShoppingCart cart = getCartForUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Validasi dari class asli
        if (!product.isInStock()) {
            throw new RuntimeException("Product is out of stock");
        }
        
        if (!product.canFulfillOrder(quantity)) {
            throw new RuntimeException("Not enough stock available");
        }
        
        // Cek apakah item sudah ada di cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // Add new item
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
        
        cartRepository.save(cart);
    }
    
    public void removeFromCart(String email, Long productId) {
        ShoppingCart cart = getCartForUser(email);
        
        boolean removed = cart.getItems().removeIf(item -> 
            item.getProduct().getId().equals(productId));
        
        if (removed) {
            cartRepository.save(cart);
        }
    }
    
    public void updateCartItemQuantity(String email, Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(email, productId);
            return;
        }
        
        ShoppingCart cart = getCartForUser(email);
        
        cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .ifPresent(item -> {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            });
        
        cartRepository.save(cart);
    }

    /**
     * Get filtered cart with only selected product IDs
     */
    public ShoppingCart getFilteredCart(String email, java.util.List<Long> selectedProductIds) {
        ShoppingCart cart = getCartForUser(email);
        
        if (selectedProductIds == null || selectedProductIds.isEmpty()) {
            return cart;
        }
        
        // Create a new cart with filtered items
        ShoppingCart filteredCart = new ShoppingCart();
        filteredCart.setCustomer(cart.getCustomer());
        filteredCart.setId(cart.getId());
        
        // Filter items by selected product IDs
        java.util.List<CartItem> filteredItems = cart.getItems().stream()
            .filter(item -> selectedProductIds.contains(item.getProduct().getId()))
            .collect(java.util.stream.Collectors.toList());
        
        filteredCart.setItems(filteredItems);
        
        return filteredCart;
    }
    
    public void clearCart(String email) {
        ShoppingCart cart = getCartForUser(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    
    /**
     * Remove only specific items from cart (used when checking out selected items only)
     * Keeps unselected items in the cart
     */
    public void removeItemsByProductIds(String email, java.util.List<Long> productIds) {
        ShoppingCart cart = getCartForUser(email);
        
        // Remove items that are in the productIds list
        cart.getItems().removeIf(item -> productIds.contains(item.getProduct().getId()));
        
        cartRepository.save(cart);
    }
    
    private ShoppingCart createNewCart(User user) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setCustomer((Customer) user);
        return cartRepository.save(newCart);
    }
}