package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.ProductReview;
import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.service.OrderService;
import com.minari.ecommerce.repository.ProductReviewRepository;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/my-orders")
public class CustomerOrderController {

    private final OrderService orderService;
    private final ProductReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public CustomerOrderController(OrderService orderService,
            ProductReviewRepository reviewRepository,
            UserRepository userRepository) {
        this.orderService = orderService;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Order order = orderService.getOrderEntityById(id);

        if (order == null) {
            return "redirect:/order-history?error=Order not found";
        }

        // Verify order belongs to this user
        if (order.getUser() == null || !order.getUser().getEmail().equals(email)) {
            return "redirect:/order-history?error=Unauthorized access";
        }

        model.addAttribute("order", order);
        return "orders/customer-detail";
    }

    @PostMapping("/{id}/review")
    public String submitReview(@PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String reviewTitle,
            @RequestParam String reviewText,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Order order = orderService.getOrderEntityById(id);

        if (order == null) {
            return "redirect:/order-history?error=Order not found";
        }

        // Verify order belongs to this user and is delivered
        if (order.getUser() == null || !order.getUser().getEmail().equals(email)) {
            return "redirect:/order-history?error=Unauthorized access";
        }

        if (!order.getStatus().name().equals("DELIVERED")) {
            return "redirect:/my-orders/" + id + "?error=Can only review delivered orders";
        }

        // Get customer
        Customer customer = (Customer) userRepository.findByEmail(email).orElse(null);
        if (customer == null) {
            return "redirect:/my-orders/" + id + "?error=Customer not found";
        }

        // Create review for each product in the order
        for (var item : order.getItems()) {
            if (item.getProduct() != null) {
                ProductReview review = new ProductReview();
                review.setProduct(item.getProduct());
                review.setCustomer(customer);
                review.setRating(rating);
                review.setReviewTitle(reviewTitle);
                review.setReviewText(reviewText);
                review.setIsVerifiedPurchase(true);
                review.setIsApproved(false); // Needs admin approval

                reviewRepository.save(review);
            }
        }

        return "redirect:/my-orders/" + id + "?success=Thank you for your feedback!";
    }
}
