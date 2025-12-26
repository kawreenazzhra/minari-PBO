package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductReview;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.repository.OrderRepository;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.ProductReviewRepository;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReviewController {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ReviewController(ProductReviewRepository reviewRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/reviews/rate/{orderNumber}")
    public String rateOrderPage(@PathVariable String orderNumber,
                                Authentication authentication,
                                Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (!(user instanceof Customer)) {
            return "redirect:/orders/history";
        }
        
        Customer customer = (Customer) user;
        
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if (order == null) {
             return "redirect:/orders/history";
        }
        
        // Find existing reviews by this customer for products in this order
        // Find existing reviews by this customer for products in this order
        Map<Long, ProductReview> existingReviews = new HashMap<>();
        
        List<ProductReview> customerReviews = reviewRepository.findByCustomerId(customer.getId()); 
        
        for (ProductReview r : customerReviews) {
            existingReviews.put(r.getProduct().getId(), r);
        }

        model.addAttribute("order", order);
        model.addAttribute("reviewsMap", existingReviews);
        
        return "reviews/rate";
    }

    @PostMapping("/reviews/add")
    public String addReview(Authentication authentication,
                            @RequestParam Long productId,
                            @RequestParam String orderNumber,
                            @RequestParam Integer rating,
                            @RequestParam String reviewText,
                            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (!(user instanceof Customer)) {
            redirectAttributes.addFlashAttribute("error", "Only customers can write reviews.");
            return "redirect:/orders/history";
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/orders/history";
        }

        Customer customer = (Customer) user;
        
        // Save Review
        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setIsVerifiedPurchase(true); 
        review.setReviewTitle("Review for " + product.getName());

        reviewRepository.save(review);
        
        // Update product stats
        product.addReview(review);
        productRepository.save(product);

        redirectAttributes.addFlashAttribute("success", "Review submitted!");
        
        // Redirect back to the rating page for this order
        return "redirect:/reviews/rate/" + orderNumber;
    }
}
