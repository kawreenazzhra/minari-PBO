package com.minari.ecommerce.controller;

import com.minari.ecommerce.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * GET /wishlist - Display wishlist page
     */
    @GetMapping
    public String viewWishlist(Authentication authentication, Model model) {
        // If user is authenticated, data will be loaded via AJAX from /api/wishlist
        // If not authenticated, wishlist.js will handle guest mode
        
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("email", authentication.getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        return "profile/wishlist";
    }
}
