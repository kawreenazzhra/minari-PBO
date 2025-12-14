package com.minari.ecommerce.controller;

import com.minari.ecommerce.dto.OrderDTO;
import com.minari.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderController API
 * ==================
 * REST endpoints for order operations
 * Provides complete CRUD operations and reporting
 * 
 * Base URL: /api/orders
 * 
 * Part of Phase 5: Backend API Integration
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    private final OrderService orderService;

    /**
     * GET /api/orders
     * Retrieve all orders with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus) {
        
        try {
            log.info("Fetching orders - page: {}, size: {}, status: {}, paymentStatus: {}", 
                    page, size, status, paymentStatus);

            // This will be implemented with proper pagination
            List<OrderDTO> orders = orderService.getAllOrders(status, paymentStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orders);
            response.put("total", orders.size());
            response.put("page", page);
            response.put("size", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/{orderNumber}
     * Retrieve specific order by order number
     */
    @GetMapping("/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getOrderByNumber(
            @PathVariable String orderNumber) {
        
        try {
            log.info("Fetching order: {}", orderNumber);
            
            OrderDTO order = orderService.getOrderByNumber(orderNumber);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "success", false,
                            "error", "Order not found: " + orderNumber
                        ));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", order
            ));
        } catch (Exception e) {
            log.error("Error fetching order: {}", orderNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/customer/{customerId}
     * Retrieve all orders for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerOrders(
            @PathVariable String customerId) {
        
        try {
            log.info("Fetching orders for customer: {}", customerId);
            
            List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customerId", customerId);
            response.put("data", orders);
            response.put("total", orders.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching customer orders: {}", customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders
     * Create new order
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody OrderDTO orderDTO) {
        
        try {
            log.info("Creating new order for customer: {}", orderDTO.getCustomerId());
            
            // Validate order data
            if (!validateOrderData(orderDTO)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                            "success", false,
                            "error", "Invalid order data"
                        ));
            }

            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "success", true,
                        "message", "Order created successfully",
                        "data", createdOrder
                    ));
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/orders/{orderNumber}/status
     * Update order status
     */
    @PatchMapping("/{orderNumber}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestBody Map<String, String> request) {
        
        try {
            String newStatus = request.get("status");
            log.info("Updating order {} status to {}", orderNumber, newStatus);
            
            if (newStatus == null || newStatus.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "error", "Status is required"));
            }

            OrderDTO updatedOrder = orderService.updateOrderStatus(orderNumber, newStatus);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order status updated",
                "data", updatedOrder
            ));
        } catch (Exception e) {
            log.error("Error updating order status: {}", orderNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/orders/{orderNumber}/payment-status
     * Update payment status
     */
    @PatchMapping("/{orderNumber}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable String orderNumber,
            @RequestBody Map<String, String> request) {
        
        try {
            String newStatus = request.get("paymentStatus");
            log.info("Updating order {} payment status to {}", orderNumber, newStatus);
            
            if (newStatus == null || newStatus.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "error", "Payment status is required"));
            }

            OrderDTO updatedOrder = orderService.updatePaymentStatus(orderNumber, newStatus);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment status updated",
                "data", updatedOrder
            ));
        } catch (Exception e) {
            log.error("Error updating payment status: {}", orderNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/orders/{orderNumber}
     * Cancel order
     */
    @DeleteMapping("/{orderNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable String orderNumber) {
        
        try {
            log.info("Cancelling order: {}", orderNumber);
            
            orderService.cancelOrder(orderNumber);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order cancelled successfully"
            ));
        } catch (Exception e) {
            log.error("Error cancelling order: {}", orderNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/stats/summary
     * Get order statistics summary
     */
    @GetMapping("/stats/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        
        try {
            log.info("Fetching order statistics");
            
            Map<String, Object> stats = orderService.getOrderStatistics();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
        } catch (Exception e) {
            log.error("Error fetching statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/stats/by-date
     * Get statistics by date range
     */
    @GetMapping("/stats/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            log.info("Fetching stats for date range: {} to {}", startDate, endDate);
            
            Map<String, Object> stats = orderService.getStatsByDateRange(startDate, endDate);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
        } catch (Exception e) {
            log.error("Error fetching statistics by date", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/stats/by-status
     * Get order count by status
     */
    @GetMapping("/stats/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatsByStatus() {
        
        try {
            log.info("Fetching order statistics by status");
            
            Map<String, Object> stats = orderService.getOrderCountByStatus();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
        } catch (Exception e) {
            log.error("Error fetching statistics by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/top-customers
     * Get top customers by total spent
     */
    @GetMapping("/top-customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTopCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            log.info("Fetching top {} customers", limit);
            
            List<Map<String, Object>> topCustomers = orderService.getTopCustomers(limit);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", topCustomers,
                "count", topCustomers.size()
            ));
        } catch (Exception e) {
            log.error("Error fetching top customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/search
     * Search orders by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchOrders(
            @RequestParam String query) {
        
        try {
            log.info("Searching orders with query: {}", query);
            
            List<OrderDTO> results = orderService.searchOrders(query);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "query", query,
                "data", results,
                "count", results.size()
            ));
        } catch (Exception e) {
            log.error("Error searching orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Validate order data
     */
    private boolean validateOrderData(OrderDTO orderDTO) {
        return orderDTO != null &&
                orderDTO.getOrderNumber() != null && !orderDTO.getOrderNumber().isBlank() &&
                orderDTO.getCustomerId() != null &&
                orderDTO.getTotalAmount() != null && orderDTO.getTotalAmount() > 0 &&
                orderDTO.getOrderDate() != null;
    }

    /**
     * Global error handler for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(IllegalArgumentException e) {
        log.error("Validation error", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage()
                ));
    }
}
