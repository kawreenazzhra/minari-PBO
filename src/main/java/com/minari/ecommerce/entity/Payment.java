package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(name = "payment_gateway")
    private String paymentGateway;
    
    @Column(name = "gateway_response_code")
    private String gatewayResponseCode;
    
    @Column(name = "gateway_response_message")
    private String gatewayResponseMessage;
    
    @Column(name = "refund_id")
    private String refundId;
    
    @Column(name = "refund_amount")
    private Double refundAmount = 0.0;
    
    @Column(name = "refund_date")
    private LocalDateTime refundDate;
    
    @Column(name = "refund_reason")
    private String refundReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Payment() {}

    public Payment(Long id, Order order, PaymentStatus status, Double amount, LocalDateTime paymentDate, PaymentMethod paymentMethod, String transactionId, String paymentGateway, String gatewayResponseCode, String gatewayResponseMessage, String refundId, Double refundAmount, LocalDateTime refundDate, String refundReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentGateway = paymentGateway;
        this.gatewayResponseCode = gatewayResponseCode;
        this.gatewayResponseMessage = gatewayResponseMessage;
        this.refundId = refundId;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
        this.refundReason = refundReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String getGatewayResponseCode() {
        return gatewayResponseCode;
    }

    public void setGatewayResponseCode(String gatewayResponseCode) {
        this.gatewayResponseCode = gatewayResponseCode;
    }

    public String getGatewayResponseMessage() {
        return gatewayResponseMessage;
    }

    public void setGatewayResponseMessage(String gatewayResponseMessage) {
        this.gatewayResponseMessage = gatewayResponseMessage;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public PaymentStatus processPayment() {
        try {
            System.out.println("Processing payment of $" + amount + " via " + paymentMethod);
            
            // COD = PENDING, others = PAID
            if (paymentMethod == PaymentMethod.COD) {
                this.status = PaymentStatus.PENDING;
                System.out.println("COD payment - Status set to PENDING");
            } else {
                this.status = PaymentStatus.PAID;
                this.transactionId = generateTransactionId();
                this.paymentDate = LocalDateTime.now();
                System.out.println("Payment successful! Transaction ID: " + transactionId);
            }
            
            return this.status;
        } catch (Exception e) {
            this.status = PaymentStatus.FAILED;
            System.out.println("Payment failed: " + e.getMessage());
            return PaymentStatus.FAILED;
        }
    }
    
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + id;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}