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
    private final com.minari.ecommerce.repository.AddressRepository addressRepository;

    public WebOrderController(OrderService orderService, ShoppingCartService cartService,
            UserRepository userRepository, com.minari.ecommerce.repository.AddressRepository addressRepository) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @GetMapping
    public String checkout(Authentication authentication, Model model) {
        // Redundant if /payment is used, but keeping for direct access
        if (authentication == null) return "redirect:/login";
        return "redirect:/payment"; 
    }

    @GetMapping("/payment-method")
    public String paymentMethod() {
        return "orders/payment-method";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
            @RequestParam(value = "selected_address_id", required = false) Long addressId,
            @RequestParam(value = "shipping_address", required = false) String street,
            @RequestParam(value = "shipping_city", required = false) String city,
            @RequestParam(value = "shipping_postal_code", required = false) String postalCode,
            @RequestParam("payment_method") String paymentMethodStr) {
        
        if (authentication == null) return "redirect:/login";

        String email = authentication.getName();
        Address address;

        // Use selected address if available
        if (addressId != null) {
            address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Address not found"));
        } else {
            // Construct Address from manual input
             if (street == null || city == null || postalCode == null) {
                // Should return error to view
                return "redirect:/payment?error=Please select or enter an address";
            }
            address = new Address();
            address.setStreetAddress(street);
            address.setCity(city);
            address.setZipcode(postalCode);
            address.setCountry("Indonesia"); 
            address.setRecipientName("Recipient"); 
        }

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
            e.printStackTrace();
            return "redirect:/payment?error=" + e.getMessage();
        }

        return "redirect:/products"; 
    }
}
