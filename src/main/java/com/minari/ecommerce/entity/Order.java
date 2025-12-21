package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private OrderStatus status = OrderStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Column(name = "subtotal_amount", nullable = false)
    private Double subtotalAmount;
    
    @Column(name = "tax_amount", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double taxAmount = 0.0;
    
    @Column(name = "shipping_cost", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double shippingCost = 0.0;
    
    @Column(name = "discount_amount", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
    private Double discountAmount = 0.0;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "order")
    private Payment payment;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "order")
    private Shipment shipment;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLog> orderLogs = new ArrayList<>();
    
    @Column(name = "promotion_id")
    private Long promotionId;
    
    @Column(name = "promo_code")
    private String promoCode;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;
    
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;
    
    @Column(name = "is_gift", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isGift = false;
    
    @Column(name = "gift_message", columnDefinition = "TEXT")
    private String giftMessage;

    // Constructors
    public Order() {}

    public Order(Long id, String orderNumber, OrderStatus status, User user, Customer customer, List<OrderItem> items, Double totalAmount, Double subtotalAmount, Double taxAmount, Double shippingCost, Double discountAmount, Address shippingAddress, Payment payment, Shipment shipment, List<OrderLog> orderLogs, Long promotionId, String promoCode, String notes, String internalNotes, LocalDateTime orderDate, LocalDateTime updatedAt, LocalDateTime estimatedDeliveryDate, LocalDateTime actualDeliveryDate, Boolean isGift, String giftMessage) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.user = user;
        this.customer = customer;
        this.items = items;
        this.totalAmount = totalAmount;
        this.subtotalAmount = subtotalAmount;
        this.taxAmount = taxAmount;
        this.shippingCost = shippingCost;
        this.discountAmount = discountAmount;
        this.shippingAddress = shippingAddress;
        this.payment = payment;
        this.shipment = shipment;
        this.orderLogs = orderLogs;
        this.promotionId = promotionId;
        this.promoCode = promoCode;
        this.notes = notes;
        this.internalNotes = internalNotes;
        this.orderDate = orderDate;
        this.updatedAt = updatedAt;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.isGift = isGift;
        this.giftMessage = giftMessage;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(Double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public List<OrderLog> getOrderLogs() {
        return orderLogs;
    }

    public void setOrderLogs(List<OrderLog> orderLogs) {
        this.orderLogs = orderLogs;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Boolean getIsGift() {
        return isGift;
    }

    public void setIsGift(Boolean isGift) {
        this.isGift = isGift;
    }

    public String getGiftMessage() {
        return giftMessage;
    }

    public void setGiftMessage(String giftMessage) {
        this.giftMessage = giftMessage;
    }
    
    // Business methods
    public void updateStatus(OrderStatus newStatus, String changedBy, String notes) {
        OrderStatus oldStatus = this.status;
        OrderLog log = new OrderLog(orderNumber, oldStatus, newStatus, changedBy, notes);
        orderLogs.add(log);
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String generateOrderNumber() {
        return "ORD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + id;
    }
    
    public double calculateTotal() {
        return subtotalAmount + taxAmount + shippingCost - discountAmount;
    }
    
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
        estimatedDeliveryDate = orderDate.plusDays(7);
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void createShipment(String trackingNumber, String shipmentMethod) {
        Shipment shipment = new Shipment(this.orderNumber, trackingNumber, shipmentMethod, this.shippingAddress);
        this.shipment = shipment;
    }
}