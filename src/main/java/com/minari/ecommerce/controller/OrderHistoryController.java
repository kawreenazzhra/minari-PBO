package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/order-history")
public class OrderHistoryController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderHistoryController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String viewOrderHistory(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        List<Order> orders = orderService.getUserOrders(email);
        
        model.addAttribute("orders", orders);
        
        userRepository.findByEmail(email).ifPresent(user -> model.addAttribute("user", user));

        return "orders/history";
    }
}
