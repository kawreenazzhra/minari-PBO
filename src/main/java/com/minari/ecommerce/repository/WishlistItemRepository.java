package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    /**
     * Find all wishlist items for a customer
     */
    List<WishlistItem> findByCustomer(Customer customer);

    /**
     * Find all wishlist items for a customer, ordered by recent first
     */
    @Query("SELECT w FROM WishlistItem w WHERE w.customer = :customer ORDER BY w.addedAt DESC")
    List<WishlistItem> findByCustomerOrderByAddedAtDesc(Customer customer);

    /**
     * Check if product exists in customer's wishlist
     */
    boolean existsByCustomerAndProduct(Customer customer, Product product);

    /**
     * Find wishlist item by customer and product
     */
    Optional<WishlistItem> findByCustomerAndProduct(Customer customer, Product product);

    /**
     * Delete all wishlist items for a customer
     */
    void deleteByCustomer(Customer customer);

    /**
     * Count wishlist items for a customer
     */
    long countByCustomer(Customer customer);
}
