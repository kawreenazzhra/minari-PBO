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
import java.util.Arrays;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public String paymentPage(@RequestParam(name = "items", required = false) String itemsParam,
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

        // Filter items logic
        List<com.minari.ecommerce.entity.CartItem> itemsToProcess = cart.getItems();
        String selectedItemsJson = ""; // Default empty string or null

        if (itemsParam != null && !itemsParam.isEmpty()) {
            try {
                // Parse comma-separated IDs from URL
                List<Long> selectedIds = Arrays.stream(itemsParam.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
                
                // Filter items
                itemsToProcess = cart.getItems().stream()
                    .filter(item -> selectedIds.contains(item.getProduct().getId()))
                    .collect(Collectors.toList());
                
                // Generate JSON for hidden input
                ObjectMapper mapper = new ObjectMapper();
                selectedItemsJson = mapper.writeValueAsString(selectedIds);
                
            } catch (Exception e) {
                // Return to full cart in case of error
                System.err.println("Error filtering cart items: " + e.getMessage());
            }
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

        // Prepare context for calculation
        // If we filtered items, we need a view/temp cart object for calculation because calculateBestDiscount uses cart.getTotalAmount()
        com.minari.ecommerce.entity.ShoppingCart calculationCart = cart;
        if (itemsToProcess.size() != cart.getItems().size()) {
            calculationCart = new com.minari.ecommerce.entity.ShoppingCart();
            calculationCart.setItems(itemsToProcess);
            // getTotalAmount() calculates on the fly based on items list
        }

        // Calculate discount
        DiscountCalculation discountCalc = promotionService.calculateBestDiscount(calculationCart);
        double subtotal = calculationCart.getTotalAmount();
        double discountAmount = discountCalc.getDiscountAmount();
        double shippingFee = 15000.0;
        double finalTotal = Math.max(0, (subtotal - discountAmount) + shippingFee);

        // Cart summary
        model.addAttribute("cartItems", itemsToProcess);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("appliedPromotion", discountCalc.getAppliedPromotion());
        model.addAttribute("total", finalTotal);
        model.addAttribute("selectedItemsJson", selectedItemsJson);
        model.addAttribute("items", itemsParam);
        
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
