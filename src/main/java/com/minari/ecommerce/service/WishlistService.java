package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.RegisteredCustomer;
import com.minari.ecommerce.entity.WishlistItem;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<WishlistItem> getWishlistByCustomer(Long customerId) {
        return wishlistRepository.findByCustomerId(customerId);
    }

    public boolean addToWishlist(Long customerId, Long productId) {
        if (wishlistRepository.existsByCustomerIdAndProductId(customerId, productId)) {
            return false; // Already exists
        }

        RegisteredCustomer customer = (RegisteredCustomer) userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        WishlistItem item = new WishlistItem(customer, product);
        wishlistRepository.save(item);
        
        // Update denormalized count if exists
        updateWishlistCount(customer);
        
        return true;
    }

    public void removeFromWishlist(Long customerId, Long productId) {
        wishlistRepository.deleteByCustomerIdAndProductId(customerId, productId);
        
        userRepository.findById(customerId).ifPresent(u -> {
            if (u instanceof RegisteredCustomer) {
                updateWishlistCount((RegisteredCustomer) u);
            }
        });
    }

    public boolean isProductInWishlist(Long customerId, Long productId) {
        return wishlistRepository.existsByCustomerIdAndProductId(customerId, productId);
    }

    public Integer countWishlistItems(Long customerId) {
        return wishlistRepository.countByCustomerId(customerId);
    }
    
    private void updateWishlistCount(RegisteredCustomer customer) {
        // Assuming there might be a field for this, otherwise skip
        // customer.setWishlistCount(wishlistRepository.countByCustomerId(customer.getId()));
        // userRepository.save(customer);
    }
}
