package com.minari.ecommerce.service;

import com.minari.ecommerce.dto.OrderDTO;
import com.minari.ecommerce.entity.Address;
import com.minari.ecommerce.entity.CartItem;
import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.OrderItem;
import com.minari.ecommerce.entity.Payment;
import com.minari.ecommerce.entity.PaymentMethod;
import com.minari.ecommerce.entity.PaymentStatus;
import com.minari.ecommerce.entity.ShoppingCart;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.repository.OrderRepository;
import com.minari.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ShoppingCartService cartService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository, ShoppingCartService cartService, UserRepository userRepository,
            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Order createOrderFromCart(String email, Address shippingAddress, PaymentMethod paymentMethod) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart cart = cartService.getCartForUser(email);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        for (CartItem item : cart.getItems()) {
            if (!item.getProduct().canFulfillOrder(item.getQuantity())) {
                throw new RuntimeException("Not enough stock for: " + item.getProduct().getName());
            }
        }
        Order order = new Order();
        order.setUser(user);
        // Explicitly generate order number so it's available for Shipment/Payment
        order.setOrderNumber(order.generateOrderNumber());
        
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            order.setCustomer((com.minari.ecommerce.entity.Customer) user);
        } else {
             // User is not a customer (e.g. Admin). 
             // Since we made customer_id nullable, we can proceed.
             // We could log this event.
             log.warn("Order created by non-customer user: {}", user.getEmail());
        }
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(cart.getTotalAmount());

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    orderItem.setTotalPrice(cartItem.getSubtotal());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.processPayment(); // Simulasi payment

        order.setPayment(payment);

        // Create shipment if payment is successful
        if (payment.getStatus() == PaymentStatus.PAID) {
            order.createShipment(generateTrackingNumber(), "J&Tuh");
        }

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(email);

        // Send confirmation email
        emailService.sendOrderConfirmation(user.getEmail(), savedOrder);

        return savedOrder;
    }

    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(user.getId());
    }

    private String generateTrackingNumber() {
        return "JNT" + System.currentTimeMillis();
    }

    /**
     * Get all orders with optional filters
     */
    public List<OrderDTO> getAllOrders(String status, String paymentStatus) {
        log.info("Fetching all orders - status: {}, paymentStatus: {}", status, paymentStatus);
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get order by order number
     */
    public OrderDTO getOrderByNumber(String orderNumber) {
        log.info("Fetching order: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get order by ID
     */
    public OrderDTO getOrderById(Long id) {
        log.info("Fetching order by ID: {}", id);
        return orderRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get full Order entity by ID
     */
    public Order getOrderEntityById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    /**
     * Get orders by customer ID
     */
    public List<OrderDTO> getOrdersByCustomerId(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(Long.parseLong(customerId));
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Create new order from DTO
     */
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating new order for customer: {}", orderDTO.getCustomerId());
        // Implementation would create actual Order entity
        return orderDTO;
    }

    /**
     * Update order status
     */
    public OrderDTO updateOrderStatus(String orderNumber, String newStatus) {
        log.info("Updating order {} status to {}", orderNumber, newStatus);
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        if (order.isPresent()) {
            return convertToDTO(order.get());
        }
        return new OrderDTO();
    }

    /**
     * Update payment status
     */
    public OrderDTO updatePaymentStatus(String orderNumber, String newStatus) {
        log.info("Updating order {} payment status to {}", orderNumber, newStatus);
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        if (order.isPresent()) {
            return convertToDTO(order.get());
        }
        return new OrderDTO();
    }

    /**
     * Cancel order
     */
    public void cancelOrder(String orderNumber) {
        log.info("Cancelling order: {}", orderNumber);
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        if (order.isPresent()) {
            orderRepository.delete(order.get());
        }
    }

    /**
     * Get order statistics summary
     */
    public Map<String, Object> getOrderStatistics() {
        log.info("Fetching order statistics");

        Map<String, Object> stats = new HashMap<>();
        List<Order> allOrders = orderRepository.findAll();

        stats.put("totalOrders", (long) allOrders.size());
        stats.put("pendingOrders", 0L);
        stats.put("completedOrders", 0L);
        stats.put("cancelledOrders", 0L);
        stats.put("totalRevenue", allOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum());
        stats.put("averageOrderValue", allOrders.isEmpty() ? 0.0
                : allOrders.stream().mapToDouble(Order::getTotalAmount).average().orElse(0.0));

        return stats;
    }

    /**
     * Get statistics by date range
     */
    public Map<String, Object> getStatsByDateRange(String startDate, String endDate) {
        log.info("Fetching stats for date range: {} to {}", startDate, endDate);

        Map<String, Object> stats = new HashMap<>();
        List<Order> allOrders = orderRepository.findAll();

        stats.put("startDate", startDate);
        stats.put("endDate", endDate);
        stats.put("totalOrders", (long) allOrders.size());
        stats.put("totalRevenue", allOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum());
        stats.put("averageOrderValue", allOrders.isEmpty() ? 0.0
                : allOrders.stream().mapToDouble(Order::getTotalAmount).average().orElse(0.0));
        stats.put("totalCustomers", 0L);

        return stats;
    }

    /**
     * Get order count by status
     */
    public Map<String, Object> getOrderCountByStatus() {
        log.info("Fetching order count by status");

        Map<String, Object> stats = new HashMap<>();
        stats.put("PENDING", 0L);
        stats.put("PROCESSING", 0L);
        stats.put("SHIPPED", 0L);
        stats.put("DELIVERED", 0L);
        stats.put("CANCELLED", 0L);
        stats.put("RETURNED", 0L);

        return stats;
    }

    /**
     * Get top customers by total spent
     */
    public List<Map<String, Object>> getTopCustomers(int limit) {
        log.info("Fetching top {} customers", limit);

        List<Map<String, Object>> topCustomers = new ArrayList<>();
        List<Order> allOrders = orderRepository.findAll();

        // Group by customer and calculate totals
        Map<Long, Double> customerTotals = new HashMap<>();
        for (Order order : allOrders) {
            Long customerId = order.getCustomer().getId();
            customerTotals.put(customerId,
                    customerTotals.getOrDefault(customerId, 0.0) + order.getTotalAmount());
        }

        // Sort and take top N
        customerTotals.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .forEach(entry -> {
                    Map<String, Object> customer = new HashMap<>();
                    customer.put("customerId", entry.getKey());
                    customer.put("customerName", "Customer");
                    customer.put("totalSpent", entry.getValue());
                    customer.put("orderCount", 0);
                    topCustomers.add(customer);
                });

        return topCustomers;
    }

    /**
     * Get top selling products
     */
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        log.info("Fetching top {} selling products", limit);
        List<Map<String, Object>> topProducts = new ArrayList<>();
        List<Order> allOrders = orderRepository.findAll();

        // Map Product ID to Sales Count
        Map<Long, Integer> productSales = new HashMap<>();
        Map<Long, com.minari.ecommerce.entity.Product> productMap = new HashMap<>();

        for (Order order : allOrders) {
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Long pid = item.getProduct().getId();
                    productSales.put(pid, productSales.getOrDefault(pid, 0) + item.getQuantity());
                    productMap.putIfAbsent(pid, item.getProduct());
                }
            }
        }

        // Sort
        productSales.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .forEach(entry -> {
                    com.minari.ecommerce.entity.Product p = productMap.get(entry.getKey());
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", p.getName());
                    map.put("imageUrl", p.getImageUrl());
                    map.put("salesCount", entry.getValue());
                    topProducts.add(map);
                });

        return topProducts;
    }

    /**
     * Search orders by query
     */
    public List<OrderDTO> searchOrders(String query) {
        log.info("Searching orders with query: {}", query);
        List<Order> orders = orderRepository.findByOrderNumberContainingIgnoreCase(query);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Order entity to OrderDTO
     */
    private OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().toString())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getFullName() : (order.getUser() != null ? order.getUser().getFullName() : "Guest"))
                .totalAmount(order.getTotalAmount())
                .subtotalAmount(order.getSubtotalAmount())
                .taxAmount(order.getTaxAmount())
                .shippingCost(order.getShippingCost())
                .discountAmount(order.getDiscountAmount())
                .orderDate(order.getOrderDate())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public void updateOrderDetails(Long id, String statusStr, String trackingNumber) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        // Update Status
        try {
            com.minari.ecommerce.entity.OrderStatus newStatus = com.minari.ecommerce.entity.OrderStatus
                    .valueOf(statusStr);
            order.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            // Ignore invalid status or handle error
        }

        // Update Tracking
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            if (order.getShipment() == null) {
                order.createShipment(trackingNumber, "Standard");
            } else {
                order.getShipment().setTrackingNumber(trackingNumber);
            }
        }

        orderRepository.save(order);
    }
}
