package com.minari.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WishlistController {

    @GetMapping("/wishlist")
    public String viewWishlist(Model model) {
        // In the future, we can add logic here to fetch wishlist items from session
        // (for guests)
        model.addAttribute("pageTitle", "My Wishlist - MINARI");
        return "wishlist/view"; // You'll need to create this template
    }
}
