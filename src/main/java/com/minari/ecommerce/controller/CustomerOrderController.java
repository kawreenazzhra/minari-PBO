package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/my-orders")
public class CustomerOrderController {

    private final OrderService orderService;

    public CustomerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Order order = orderService.getOrderEntityById(id);

        if (order == null) {
            return "redirect:/order-history?error=Order not found";
        }

        // Verify order belongs to this user
        if (order.getUser() == null || !order.getUser().getEmail().equals(email)) {
            return "redirect:/order-history?error=Unauthorized access";
        }

        model.addAttribute("order", order);
        return "orders/customer-detail";
    }
}
