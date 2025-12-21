package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderNumberContainingIgnoreCase(String orderNumber);
    
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.customer " +
           "LEFT JOIN FETCH o.payment " +
           "LEFT JOIN FETCH o.shipment " +
           "LEFT JOIN FETCH o.shippingAddress " +
           "LEFT JOIN FETCH o.items " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.customer " +
           "LEFT JOIN FETCH o.payment " +
           "LEFT JOIN FETCH o.shipment " +
           "LEFT JOIN FETCH o.shippingAddress " +
           "LEFT JOIN FETCH o.items " +
           "WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithDetails(@Param("orderNumber") String orderNumber);
}