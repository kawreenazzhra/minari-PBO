package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByCustomerId(Long customerId);
    
    Optional<WishlistItem> findByCustomerIdAndProductId(Long customerId, Long productId);
    
    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);
    
    void deleteByCustomerIdAndProductId(Long customerId, Long productId);
    
    Integer countByCustomerId(Long customerId);
}
