/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.minari.notifications;

/**
 *
 * @author Aliyah Rahma
 */
public class EmailService {
    public void sendEmail(String toEmail, String subject, String message) {
        // Simulasi pengiriman email
        System.out.println("📧 === EMAIL NOTIFICATION ===");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("Email sent successfully!");
        System.out.println("=============================");
    }
    
    public void sendOrderConfirmation(String customerEmail, String orderId, String customerName) {
        String subject = "MINARI - Order Confirmation #" + orderId;
        String message = String.format(
            "Dear %s,\n\n" +
            "Thank you for your order! Your order #%s has been confirmed and is being processed.\n\n" +
            "We will notify you when your order ships.\n\n" +
            "Best regards,\nMINARI Team",
            customerName, orderId
        );
        
        sendEmail(customerEmail, subject, message);
    }
    
    public void sendOrderShipped(String customerEmail, String orderId, String trackingNumber) {
        String subject = "MINARI - Order Shipped #" + orderId;
        String message = String.format(
            "Great news! Your order #%s has been shipped.\n\n" +
            "Tracking Number: %s\n\n" +
            "You can track your package using the tracking number above.\n\n" +
            "Thank you for shopping with MINARI!",
            orderId, trackingNumber
        );
        
        sendEmail(customerEmail, subject, message);
    }
    
    public void sendOrderDelivered(String customerEmail, String orderId) {
        String subject = "MINARI - Order Delivered #" + orderId;
        String message = String.format(
            "Your order #%s has been delivered!\n\n" +
            "We hope you love your MINARI products. If you have any questions or need assistance, " +
            "please don't hesitate to contact our customer service.\n\n" +
            "Thank you for choosing MINARI!",
            orderId
        );
        
        sendEmail(customerEmail, subject, message);
    }
    
    public void sendPaymentFailed(String customerEmail, String orderId, String customerName) {
        String subject = "MINARI - Payment Failed #" + orderId;
        String message = String.format(
            "Dear %s,\n\n" +
            "We encountered an issue processing your payment for order #%s.\n\n" +
            "Please update your payment information to complete your order.\n\n" +
            "If you need assistance, contact our support team.\n\n" +
            "Best regards,\nMINARI Team",
            customerName, orderId
        );
        
        sendEmail(customerEmail, subject, message);
    }
}
