package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity
 * Provides database operations for customer management
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by email
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find customer by full name
     */
    Optional<Customer> findByFullName(String fullName);

    /**
     * Find all active customers
     */
    List<Customer> findByIsActive(Boolean isActive);

    /**
     * Find all newsletter subscribers
     */
    List<Customer> findByNewsletterSubscribed(Boolean subscribed);

    /**
     * Find customers with minimum loyalty points
     */
    List<Customer> findByLoyaltyPointsGreaterThanEqual(Integer points);

    /**
     * Search customers by name or email with pagination
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "c.phone LIKE CONCAT('%', :keyword, '%')")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find customers created between dates
     */
    @Query("SELECT c FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Customer> findCustomersCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find customers by registration date range with pagination
     */
    @Query("SELECT c FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    Page<Customer> findNewCustomersByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Get total loyalty points for a customer
     */
    @Query("SELECT COALESCE(SUM(c.loyaltyPoints), 0) FROM Customer c WHERE c.id = :customerId")
    Integer getTotalLoyaltyPoints(@Param("customerId") Long customerId);

    /**
     * Find VIP customers (high loyalty points)
     */
    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints >= :minPoints ORDER BY c.loyaltyPoints DESC")
    Page<Customer> findVIPCustomers(@Param("minPoints") Integer minPoints, Pageable pageable);

    /**
     * Count customers by status
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.isActive = :status")
    Long countByStatus(@Param("status") Boolean status);

    /**
     * Find customers with saved addresses
     */
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN c.savedAddresses a WHERE SIZE(c.savedAddresses) > 0")
    Page<Customer> findCustomersWithSavedAddresses(Pageable pageable);

    /**
     * Find customers with active orders
     */
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN c.orders o WHERE o IS NOT NULL")
    Page<Customer> findCustomersWithOrders(Pageable pageable);

    /**
     * Find customers who haven't ordered in specific period
     */
    @Query("SELECT c FROM Customer c WHERE (SELECT MAX(o.orderDate) FROM Order o WHERE o.customer.id = c.id) < :date OR " +
           "(SELECT MAX(o.orderDate) FROM Order o WHERE o.customer.id = c.id) IS NULL")
    List<Customer> findInactiveCustomers(@Param("date") LocalDateTime date);

    /**
     * Get customer count by creation date
     */
    @Query(value = "SELECT COUNT(*) FROM customers WHERE DATE(created_at) = CURDATE()", nativeQuery = true)
    Long countNewCustomersToday();

    /**
     * Find customers in city
     */
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN c.shippingAddress a WHERE a.city = :city")
    List<Customer> findCustomersByCity(@Param("city") String city);

    /**
     * Find customers in country
     */
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN c.shippingAddress a WHERE a.country = :country")
    List<Customer> findCustomersByCountry(@Param("country") String country);

    /**
     * Find customers by phone number
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);

    /**
     * Get all customers with pagination
     */
    Page<Customer> findAll(Pageable pageable);

    /**
     * Find active customers with pagination
     */
    Page<Customer> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find customers ordered by loyalty points
     */
    @Query("SELECT c FROM Customer c ORDER BY c.loyaltyPoints DESC")
    List<Customer> findTopLoyaltyCustomers();

    /**
     * Find customers with recent activity
     */
    @Query("SELECT c FROM Customer c ORDER BY c.lastLogin DESC")
    Page<Customer> findRecentlyActiveCustomers(Pageable pageable);
}
