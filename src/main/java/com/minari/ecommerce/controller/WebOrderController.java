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
    public String selectAddress(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        
        List<Address> addresses = List.of();
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            addresses = addressRepository.findByCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        // For Admin or others, list is empty -> they will see "Add Address" form
        
        model.addAttribute("addresses", addresses);
        // Pre-select the last added address if available
        if (!addresses.isEmpty()) {
            model.addAttribute("selectedAddressId", addresses.get(0).getId());
        }
        
        return "orders/shipping_address";
    }

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
        
        // Default values for required fields not in simple form
        address.setState(city); // Fallback
        address.setProvince(city); // Fallback
        address.setAddressType("SHIPPING");
        
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            address.setCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        
        Address savedAddress = addressRepository.save(address);
        
        return "redirect:/checkout/payment?addressId=" + savedAddress.getId();
    }

    @GetMapping("/payment")
    public String paymentMethod(Authentication authentication, 
                              @RequestParam(value = "addressId", required = false) Long addressId,
                              Model model) {
        if (authentication == null) return "redirect:/login";
        if (addressId == null) return "redirect:/checkout/address";
        
        model.addAttribute("addressId", addressId);
        return "orders/payment-method";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam("payment_method") String paymentMethodStr) {
        
        if (authentication == null) return "redirect:/login";
        
        if (addressId == null) {
            return "redirect:/checkout/payment?error=Missing address information";
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
            com.minari.ecommerce.entity.Order savedOrder = orderService.createOrderFromCart(email, address, method);
            return "redirect:/checkout/success?orderNumber=" + savedOrder.getOrderNumber();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Order placement error: " + e.getMessage());
            // Redirect back to payment with error
            return "redirect:/checkout/payment?addressId=" + addressId + "&error=Payment Failed Check Server Console";
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
