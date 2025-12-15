package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Address;
import com.minari.ecommerce.entity.PaymentMethod;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.service.OrderService;
import com.minari.ecommerce.service.ShoppingCartService;
import com.minari.ecommerce.service.UserService;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class WebOrderController {

    private final OrderService orderService;
    private final ShoppingCartService cartService;
    private final UserRepository userRepository;

    public WebOrderController(OrderService orderService, ShoppingCartService cartService,
            UserRepository userRepository) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String checkout(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        var cart = cartService.getCartForUser(email);
        if (cart.getItems().isEmpty()) {
            return "redirect:/products";
        }

        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("subtotal", cart.getTotalAmount());
        model.addAttribute("shippingCost", 0.0); // Simple logic for now, standard fee can be added
        model.addAttribute("total", cart.getTotalAmount()); // + shipping

        // Pass addresses for the "Shipping to" section logic
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            model.addAttribute("addresses", ((com.minari.ecommerce.entity.Customer) user).getSavedAddresses());
        } else {
            model.addAttribute("addresses", List.of());
        }

        return "orders/checkout";
    }

    @GetMapping("/payment-method")
    public String paymentMethod() {
        return "orders/payment-method";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
            @RequestParam("shipping_address") String street,
            @RequestParam("shipping_city") String city,
            @RequestParam("shipping_postal_code") String postalCode,
            @RequestParam("payment_method") String paymentMethodStr) {
        if (authentication == null)
            return "redirect:/login";

        String email = authentication.getName();

        // Construct Address
        Address address = new Address();
        address.setStreetAddress(street);
        address.setCity(city);
        address.setZipcode(postalCode);
        address.setCountry("Indonesia"); // Default
        address.setRecipientName("Recipient"); // Should strictly come from form too, but reusing fields for now

        // Map Payment Method
        PaymentMethod method = PaymentMethod.COD;
        if ("bank_transfer".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.BANK_TRANSFER;
        else if ("e_wallet".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.E_WALLET;
        else if ("credit_card".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.CREDIT_CARD;

        try {
            orderService.createOrderFromCart(email, address, method);
        } catch (Exception e) {
            // In a real app, pass error to flash attributes
            e.printStackTrace();
            return "redirect:/checkout?error=" + e.getMessage();
        }

        return "redirect:/products"; // Or confirmation page
    }
}
