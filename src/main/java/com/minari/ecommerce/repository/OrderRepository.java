package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long userId);
    List<Order> findByUser_IdOrderByOrderDateDesc(Long userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderNumberContainingIgnoreCase(String orderNumber);
    
    // Optimized stats queries
    long countByStatus(OrderStatus status);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o")
    Double sumTotalRevenue();
    
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(org.springframework.data.domain.Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT p.name, p.imageUrl, SUM(oi.quantity) as totalSold FROM OrderItem oi JOIN oi.product p GROUP BY p.name, p.imageUrl ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts(org.springframework.data.domain.Pageable pageable);
}