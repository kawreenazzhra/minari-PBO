package com.minari.ecommerce.controller;

import com.minari.ecommerce.dto.CartSessionItem;
import com.minari.ecommerce.entity.Address;
import com.minari.ecommerce.entity.RegisteredCustomer;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartService cartService;
    private static final String GUEST_CART_SESSION_KEY = "guestCart";
    
    public AuthController(UserRepository userRepository, 
                         PasswordEncoder passwordEncoder,
                         ShoppingCartService cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, 
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully");
        }
        model.addAttribute("pageTitle", "Login - Minari");
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("customer", new RegisteredCustomer());
        model.addAttribute("pageTitle", "Register - Minari");
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("customer") RegisteredCustomer customer,
                          BindingResult bindingResult,
                          @RequestParam String confirmPassword,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        
        // Validation
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(customer.getEmail())) {
            bindingResult.rejectValue("email", "error.customer", "Email already exists");
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/register";
        }
        
        // Check password match
        if (!customer.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.customer", "Passwords do not match");
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }
        
        // Check minimum password length
        if (customer.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "error.customer", "Password must be at least 6 characters");
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters");
            return "redirect:/register";
        }
        
        try {
            // Encode password and set user data
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customer.setIsActive(true);

            // Set default shipping address
            Address address = new Address();
            customer.setShippingAddress(address);

            // Save user
            userRepository.save(customer);
            
            // Merge guest cart items to user cart
            List<CartSessionItem> guestCart = (List<CartSessionItem>) session.getAttribute(GUEST_CART_SESSION_KEY);
            if (guestCart != null && !guestCart.isEmpty()) {
                for (CartSessionItem item : guestCart) {
                    cartService.addToCart(customer.getEmail(), item.getProductId(), item.getQuantity());
                }
                // Clear guest cart from session
                session.removeAttribute(GUEST_CART_SESSION_KEY);
            }

            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("pageTitle", "Forgot Password - Minari");
        return "auth/forgot-password";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}