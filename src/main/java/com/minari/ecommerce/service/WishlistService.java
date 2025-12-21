package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.entity.WishlistItem;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.repository.WishlistItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WishlistService {

    private static final Logger log = LoggerFactory.getLogger(WishlistService.class);

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistItemRepository wishlistItemRepository, 
                          UserRepository userRepository,
                          ProductRepository productRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Get all wishlist items for a customer
     */
    public List<WishlistItem> getWishlistForCustomer(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        if (!(user instanceof Customer)) {
            throw new RuntimeException("User is not a customer");
        }

        Customer customer = (Customer) user;
        return wishlistItemRepository.findByCustomerOrderByAddedAtDesc(customer);
    }

    /**
     * Add product to wishlist
     */
    public WishlistItem addToWishlist(String email, Long productId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        if (!(user instanceof Customer)) {
            throw new RuntimeException("User is not a customer");
        }

        Customer customer = (Customer) user;
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Check if already in wishlist
        if (wishlistItemRepository.existsByCustomerAndProduct(customer, product)) {
            log.info("Product {} already in wishlist for customer {}", productId, email);
            return wishlistItemRepository.findByCustomerAndProduct(customer, product).orElse(null);
        }

        WishlistItem wishlistItem = new WishlistItem(customer, product);
        WishlistItem saved = wishlistItemRepository.save(wishlistItem);
        
        log.info("Added product {} to wishlist for customer {}", productId, email);
        return saved;
    }

    /**
     * Remove product from wishlist
     */
    public void removeFromWishlist(String email, Long productId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        if (!(user instanceof Customer)) {
            throw new RuntimeException("User is not a customer");
        }

        Customer customer = (Customer) user;
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        Optional<WishlistItem> wishlistItem = wishlistItemRepository.findByCustomerAndProduct(customer, product);
        if (wishlistItem.isPresent()) {
            wishlistItemRepository.delete(wishlistItem.get());
            log.info("Removed product {} from wishlist for customer {}", productId, email);
        }
    }

    /**
     * Remove wishlist item by ID
     */
    public void removeWishlistItem(Long id) {
        wishlistItemRepository.deleteById(id);
    }

    /**
     * Check if product is in customer's wishlist
     */
    public boolean isInWishlist(String email, Long productId) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null || !(user instanceof Customer)) {
            return false;
        }

        Customer customer = (Customer) user;
        Product product = productRepository.findById(productId).orElse(null);
        
        if (product == null) {
            return false;
        }

        return wishlistItemRepository.existsByCustomerAndProduct(customer, product);
    }

    /**
     * Get wishlist count for customer
     */
    public long getWishlistCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        if (!(user instanceof Customer)) {
            throw new RuntimeException("User is not a customer");
        }

        Customer customer = (Customer) user;
        return wishlistItemRepository.countByCustomer(customer);
    }

    /**
     * Clear entire wishlist for customer
     */
    public void clearWishlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        if (!(user instanceof Customer)) {
            throw new RuntimeException("User is not a customer");
        }

        Customer customer = (Customer) user;
        wishlistItemRepository.deleteByCustomer(customer);
        log.info("Cleared wishlist for customer {}", email);
    }
}
