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
    public String checkout(Authentication authentication, 
                         @RequestParam(value = "addressId", required = false) Long addressId,
                         @RequestParam(value = "paymentMethod", required = false) String paymentMethodStr,
                         Model model) {
        if (authentication == null) return "redirect:/login";
        String email = authentication.getName();
        
        // 1. Get Cart
        com.minari.ecommerce.entity.ShoppingCart cart = cartService.getCartForUser(email);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotalAmount()); // shipping calc can be added later

        // 2. Resolve Address
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Address> addresses = List.of();
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            addresses = addressRepository.findByCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        
        Address selectedAddress = null;
        if (addressId != null) {
            selectedAddress = addressRepository.findById(addressId).orElse(null);
        } else if (!addresses.isEmpty()) {
            selectedAddress = addresses.get(0); // Default to first
        }
        model.addAttribute("shippingAddress", selectedAddress);

        // 3. Resolve Payment
        PaymentMethod selectedPayment = PaymentMethod.COD; // Default
        String paymentDisplay = "Cash on Delivery";
        if (paymentMethodStr != null) {
            if ("bank_transfer".equalsIgnoreCase(paymentMethodStr)) {
                selectedPayment = PaymentMethod.BANK_TRANSFER;
                paymentDisplay = "Virtual Account Transfer";
            } else if ("e_wallet".equalsIgnoreCase(paymentMethodStr)) {
                selectedPayment = PaymentMethod.E_WALLET;
                 paymentDisplay = "E-Wallet";
            }
        }
        model.addAttribute("paymentMethod", selectedPayment);
        model.addAttribute("paymentMethodName", paymentDisplay);
        model.addAttribute("paymentMethodValue", paymentMethodStr != null ? paymentMethodStr : "cod");

        return "checkout/summary";
    }

    @GetMapping("/address")
    public String selectAddress(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        
        List<Address> addresses = List.of();
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            addresses = addressRepository.findByCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        model.addAttribute("addresses", addresses);
        return "checkout/address_selection";
    }
    
    // Kept for backward compatibility if needed or removed if unused
    @PostMapping("/address/add")
    public String addAddress(Authentication authentication,
                             @RequestParam("recipientName") String recipientName,
                             @RequestParam("phone") String phone,
                             @RequestParam("streetAddress") String streetAddress,
                             @RequestParam("city") String city,
                             @RequestParam("zipcode") String zipcode,
                             @RequestParam("country") String country) {
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
        address.setState(city); 
        address.setProvince(city); 
        address.setAddressType("SHIPPING");
        
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            address.setCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        
        Address savedAddress = addressRepository.save(address);
        
        return "redirect:/checkout?addressId=" + savedAddress.getId();
    }

    @GetMapping("/payment")
    public String paymentMethod(Authentication authentication, 
                              @RequestParam(value = "addressId", required = false) Long addressId,
                              Model model) {
        if (authentication == null) return "redirect:/login";
        model.addAttribute("addressId", addressId);
        return "checkout/payment_selection";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam("payment_method") String paymentMethodStr) {
        
        if (authentication == null) return "redirect:/login";
        
        // ... (Error handling logic same as before)
        if (addressId == null) {
             // Fallback if null, try to find default? Or error.
             return "redirect:/checkout?error=Missing address";
        }

        String email = authentication.getName();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        PaymentMethod method = PaymentMethod.COD;
        if ("bank_transfer".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.BANK_TRANSFER;
        else if ("e_wallet".equalsIgnoreCase(paymentMethodStr))
            method = PaymentMethod.E_WALLET;

        try {
            com.minari.ecommerce.entity.Order savedOrder = orderService.createOrderFromCart(email, address, method);
            return "redirect:/checkout/success?orderNumber=" + savedOrder.getOrderNumber();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Order placement error: " + e.getMessage());
            String errorMessage = "Payment Failed: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            // Sanitize
            errorMessage = errorMessage.replaceAll("[^a-zA-Z0-9 :_.-]", "");
            if (errorMessage.length() > 100) errorMessage = errorMessage.substring(0, 100);
            
            return "redirect:/checkout?addressId=" + addressId + "&error=" + errorMessage;
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
