package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.service.ProductService;
import com.minari.ecommerce.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CatalogService catalogService;
    private final com.minari.ecommerce.service.PromotionService promotionService;

    public HomeController(ProductService productService, CatalogService catalogService, com.minari.ecommerce.service.PromotionService promotionService) {
        this.productService = productService;
        this.catalogService = catalogService;
        this.promotionService = promotionService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Get all products and limit to 4 featured ones
        List<Product> allProducts = productService.getAllProducts();
        List<Product> featuredProducts = allProducts.stream()
                .limit(4)
                .collect(Collectors.toList());

        // Get all active categories
        List<ProductCategory> categories = productService.getAllCategories()
                .stream()
                .filter(cat -> cat.getIsActive() != null && cat.getIsActive())
                .limit(6)
                .collect(Collectors.toList());

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("products", allProducts);
        model.addAttribute("categories", categories);
        // Used for the navbar menu
        model.addAttribute("navCategories", productService.getAllCategories().stream()
                .filter(cat -> cat.getIsActive() != null && cat.getIsActive())
                .collect(Collectors.toList()));
        
        // Add active promotions
        promotionService.updateExpiredPromotions();
        model.addAttribute("promotions", promotionService.getActivePromotions());

        model.addAttribute("pageTitle", "MINARI - Fashion E-Commerce");

        return "home";
    }
}