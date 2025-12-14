package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Order;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendOrderConfirmation(String toEmail, Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - Minari");
            message.setText(buildOrderConfirmationEmail(order));
            message.setFrom("noreply@minari.com");
            
            mailSender.send(message);
            System.out.println("Order confirmation email sent to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    
    public void sendEmail(String toEmail, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom("noreply@minari.com");
            
            mailSender.send(message);
            System.out.println("Email sent to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    
    private String buildOrderConfirmationEmail(Order order) {
        return String.format("""
            Dear %s,
            
            Thank you for your order! Your order #%s has been received and is being processed.
            
            Order Details:
            - Order Number: %s
            - Total Amount: $%.2f
            - Order Date: %s
            
            We'll notify you when your order ships.
            
            Thank you for shopping with Minari!
            
            Best regards,
            Minari Team
            """, 
            order.getCustomer().getFullName(),
            order.getOrderNumber(),
            order.getOrderNumber(),
            order.getTotalAmount(),
            order.getOrderDate()
        );
    }
}