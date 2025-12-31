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
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
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
    private final ProductService productService;
    private final com.minari.ecommerce.repository.CustomerRepository customerRepository;

    private final com.minari.ecommerce.service.PromotionService promotionService;

    public OrderService(OrderRepository orderRepository, ShoppingCartService cartService, UserRepository userRepository,
            EmailService emailService, ProductService productService, 
            com.minari.ecommerce.repository.CustomerRepository customerRepository,
            com.minari.ecommerce.service.PromotionService promotionService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.productService = productService;
        this.customerRepository = customerRepository;
        this.promotionService = promotionService;
    }

    // Overloaded method with selectedProductIds parameter for selective checkout
    public Order createOrderFromCart(String email, Address shippingAddress, PaymentMethod paymentMethod, List<Long> selectedProductIds) {
        if (selectedProductIds == null || selectedProductIds.isEmpty()) {
            // If no selection, use original method (all items)
            return createOrderFromCart(email, shippingAddress, paymentMethod);
        }
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart cart = cartService.getCartForUser(email);

        System.out.println("[OrderService] Creating order from cart for user: " + email);
        System.out.println("[OrderService] Cart has " + cart.getItems().size() + " items");
        System.out.println("[OrderService] Selected product IDs: " + selectedProductIds);
        
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Filter cart items based on selection
        List<CartItem> itemsToProcess = cart.getItems().stream()
            .filter(item -> selectedProductIds.contains(item.getProduct().getId()))
            .collect(Collectors.toList());
            
        System.out.println("[OrderService] Filtered to " + itemsToProcess.size() + " selected items");
        
        if (itemsToProcess.isEmpty()) {
            throw new RuntimeException("No items selected for checkout");
        }
        
        // Validate stock and deduct for selected items only
        for (CartItem item : itemsToProcess) {
            com.minari.ecommerce.entity.Product product = item.getProduct();
            if (!product.canFulfillOrder(item.getQuantity())) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }
            int newStock = product.getStockQuantity() - item.getQuantity();
            product.setStockQuantity(newStock);
            productService.saveProduct(product);
            log.info("Stock deducted for product: {} new stock: {}", product.getName(), newStock);
        }
        
        Order order = new Order();
        order.setOrderNumber("MIN" + System.currentTimeMillis() + new Random().nextInt(100));
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            order.setCustomer((com.minari.ecommerce.entity.Customer) user);
        } else {
             log.warn("Order created by non-customer user: {}", user.getEmail());
        }
        
        // Create address snapshot
        Address orderAddress = new Address();
        orderAddress.setRecipientName(shippingAddress.getRecipientName());
        orderAddress.setPhoneNumber(shippingAddress.getPhoneNumber());
        orderAddress.setStreetAddress(shippingAddress.getStreetAddress());
        orderAddress.setCity(shippingAddress.getCity());
        orderAddress.setProvince(shippingAddress.getProvince());
        orderAddress.setState(shippingAddress.getState() != null ? shippingAddress.getState() : shippingAddress.getProvince());
        orderAddress.setCountry(shippingAddress.getCountry() != null ? shippingAddress.getCountry() : "Indonesia");
        orderAddress.setZipcode(shippingAddress.getZipcode());
        order.setShippingAddress(orderAddress);

        // Calculate totals for selected items only
        double productsSubtotal = itemsToProcess.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        
        double shippingFee = 15000.0;

        // Calculate discount for selected items
        DiscountCalculation discountCalc = promotionService.calculateBestDiscount(
            itemsToProcess.stream()
                .map(CartItem::getProduct)
                .collect(Collectors.toList()),
            productsSubtotal
        );
        
        double bestDiscountAmount = discountCalc.getDiscountAmount();
        com.minari.ecommerce.entity.Promotion appliedPromotion = discountCalc.getAppliedPromotion();
        
        if (appliedPromotion != null) {
            log.info("Applying promotion: {} with discount: {}", appliedPromotion.getName(), bestDiscountAmount);
            appliedPromotion.setUsedCount(appliedPromotion.getUsedCount() + 1);
            promotionService.savePromotion(appliedPromotion);
            order.setPromotionId(appliedPromotion.getId());
            order.setPromoCode(appliedPromotion.getPromoCode());
        }
        
        order.setSubtotalAmount(productsSubtotal);
        order.setDiscountAmount(bestDiscountAmount);
        order.setShippingCost(shippingFee);
        order.setTotalAmount(Math.max(0, (productsSubtotal - bestDiscountAmount) + shippingFee));

        // Create order items from selected cart items only
        List<OrderItem> orderItems = itemsToProcess.stream()
                .map(cartItem -> new OrderItem(order, cartItem.getProduct(), cartItem))
                .collect(Collectors.toList());

        order.setItems(orderItems);
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.processPayment();

        order.setPayment(payment);

        if (payment.getStatus() == PaymentStatus.PAID || payment.getPaymentMethod() == PaymentMethod.COD) {
            order.createShipment(generateTrackingNumber(), "J&Tuh");
        }

        System.out.println("[OrderService] Saving order...");
        Order savedOrder = orderRepository.saveAndFlush(order);
        System.out.println("[OrderService] Order saved successfully! Order ID: " + savedOrder.getId() + ", Order Number: " + savedOrder.getOrderNumber());

        // Remove only selected items from cart
        for (Long productId : selectedProductIds) {
            cartService.removeFromCart(email, productId);
        }
        System.out.println("[OrderService] Removed selected items from cart");

        try {
             emailService.sendOrderConfirmation(user.getEmail(), savedOrder);
             emailService.sendAdminOrderNotification(savedOrder);
        } catch (Exception e) {
             log.error("Failed to send order confirmation email", e);
        }

        return savedOrder;
    }

    public Order createOrderFromCart(String email, Address shippingAddress, PaymentMethod paymentMethod) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart cart = cartService.getCartForUser(email);

        System.out.println("[OrderService] Creating order from cart for user: " + email);
        System.out.println("[OrderService] Cart has " + cart.getItems().size() + " items");
        
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Validate stock and deduct
        for (CartItem item : cart.getItems()) {
            com.minari.ecommerce.entity.Product product = item.getProduct();
            if (!product.canFulfillOrder(item.getQuantity())) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }
            // Deduct stock
            int newStock = product.getStockQuantity() - item.getQuantity();
            product.setStockQuantity(newStock);
            productService.saveProduct(product);
            log.info("Stock deducted for product: {} new stock: {}", product.getName(), newStock);
        }
        Order order = new Order();
        order.setOrderNumber("MIN" + System.currentTimeMillis() + new Random().nextInt(100));
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            order.setCustomer((com.minari.ecommerce.entity.Customer) user);
        } else {
             // User is not a customer (e.g. Admin). 
             // Since we made customer_id nullable, we can proceed.
             // We could log this event.
             log.warn("Order created by non-customer user: {}", user.getEmail());
        }
        // Create a snapshot of the address for this specific order
        // This ensures the order is saved correctly to the database by avoiding detached entity errors
        Address orderAddress = new Address();
        orderAddress.setRecipientName(shippingAddress.getRecipientName());
        orderAddress.setPhoneNumber(shippingAddress.getPhoneNumber());
        orderAddress.setStreetAddress(shippingAddress.getStreetAddress());
        orderAddress.setCity(shippingAddress.getCity());
        orderAddress.setProvince(shippingAddress.getProvince());
        orderAddress.setState(shippingAddress.getState() != null ? shippingAddress.getState() : shippingAddress.getProvince()); // Fallback if state is null
        orderAddress.setCountry(shippingAddress.getCountry() != null ? shippingAddress.getCountry() : "Indonesia"); // Default if null
        orderAddress.setZipcode(shippingAddress.getZipcode());
        // Do NOT set ID, let it generate a new unique ID for this order history record
        
        order.setShippingAddress(orderAddress);
    
        double productsSubtotal = cart.getTotalAmount();
        double shippingFee = 15000.0;
        
        // --- Auto-Apply Best Promotion Logic ---
        com.minari.ecommerce.dto.DiscountCalculation discountCalc = promotionService.calculateBestDiscount(cart);
        double bestDiscountAmount = discountCalc.getDiscountAmount();
        com.minari.ecommerce.entity.Promotion appliedPromotion = discountCalc.getAppliedPromotion();
        
        // Apply discount
        if (appliedPromotion != null) {
            log.info("Applying promotion: {} with discount: {}", appliedPromotion.getName(), bestDiscountAmount);
            // Update usage count
            appliedPromotion.setUsedCount(appliedPromotion.getUsedCount() + 1);
            promotionService.savePromotion(appliedPromotion);
            
            // Link promotion to order
            order.setPromotionId(appliedPromotion.getId());
            order.setPromoCode(appliedPromotion.getPromoCode());
        }
        
        order.setSubtotalAmount(productsSubtotal);
        order.setDiscountAmount(bestDiscountAmount);
        order.setShippingCost(shippingFee);
        order.setTotalAmount(Math.max(0, (productsSubtotal - bestDiscountAmount) + shippingFee));

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> new OrderItem(order, cartItem.getProduct(), cartItem))
                .collect(Collectors.toList());

        order.setItems(orderItems);
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.processPayment(); // Simulasi payment

        order.setPayment(payment);

        // Create shipment if payment is successful OR if COD
        if (payment.getStatus() == PaymentStatus.PAID || payment.getPaymentMethod() == PaymentMethod.COD) {
            order.createShipment(generateTrackingNumber(), "J&Tuh");
        }

        // Save order
        System.out.println("[OrderService] Saving order...");
        Order savedOrder = orderRepository.saveAndFlush(order); // Force flush to catch trigger/constraint errors immediately
        System.out.println("[OrderService] Order saved successfully! Order ID: " + savedOrder.getId() + ", Order Number: " + savedOrder.getOrderNumber());

        // Clear cart
        System.out.println("[OrderService] Clearing cart for user: " + email);
        cartService.clearCart(email);
        System.out.println("[OrderService] Cart cleared successfully");

        // Send confirmation email and admin notification
        try {
             emailService.sendOrderConfirmation(user.getEmail(), savedOrder);
             emailService.sendAdminOrderNotification(savedOrder);
        } catch (Exception e) {
             log.error("Failed to send order confirmation email", e);
             // Don't rollback order for email failure
        }

        return savedOrder;
    }

    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser_IdOrderByOrderDateDesc(user.getId());
    }

    private String generateTrackingNumber() {
        return "JNT" + System.currentTimeMillis() + (int)(Math.random() * 1000);
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
    /**
     * Get order statistics summary (Optimized)
     */
    public Map<String, Object> getOrderStatistics() {
        log.info("Fetching order statistics (Optimized)");

        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus(com.minari.ecommerce.entity.OrderStatus.PENDING));
        stats.put("completedOrders", orderRepository.countByStatus(com.minari.ecommerce.entity.OrderStatus.DELIVERED));
        stats.put("cancelledOrders", orderRepository.countByStatus(com.minari.ecommerce.entity.OrderStatus.CANCELLED));
        
        Double totalRevenue = orderRepository.sumTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        
        long count = orderRepository.count();
        stats.put("averageOrderValue", count > 0 ? (totalRevenue != null ? totalRevenue : 0.0) / count : 0.0);

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
    /**
     * Get top selling products (Optimized)
     */
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        log.info("Fetching top {} selling products (Optimized)", limit);
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        List<Object[]> results = orderRepository.findTopSellingProducts(org.springframework.data.domain.PageRequest.of(0, limit));
        
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("imageUrl", row[1]);
            map.put("salesCount", row[2]);
            topProducts.add(map);
        }

        return topProducts;
    }

    /**
     * Get recent orders (Optimized)
     */
    public List<OrderDTO> getRecentOrders(int limit) {
         return orderRepository.findRecentOrders(org.springframework.data.domain.PageRequest.of(0, limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
                .shippingAddressStreet(order.getShippingAddress() != null ? order.getShippingAddress().getStreetAddress() : null)
                .shippingAddressCity(order.getShippingAddress() != null ? order.getShippingAddress().getCity() : null)
                .shippingAddressState(order.getShippingAddress() != null ? order.getShippingAddress().getState() : null)
                .shippingAddressPostalCode(order.getShippingAddress() != null ? order.getShippingAddress().getZipcode() : null)
                .shippingAddressCountry(order.getShippingAddress() != null ? order.getShippingAddress().getCountry() : null)
                .paymentStatus(order.getPayment() != null ? order.getPayment().getStatus().toString() : "PENDING")
                .paymentMethod(order.getPayment() != null ? order.getPayment().getPaymentMethod().toString() : null)
                .orderDate(order.getOrderDate())
                .updatedAt(order.getUpdatedAt())
                .shipmentStatus(order.getShipment() != null && order.getShipment().getCurrentStatus() != null ? order.getShipment().getCurrentStatus().toString() : null)
                .trackingNumber(order.getShipment() != null ? order.getShipment().getTrackingNumber() : null)
                .items(order.getItems().stream().map(item -> OrderDTO.OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .variantInfo(item.getVariantInfo())
                        .imageUrl(item.getImageUrl())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    public void updateOrderDetails(Long id, String statusStr, String trackingNumber) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Track old status to detect status change
        com.minari.ecommerce.entity.OrderStatus oldStatus = order.getStatus();

        // Update Status
        try {
            com.minari.ecommerce.entity.OrderStatus newStatus = com.minari.ecommerce.entity.OrderStatus
                    .valueOf(statusStr);
            order.setStatus(newStatus);

            // Auto-update Payment Status for COD when Delivered
            if (newStatus == com.minari.ecommerce.entity.OrderStatus.DELIVERED && 
                order.getPayment() != null && 
                order.getPayment().getPaymentMethod() == PaymentMethod.COD) {
                
                order.getPayment().setStatus(PaymentStatus.PAID);
                order.getPayment().setPaymentDate(LocalDateTime.now());
            }
            
            // Award loyalty points when order is delivered (only if status changed to DELIVERED)
            if (newStatus == com.minari.ecommerce.entity.OrderStatus.DELIVERED && 
                oldStatus != com.minari.ecommerce.entity.OrderStatus.DELIVERED) {
                awardLoyaltyPoints(order);
            }
        } catch (IllegalArgumentException e) {
            // Ignore invalid status or handle error
        }

        // Update Tracking
        boolean isShippedOrDelivered = order.getStatus() == com.minari.ecommerce.entity.OrderStatus.SHIPPED || 
                                       order.getStatus() == com.minari.ecommerce.entity.OrderStatus.DELIVERED;
                                       
        if (isShippedOrDelivered) {
             // Only auto-generate if input is empty AND current shipment doesn't have one
             boolean hasExistingTracking = order.getShipment() != null && 
                                           order.getShipment().getTrackingNumber() != null && 
                                           !order.getShipment().getTrackingNumber().isEmpty();
                                           
             if ((trackingNumber == null || trackingNumber.trim().isEmpty()) && !hasExistingTracking) {
                 trackingNumber = generateTrackingNumber();
             }
        }

        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            if (order.getShipment() == null) {
                order.createShipment(trackingNumber, "Standard");
            } else {
                order.getShipment().setTrackingNumber(trackingNumber);
            }
            // Auto update shipment status to IN_TRANSIT if we have tracking
             if (order.getShipment() != null && order.getShipment().getCurrentStatus() == com.minari.ecommerce.entity.ShipmentStatus.AWAITING_PICKUP) {
                order.getShipment().setCurrentStatus(com.minari.ecommerce.entity.ShipmentStatus.IN_TRANSIT);
            }
        }

        orderRepository.save(order);
    }

    /**
     * Award loyalty points to customer when order is delivered
     * Formula: Every Rp 10,000 spent = 1 loyalty point
     */
    private void awardLoyaltyPoints(Order order) {
        try {
            if (order.getCustomer() == null) {
                log.warn("Cannot award loyalty points - order has no customer: {}", order.getOrderNumber());
                return;
            }

            // Calculate points: Rp 10,000 = 1 point
            int pointsToAward = (int) (order.getTotalAmount() / 10000);
            
            if (pointsToAward > 0) {
                // Directly update customer loyalty points via repository
                com.minari.ecommerce.entity.Customer customer = order.getCustomer();
                customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsToAward);
                customerRepository.save(customer);
                
                log.info("Awarded {} loyalty points to customer {} for order {}", 
                    pointsToAward, customer.getId(), order.getOrderNumber());
            }
        } catch (Exception e) {
            log.error("Failed to award loyalty points for order: {}", order.getOrderNumber(), e);
            // Don't throw exception - loyalty points failure shouldn't break order update
        }
    }
}
