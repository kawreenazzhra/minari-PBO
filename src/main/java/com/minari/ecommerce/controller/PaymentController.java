package com.minari.ecommerce.controller;

import com.minari.ecommerce.dto.DiscountCalculation;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.service.PromotionService;
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
    private final PromotionService promotionService;

    public PaymentController(ShoppingCartService cartService, UserRepository userRepository, PromotionService promotionService) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.promotionService = promotionService;
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
            // No default selection - user must choose
        } else {
            model.addAttribute("addresses", List.of());
        }
        
        // Pass addressId directly for link generation
        model.addAttribute("addressId", addressId);
        
        // Selected Payment Method - NO DEFAULT
        model.addAttribute("selectedPaymentMethod", paymentMethod);

        // Calculate discount
        DiscountCalculation discountCalc = promotionService.calculateBestDiscount(cart);
        double subtotal = cart.getTotalAmount();
        double discountAmount = discountCalc.getDiscountAmount();
        double shippingFee = 15000.0;
        double finalTotal = Math.max(0, (subtotal - discountAmount) + shippingFee);

        // Cart summary
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("appliedPromotion", discountCalc.getAppliedPromotion());
        model.addAttribute("total", finalTotal);
        
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
