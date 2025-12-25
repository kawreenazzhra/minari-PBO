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
                            @RequestParam(name = "addressId", required = false) Long addressId,
                            @RequestParam(name = "paymentMethod", required = false) String paymentMethod,
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

        // Add selected address logic
        if (user instanceof Customer) {
            List<com.minari.ecommerce.entity.Address> addresses = ((Customer) user).getSavedAddresses();
            model.addAttribute("addresses", addresses);
            
            if (addressId != null) {
                // Find selected
                 model.addAttribute("selectedAddress", addresses.stream().filter(a -> a.getId().equals(addressId)).findFirst().orElse(null));
            }
            // Removed default selection to respect user choice
        } else {
            model.addAttribute("addresses", List.of());
        }
        
        // Pass addressId directly for link generation
        model.addAttribute("addressId", addressId);
        
        // Selected Payment Method - NO DEFAULT
        model.addAttribute("selectedPaymentMethod", paymentMethod);

        // Cart summary
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("subtotal", cart.getTotalAmount());
        model.addAttribute("total", cart.getTotalAmount());
        
        return "checkout/summary"; 
    }

    @GetMapping("/view")
    public String paymentViewPage(@RequestParam(name = "orderNumber", required = false) String orderNumber,
                                  @RequestParam(name = "addressId", required = false) Long addressId,
                                  @RequestParam(name = "paymentMethod", required = false) String paymentMethod,
                                  Model model,
                                  Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        model.addAttribute("orderNumber", orderNumber != null ? orderNumber : "Processing...");
        model.addAttribute("addressId", addressId);
        model.addAttribute("selectedPaymentMethod", paymentMethod);
        
        return "payment/view"; 
    }
}
