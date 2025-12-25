package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    private final OrderRepository orderRepository;

    public AdminNotificationController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Get pending order notifications (orders that are in PENDING status)
     */
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getPendingOrders() {
        // Get all pending orders
        List<Order> pendingOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == com.minari.ecommerce.entity.OrderStatus.PENDING)
                .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate())) // Most recent first
                .limit(10) // Limit to 10 most recent
                .collect(Collectors.toList());

        // Convert to notification format
        List<Map<String, Object>> notifications = pendingOrders.stream()
                .map(order -> {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("id", order.getId());
                    notification.put("orderNumber", order.getOrderNumber());
                    notification.put("customerName", order.getCustomer() != null ? 
                        order.getCustomer().getFullName() : 
                        (order.getUser() != null ? order.getUser().getFullName() : "Guest"));
                    notification.put("totalAmount", order.getTotalAmount());
                    notification.put("orderDate", order.getOrderDate().toString());
                    notification.put("status", order.getStatus().toString());
                    return notification;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("count", notifications.size());
        response.put("notifications", notifications);

        return ResponseEntity.ok(response);
    }

    /**
     * Get notification count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getNotificationCount() {
        long count = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == com.minari.ecommerce.entity.OrderStatus.PENDING)
                .count();

        Map<String, Object> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}
