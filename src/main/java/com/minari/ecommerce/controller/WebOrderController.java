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
    public String checkout(Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        return "redirect:/checkout/address";
    }

    @GetMapping("/address")
    public String selectAddress(Authentication authentication, 
                              @RequestParam(value = "addressId", required = false) Long currentAddressId,
                              @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                              Model model) {
        if (authentication == null) return "redirect:/login";
        
        model.addAttribute("paymentMethod", paymentMethod);
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        
        List<Address> addresses = List.of();
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            addresses = addressRepository.findByCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        
        model.addAttribute("addresses", addresses);
        
        if (currentAddressId != null) {
             model.addAttribute("selectedAddressId", currentAddressId);
        }
        // Removed default selection to respect user choice (or lack thereof)
        
        return "checkout/address";
    }

    @PostMapping("/address/add")
    public String addAddress(Authentication authentication,
                             @RequestParam("recipientName") String recipientName,
                             @RequestParam("phone") String phone,
                             @RequestParam("streetAddress") String streetAddress,
                             @RequestParam("city") String city,
                             @RequestParam("zipcode") String zipcode,
                             @RequestParam("country") String country,
                             @RequestParam(value = "addressType", required = false, defaultValue = "Home") String addressType,
                             @RequestParam(value = "paymentMethod", required = false) String paymentMethod) {
        if (authentication == null) return "redirect:/login";
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        Address address = new Address();
        address.setRecipientName(recipientName);
        address.setPhoneNumber(phone);
        address.setStreetAddress(streetAddress);
        address.setCity(city);
        address.setZipcode(zipcode);
        address.setCountry(country);
        address.setAddressType(addressType);
        
        // Default values
        address.setState(city); 
        address.setProvince(city); 
        
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            address.setCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        
        Address savedAddress = addressRepository.save(address);
        
        String redirectUrl = "redirect:/payment?addressId=" + savedAddress.getId();
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            redirectUrl += "&paymentMethod=" + paymentMethod;
        }
        return redirectUrl;
    }

    @GetMapping("/payment")
    public String paymentMethod(Authentication authentication, 
                              @RequestParam(value = "addressId", required = false) Long addressId,
                              @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                              Model model) {
        if (authentication == null) return "redirect:/login";
        
        model.addAttribute("addressId", addressId);
        model.addAttribute("selectedPaymentMethod", paymentMethod);
        
        return "checkout/payment";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam(value = "payment_method", required = false) String paymentMethodStr) {
        
        if (authentication == null) return "redirect:/login";
        
        if (addressId == null) {
            return "redirect:/checkout?error=missing_address";
        }

        if (paymentMethodStr == null || paymentMethodStr.isEmpty()) {
            return "redirect:/checkout?error=missing_payment";
        }

        String email = authentication.getName();
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Map Payment Method
        PaymentMethod method = PaymentMethod.COD;
        if ("bank_transfer".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.BANK_TRANSFER;
        else if ("e_wallet".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.E_WALLET;
        else if ("credit_card".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.CREDIT_CARD;

        try {
            System.out.println("Creating order for user: " + email);
            com.minari.ecommerce.entity.Order savedOrder = orderService.createOrderFromCart(email, address, method);
            System.out.println("Order created successfully: " + savedOrder.getOrderNumber());
            System.out.println("Cart should be cleared now");
            return "redirect:/payment/view?orderNumber=" + savedOrder.getOrderNumber() + 
                   "&addressId=" + addressId + "&paymentMethod=" + paymentMethodStr;
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage()); // Keep sysout just in case
            e.printStackTrace();
            // Use logger if available (assuming class has Slf4j or similar, if not, relying on sysout/printstacktrace)
            // But redirect with params to keep selection
            return "redirect:/checkout?error=creation_failed&addressId=" + addressId + "&paymentMethod=" + paymentMethodStr;
        }
    }

    @GetMapping("/success")
    public String orderSuccess(@RequestParam("orderNumber") String orderNumber, Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        model.addAttribute("email", authentication.getName());
        model.addAttribute("orderNumber", orderNumber);
        return "orders/success";
    }
}
