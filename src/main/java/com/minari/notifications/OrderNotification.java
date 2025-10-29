package com.minari.notifications;

import java.time.LocalDateTime;

import com.minari.model.Notification;

public class OrderNotification implements Notification{
    private String notificationId;
    private String orderId;
    private String userId;
    private String userEmail;
    private String userName;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Boolean emailSent;
    private Boolean popupShown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constants for notification types
    public static final String ORDER_CREATED = "ORDER_CREATED";
    public static final String ORDER_CONFIRMED = "ORDER_CONFIRMED";
    public static final String ORDER_SHIPPED = "ORDER_SHIPPED";
    public static final String ORDER_DELIVERED = "ORDER_DELIVERED";
    public static final String PAYMENT_RECEIVED = "PAYMENT_RECEIVED";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String ORDER_CANCELLED = "ORDER_CANCELLED";
    public static final String LOW_STOCK_ALERT = "LOW_STOCK_ALERT";
    
    // Default constructor
    public OrderNotification() {
        this.notificationId = generateNotificationId();
        this.isRead = false;
        this.emailSent = false;
        this.popupShown = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public OrderNotification(String orderId, String userId, String userEmail, 
                           String userName, String title, String message, String type) {
        this();
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    
    private String generateNotificationId() {
        return "NOTIF_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Implement methods from Notification interface
    @Override
    public void sendNotification(String message) {
        this.message = message;
        this.emailSent = true;
        this.updatedAt = LocalDateTime.now();
        System.out.println("📧 Notification sent to " + userEmail + ": " + message);
    }
    
    @Override
    public void showPopupNotification(String message) {
        this.message = message;
        this.popupShown = true;
        this.updatedAt = LocalDateTime.now();
        System.out.println("🪟 Popup shown for user " + userName + ": " + message);
    }
    
    // Business methods
    public void markAsRead() {
        this.isRead = true;
        this.updatedAt = LocalDateTime.now();
        System.out.println("✅ Notification marked as read: " + title);
    }
    
    public void markAsUnread() {
        this.isRead = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void sendEmailNotification() {
        this.emailSent = true;
        this.updatedAt = LocalDateTime.now();
        System.out.println("✓ Email notification sent for: " + title);
    }
    
    public void showPopup() {
        this.popupShown = true;
        this.updatedAt = LocalDateTime.now();
        System.out.println("✓ Popup notification shown for: " + title);
    }
    
    public boolean isUrgent() {
        return PAYMENT_FAILED.equals(type) || ORDER_CANCELLED.equals(type) || LOW_STOCK_ALERT.equals(type);
    }
    
    public String getStatus() {
        if (isRead) {
            return "READ";
        } else if (popupShown && emailSent) {
            return "COMPLETE";
        } else if (popupShown) {
            return "POPUP_SHOWN";
        } else if (emailSent) {
            return "EMAIL_SENT";
        } else {
            return "PENDING";
        }
    }
    
    public String getPriority() {
        if (isUrgent()) {
            return "HIGH";
        } else if (ORDER_CONFIRMED.equals(type) || ORDER_SHIPPED.equals(type)) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    public void displayNotificationInfo() {
        System.out.println("=== ORDER NOTIFICATION ===");
        System.out.println("ID: " + notificationId);
        System.out.println("Order: " + orderId);
        System.out.println("User: " + userName + " (" + userEmail + ")");
        System.out.println("Title: " + title);
        System.out.println("Message: " + message);
        System.out.println("Type: " + type);
        System.out.println("Status: " + getStatus());
        System.out.println("Priority: " + getPriority());
        System.out.println("Urgent: " + (isUrgent() ? "YES" : "NO"));
        System.out.println("Created: " + createdAt);
        System.out.println("Updated: " + updatedAt);
        System.out.println("==========================");
    }
    
    // Factory methods untuk membuat notifikasi umum
    public static OrderNotification createOrderConfirmed(String orderId, String userId, 
                                                        String userEmail, String userName) {
        return new OrderNotification(
            orderId, userId, userEmail, userName,
            "Order Confirmed",
            "Your order #" + orderId + " has been confirmed and is being processed",
            ORDER_CONFIRMED
        );
    }
    
    public static OrderNotification createOrderShipped(String orderId, String userId, 
                                                      String userEmail, String userName) {
        return new OrderNotification(
            orderId, userId, userEmail, userName,
            "Order Shipped", 
            "Your order #" + orderId + " has been shipped and is on its way",
            ORDER_SHIPPED
        );
    }
    
    public static OrderNotification createOrderDelivered(String orderId, String userId, 
                                                        String userEmail, String userName) {
        return new OrderNotification(
            orderId, userId, userEmail, userName,
            "Order Delivered",
            "Your order #" + orderId + " has been delivered successfully",
            ORDER_DELIVERED
        );
    }
    
    public static OrderNotification createPaymentFailed(String orderId, String userId,
                                                      String userEmail, String userName) {
        return new OrderNotification(
            orderId, userId, userEmail, userName,
            "Payment Failed",
            "There was an issue processing your payment for order #" + orderId,
            PAYMENT_FAILED
        );
    }
    
    public static OrderNotification createOrderCancelled(String orderId, String userId,
                                                        String userEmail, String userName) {
        return new OrderNotification(
            orderId, userId, userEmail, userName,
            "Order Cancelled",
            "Your order #" + orderId + " has been cancelled",
            ORDER_CANCELLED
        );
    }
    
    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public Boolean getEmailSent() { return emailSent; }
    public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }
    
    public Boolean getPopupShown() { return popupShown; }
    public void setPopupShown(Boolean popupShown) { this.popupShown = popupShown; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
