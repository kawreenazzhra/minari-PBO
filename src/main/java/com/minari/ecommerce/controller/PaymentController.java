package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.service.ShoppingCartService;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final ShoppingCartService cartService;
    private final UserRepository userRepository;

    public PaymentController(ShoppingCartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String paymentPage(@RequestParam(name = "items", required = false) String items,
                            Model model,
                            Principal principal) {
        // Redirect to the new checkout flow
        return "redirect:/checkout"; 
    }
}
