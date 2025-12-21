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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class WebOrderController {

    private static final Logger log = LoggerFactory.getLogger(WebOrderController.class);

    private final OrderService orderService;
    private final ShoppingCartService cartService;
    private final UserRepository userRepository;
    private final com.minari.ecommerce.repository.AddressRepository addressRepository;
    private final com.minari.ecommerce.service.PromotionService promotionService;

    public WebOrderController(OrderService orderService, ShoppingCartService cartService,
            UserRepository userRepository, com.minari.ecommerce.repository.AddressRepository addressRepository,
            com.minari.ecommerce.service.PromotionService promotionService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.promotionService = promotionService;
    }

    @GetMapping
    public String checkout(Authentication authentication,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam(value = "paymentMethod", required = false) String paymentMethodStr,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "selectedItems", required = false) String selectedItemsParam,
            jakarta.servlet.http.HttpSession session,
            Model model) {
        if (authentication == null)
            return "redirect:/login";
        String email = authentication.getName();

        // 1. Get Cart (with filtering if selected items provided)
        com.minari.ecommerce.entity.ShoppingCart cart = null;

        // Parse selected items from comma-separated string
        java.util.List<Long> selectedProductIds = new java.util.ArrayList<>();

        // First, check if selectedItems param was provided in this request
        if (selectedItemsParam != null && !selectedItemsParam.isEmpty()) {
            try {
                String[] ids = selectedItemsParam.split(",");
                for (String id : ids) {
                    selectedProductIds.add(Long.parseLong(id.trim()));
                }
                // Save to session for persistence across checkout steps
                session.setAttribute("checkoutSelectedItems", selectedProductIds);
            } catch (Exception e) {
                log.warn("Failed to parse selectedItems: {}", e.getMessage());
            }
        } else {
            // Try to get from session if not in request param
            @SuppressWarnings("unchecked")
            java.util.List<Long> sessionItems = (java.util.List<Long>) session.getAttribute("checkoutSelectedItems");
            if (sessionItems != null) {
                selectedProductIds = sessionItems;
            }
        }

        // Get filtered or full cart
        if (!selectedProductIds.isEmpty()) {
            cart = cartService.getFilteredCart(email, selectedProductIds);
        } else {
            // No selected items, use full cart
            cart = cartService.getCartForUser(email);
        }

        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        // Validate cart items have products
        cart.getItems().stream()
                .filter(item -> item.getProduct() == null)
                .forEach(item -> {
                    throw new RuntimeException("Cart item missing product reference");
                });

        // Calculate discount
        double discountAmount = promotionService.calculateCartDiscount(cart);
        model.addAttribute("discountAmount", discountAmount);

        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotalAmount()); // shipping calc can be added later

        // 2. Resolve Address
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        List<Address> addresses = List.of();
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            addresses = addressRepository.findByCustomer((com.minari.ecommerce.entity.Customer) user);
        }

        Address selectedAddress = null;
        if (addressId != null) {
            selectedAddress = addressRepository.findById(addressId).orElse(null);
            if (selectedAddress == null) {
                model.addAttribute("error", "Selected address not found");
            }
        } else if (!addresses.isEmpty()) {
            selectedAddress = addresses.get(0); // Default to first
        }
        model.addAttribute("shippingAddress", selectedAddress);
        model.addAttribute("addresses", addresses);

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

        // Add error if present
        if (error != null && !error.isBlank()) {
            model.addAttribute("error", error);
        }

        return "checkout/summary";
    }

    @GetMapping("/address")
    public String selectAddress(Authentication authentication,
            @RequestParam(value = "selectedItems", required = false) String selectedItems,
            Model model) {
        if (authentication == null)
            return "redirect:/login";
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        List<Address> addresses = List.of();
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            addresses = addressRepository.findSavedAddressesByCustomer((com.minari.ecommerce.entity.Customer) user);
        }
        model.addAttribute("addresses", addresses);
        if (selectedItems != null) {
            model.addAttribute("selectedItems", selectedItems);
        }
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
        if (authentication == null)
            return "redirect:/login";

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
            @RequestParam(value = "selectedItems", required = false) String selectedItems,
            Model model) {
        if (authentication == null)
            return "redirect:/login";

        // Validate that address was selected
        if (addressId == null || addressId <= 0) {
            return "redirect:/checkout?error=Please select an address first";
        }

        // Verify address exists
        Address address = addressRepository.findById(addressId).orElse(null);
        if (address == null) {
            return "redirect:/checkout?error=Selected address not found";
        }

        model.addAttribute("addressId", addressId);
        if (selectedItems != null) {
            model.addAttribute("selectedItems", selectedItems);
        }
        return "checkout/payment_selection";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam("payment_method") String paymentMethodStr,
            @RequestParam(value = "selectedItems", required = false) String selectedItemsParam,
            jakarta.servlet.http.HttpSession session) {

        if (authentication == null)
            return "redirect:/login";

        String email = authentication.getName();

        // Parse selected items if provided
        java.util.List<Long> selectedProductIds = new java.util.ArrayList<>();
        if (selectedItemsParam != null && !selectedItemsParam.isEmpty()) {
            try {
                String[] ids = selectedItemsParam.split(",");
                for (String id : ids) {
                    selectedProductIds.add(Long.parseLong(id.trim()));
                }
            } catch (Exception e) {
                log.warn("Failed to parse selectedItems in placeOrder: {}", e.getMessage());
            }
        }

        // Validate address is provided
        if (addressId == null || addressId <= 0) {
            return "redirect:/checkout?error=Please select a shipping address";
        }

        // Fetch and validate address exists
        Address address = addressRepository.findById(addressId)
                .orElse(null);

        if (address == null) {
            return "redirect:/checkout?error=Shipping address not found";
        }

        // Validate payment method
        PaymentMethod method = PaymentMethod.COD;
        if ("bank_transfer".equalsIgnoreCase(paymentMethodStr)) {
            method = PaymentMethod.BANK_TRANSFER;
        } else if ("e_wallet".equalsIgnoreCase(paymentMethodStr)) {
            method = PaymentMethod.E_WALLET;
        } else if (!"cod".equalsIgnoreCase(paymentMethodStr) && !"".equals(paymentMethodStr)) {
            return "redirect:/checkout?addressId=" + addressId + "&error=Invalid payment method selected";
        }

        try {
            // Get filtered cart if selected items provided, otherwise full cart
            com.minari.ecommerce.entity.ShoppingCart cart;
            if (!selectedProductIds.isEmpty()) {
                cart = cartService.getFilteredCart(email, selectedProductIds);
            } else {
                cart = cartService.getCartForUser(email);
            }

            if (cart == null || cart.getItems().isEmpty()) {
                return "redirect:/cart?error=Your cart is empty";
            }

            // Create the order from the filtered/full cart
            com.minari.ecommerce.entity.Order savedOrder = orderService.createOrderFromCart(email, address, method,
                    cart, selectedProductIds);

            if (savedOrder == null || savedOrder.getOrderNumber() == null) {
                return "redirect:/checkout?addressId=" + addressId
                        + "&error=Order creation failed - order number not generated";
            }

            // Clear checkout session after successful order
            session.removeAttribute("checkoutSelectedItems");

            return "redirect:/checkout/success?orderNumber=" + savedOrder.getOrderNumber();
        } catch (RuntimeException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage == null) {
                errorMessage = "Order creation failed";
            }
            // Sanitize error message for URL
            errorMessage = errorMessage.replaceAll("[^a-zA-Z0-9 :_.-]", "");
            if (errorMessage.length() > 100) {
                errorMessage = errorMessage.substring(0, 100);
            }

            return "redirect:/checkout?addressId=" + addressId + "&error=" + errorMessage;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error during order placement: " + e.getMessage());
            String errorMessage = "Unexpected error: " + e.getClass().getSimpleName();
            return "redirect:/checkout?addressId=" + addressId + "&error=" + errorMessage;
        }
    }

    @GetMapping("/success")
    public String orderSuccess(@RequestParam("orderNumber") String orderNumber, Authentication authentication,
            Model model) {
        if (authentication == null)
            return "redirect:/login";
        model.addAttribute("email", authentication.getName());
        model.addAttribute("orderNumber", orderNumber);
        return "orders/success";
    }
}
