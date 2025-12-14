package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.repository.OrderRepository;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardService {
    
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    public DashboardService(ProductRepository productRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    
    public void showAdminDashboard() {
        long productCount = productRepository.count();
        long orderCount = orderRepository.count();
        long userCount = userRepository.count();
        
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        
        System.out.println("=== MINARI ADMIN DASHBOARD ===");
        System.out.println("📦 Products: " + productCount);
        System.out.println("📊 Orders: " + orderCount);
        System.out.println("👥 Customers: " + userCount);
        System.out.println("💰 Total Revenue: $" + totalRevenue);
        
        System.out.println("\n🆕 Recent Orders:");
        List<Order> recentOrders = orderRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(5)
                .toList();
        
        for (Order order : recentOrders) {
            System.out.println(" - " + order.getOrderNumber() + " | " + 
                             order.getCustomer().getFullName() + " | $" + 
                             order.getTotalAmount());
        }
    }
}