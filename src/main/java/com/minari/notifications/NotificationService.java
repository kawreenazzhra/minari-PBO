/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.minari.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Aliyah Rahma
 */
public class NotificationService {
    private EmailService emailService;
    private PopupNotificationService popupService;
    private List<OrderNotification> notificationHistory;
    
    public NotificationService() {
        this.emailService = new EmailService();
        this.popupService = new PopupNotificationService();
        this.notificationHistory = new ArrayList<>();
    }
    
    // Method untuk mengirim notifikasi order lengkap (email + popup)
    public OrderNotification sendOrderNotification(OrderNotification notification) {
        try {
            System.out.println("\n🚀 Sending notification for order: " + notification.getOrderId());
            
            // Send email notification
            if (!notification.getEmailSent()) {
                sendEmailNotification(notification);
                notification.sendEmailNotification();
            }
            
            // Show popup notification
            if (!notification.getPopupShown()) {
                sendPopupNotification(notification);
                notification.showPopup();
            }
            
            // Add to history
            notificationHistory.add(notification);
            
            System.out.println("✅ Notification completed for order: " + notification.getOrderId());
            notification.displayNotificationInfo();
            
        } catch (Exception e) {
            System.out.println("❌ Failed to send notification: " + e.getMessage());
        }
        
        return notification;
    }
    
    private void sendEmailNotification(OrderNotification notification) {
        switch (notification.getType()) {
            case OrderNotification.ORDER_CONFIRMED:
                emailService.sendOrderConfirmation(
                    notification.getUserEmail(),
                    notification.getOrderId(),
                    notification.getUserName()
                );
                break;
                
            case OrderNotification.ORDER_SHIPPED:
                emailService.sendOrderShipped(
                    notification.getUserEmail(),
                    notification.getOrderId(),
                    "TRACK_" + notification.getOrderId()
                );
                break;
                
            case OrderNotification.ORDER_DELIVERED:
                emailService.sendOrderDelivered(
                    notification.getUserEmail(),
                    notification.getOrderId()
                );
                break;
                
            case OrderNotification.PAYMENT_FAILED:
                emailService.sendPaymentFailed(
                    notification.getUserEmail(),
                    notification.getOrderId(),
                    notification.getUserName()
                );
                break;
                
            default:
                emailService.sendEmail(
                    notification.getUserEmail(),
                    "MINARI - " + notification.getTitle(),
                    notification.getMessage()
                );
        }
    }
    
    private void sendPopupNotification(OrderNotification notification) {
        switch (notification.getType()) {
            case OrderNotification.ORDER_CONFIRMED:
                popupService.showSuccessPopup("Order #" + notification.getOrderId() + " confirmed!");
                break;
                
            case OrderNotification.ORDER_SHIPPED:
                popupService.showInfoPopup("Order #" + notification.getOrderId() + " has been shipped!");
                break;
                
            case OrderNotification.ORDER_DELIVERED:
                popupService.showSuccessPopup("Order #" + notification.getOrderId() + " delivered successfully!");
                break;
                
            case OrderNotification.PAYMENT_FAILED:
                popupService.showErrorPopup("Payment failed for order #" + notification.getOrderId());
                break;
                
            case OrderNotification.ORDER_CANCELLED:
                popupService.showWarningPopup("Order #" + notification.getOrderId() + " has been cancelled");
                break;
                
            default:
                popupService.showInfoPopup(notification.getMessage());
        }
    }
    
    // Method untuk mendapatkan notifikasi berdasarkan user
    public List<OrderNotification> getUserNotifications(String userId) {
        return notificationHistory.stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    public List<OrderNotification> getUnreadNotifications(String userId) {
        return notificationHistory.stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.getIsRead())
                .collect(Collectors.toList());
    }
    
    public void markAllAsRead(String userId) {
        notificationHistory.stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.getIsRead())
                .forEach(OrderNotification::markAsRead);
    }
    
    // Statistics
    public void displayStatistics() {
        System.out.println("\n=== NOTIFICATION STATISTICS ===");
        System.out.println("Total Notifications: " + notificationHistory.size());
        System.out.println("Unread Popups: " + popupService.getUnreadCount());
        
        long urgentCount = notificationHistory.stream()
                .filter(OrderNotification::isUrgent)
                .count();
        System.out.println("Urgent Notifications: " + urgentCount);
        
        notificationHistory.stream()
                .collect(Collectors.groupingBy(OrderNotification::getType, Collectors.counting()))
                .forEach((type, count) -> System.out.println(type + ": " + count));
    }
}
