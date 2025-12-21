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
        
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        var cart = cartService.getCartForUser(email);
        
        if (cart.getItems().isEmpty()) {
            return "redirect:/products";
        }

        // Add addresses
        if (user instanceof Customer) {
            model.addAttribute("addresses", ((Customer) user).getSavedAddresses());
        } else {
            model.addAttribute("addresses", List.of());
        }

        // Cart summary (Logic could be refined to only show selected items if 'items' param is used)
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("subtotal", cart.getTotalAmount());
        model.addAttribute("total", cart.getTotalAmount()); 

        return "payment/view"; 
    }
}
