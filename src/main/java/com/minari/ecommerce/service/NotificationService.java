package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private final EmailService emailService;
    
    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    public void sendOrderStatusNotification(Order order, Customer customer) {
        String subject = "Order Update - Minari";
        String content = buildOrderNotificationContent(order, customer);
        
        emailService.sendEmail(customer.getEmail(), subject, content);
        
        System.out.println("Order status notification sent for order: " + order.getOrderNumber());
    }
    
    private String buildOrderNotificationContent(Order order, Customer customer) {
        return String.format("""
            Dear %s,
            
            Your order #%s status has been updated to: %s
            
            Order Details:
            - Total Amount: $%.2f
            - Order Date: %s
            
            Thank you for shopping with Minari!
            
            Best regards,
            Minari Team
            """, 
            customer.getFullName(),
            order.getOrderNumber(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getOrderDate()
        );
    }
}