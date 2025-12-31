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
                              @RequestParam(value = "items", required = false) String items,
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
        
        // Pass items param to view so it can be preserved
        model.addAttribute("items", items);
        
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
                             @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                             @RequestParam(value = "items", required = false) String items) {
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
        if (items != null && !items.isEmpty()) {
            redirectUrl += "&items=" + items;
        }
        return redirectUrl;
    }

    @GetMapping("/payment")
    public String paymentMethod(Authentication authentication, 
                              @RequestParam(value = "addressId", required = false) Long addressId,
                              @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                              @RequestParam(value = "items", required = false) String items,
                              Model model) {
        if (authentication == null) return "redirect:/login";
        
        model.addAttribute("addressId", addressId);
        model.addAttribute("selectedPaymentMethod", paymentMethod);
        model.addAttribute("items", items);
        
        return "checkout/payment";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam(value = "payment_method", required = false) String paymentMethodStr,
            @RequestParam(value = "selectedItems", required = false) String selectedItemsJson) {
        
        if (authentication == null) return "redirect:/login";
        
        System.out.println("DEBUG: placeOrder called");
        System.out.println("DEBUG: addressId = " + addressId);
        System.out.println("DEBUG: paymentMethodStr = " + paymentMethodStr);
        System.out.println("DEBUG: selectedItems = " + selectedItemsJson);
        
        if (addressId == null) {
            return "redirect:/payment?error=missing_address";
        }

        if (paymentMethodStr == null || paymentMethodStr.isEmpty()) {
            return "redirect:/payment?error=missing_payment&addressId=" + addressId;
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

        // Parse selected item IDs
        java.util.List<Long> selectedProductIds = null;
        if (selectedItemsJson != null && !selectedItemsJson.isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                selectedProductIds = mapper.readValue(selectedItemsJson, 
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.List<Long>>() {});
            } catch (Exception e) {
                System.err.println("Error parsing selected items: " + e.getMessage());
            }
        }

        try {
            System.out.println("Creating order for user: " + email);
            com.minari.ecommerce.entity.Order savedOrder = orderService.createOrderFromCart(email, address, method, selectedProductIds);
            System.out.println("Order created successfully: " + savedOrder.getOrderNumber());
            System.out.println("Cart should be cleared now");
            return "redirect:/checkout/order-confirm?orderNumber=" + savedOrder.getOrderNumber() + 
               "&paymentMethod=" + paymentMethodStr;
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage()); // Keep sysout just in case
            e.printStackTrace();
            // Redirect back to /payment instead of /checkout to avoid address redirect loop
            return "redirect:/payment?error=creation_failed&addressId=" + addressId + "&paymentMethod=" + paymentMethodStr;
        }
    }

    @GetMapping("/order-confirm")
    public String orderSuccess(@RequestParam("orderNumber") String orderNumber,
                              @RequestParam(value = "paymentMethod", required = false) String paymentMethodStr,
                              Authentication authentication, 
                              Model model) {
        if (authentication == null) return "redirect:/login";
        
        // Fetch order details using DTO
        com.minari.ecommerce.dto.OrderDTO orderDTO = orderService.getOrderByNumber(orderNumber);
        
        if (orderDTO == null) {
            return "redirect:/orders/history?error=order_not_found";
        }
        
        model.addAttribute("email", authentication.getName());
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("order", orderDTO);
        
        // Determine payment status message based on DTO
        String paymentStatusMessage;
        String paymentStatus = "UNKNOWN";
        
        if (orderDTO.getPaymentStatus() != null) {
            paymentStatus = orderDTO.getPaymentStatus();
            if ("PAID".equalsIgnoreCase(paymentStatus)) {
                paymentStatusMessage = "Payment successful! Your order has been confirmed.";
            } else if ("PENDING".equalsIgnoreCase(paymentStatus)) {
                paymentStatusMessage = "Payment pending - Please pay on delivery.";
            } else {
                paymentStatusMessage = "Payment status: " + paymentStatus;
            }
        } else {
            paymentStatusMessage = "Payment information not available.";
        }
        
        model.addAttribute("paymentStatusMessage", paymentStatusMessage);
        model.addAttribute("paymentStatus", paymentStatus);
        
        return "checkout/success";
    }
}
