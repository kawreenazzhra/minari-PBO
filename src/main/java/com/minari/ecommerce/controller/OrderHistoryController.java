package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class OrderHistoryController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderHistoryController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/order-history")
    public String viewOrderHistory(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        List<Order> orders = orderService.getUserOrders(email);
        
        model.addAttribute("orders", orders);
        
        return "orders/history";
    }

    @GetMapping("/order/{id}")
    public String viewOrderDetails(@PathVariable("id") Long id, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Order order = orderService.getOrderEntityById(id);

        if (order == null || !order.getUser().getEmail().equals(email)) {
            return "redirect:/order-history?error=access_denied";
        }

        model.addAttribute("order", order);
        return "orders/details";
    }
}
