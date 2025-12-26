package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.service.ProductService;
import com.minari.ecommerce.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String home(@RequestParam(required = false) String logout, Model model) {
        // Handle logout success message
        if ("success".equals(logout)) {
            model.addAttribute("logoutSuccess", true);
        }
        
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
        // Used for the navbar menu - mapped to avoid serialization issues
        List<java.util.Map<String, Object>> navCategoryMaps = productService.getAllCategories().stream()
                .filter(cat -> cat.getIsActive() != null && cat.getIsActive())
                .map(cat -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", cat.getId());
                    map.put("name", cat.getName());
                    map.put("imageUrl", cat.getImageUrl());
                    return map;
                })
                .collect(Collectors.toList());
        model.addAttribute("navCategories", navCategoryMaps);
        
        // Add active promotions with enriched category information
        promotionService.updateExpiredPromotions();
        List<com.minari.ecommerce.entity.Promotion> promotions = promotionService.getActivePromotions();
        
        // Enrich promotions with category names
        List<java.util.Map<String, Object>> enrichedPromotions = promotions.stream()
                .map(promo -> {
                    java.util.Map<String, Object> promoMap = new java.util.HashMap<>();
                    promoMap.put("id", promo.getId());
                    promoMap.put("name", promo.getName());
                    promoMap.put("description", promo.getDescription());
                    promoMap.put("promoCode", promo.getPromoCode());
                    promoMap.put("discountType", promo.getDiscountType());
                    promoMap.put("discountValue", promo.getDiscountValue());
                    promoMap.put("minPurchaseAmount", promo.getMinPurchaseAmount());
                    promoMap.put("endDate", promo.getEndDate());
                    
                    // Parse applicable categories and get category names
                    List<String> categoryNames = new java.util.ArrayList<>();
                    if (promo.getApplicableCategories() != null && !promo.getApplicableCategories().trim().isEmpty()) {
                        try {
                            // Parse JSON array of category IDs
                            String categoriesJson = promo.getApplicableCategories().trim();
                            if (categoriesJson.startsWith("[") && categoriesJson.endsWith("]")) {
                                categoriesJson = categoriesJson.substring(1, categoriesJson.length() - 1);
                                String[] categoryIds = categoriesJson.split(",");
                                for (String idStr : categoryIds) {
                                    try {
                                        Long categoryId = Long.parseLong(idStr.trim().replace("\"", ""));
                                        productService.getAllCategories().stream()
                                                .filter(cat -> cat.getId().equals(categoryId))
                                                .findFirst()
                                                .ifPresent(cat -> categoryNames.add(cat.getName()));
                                    } catch (NumberFormatException e) {
                                        // Skip invalid IDs
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // If parsing fails, leave categoryNames empty
                        }
                    }
                    promoMap.put("categoryNames", categoryNames);
                    
                    return promoMap;
                })
                .collect(Collectors.toList());
        
        model.addAttribute("promotions", enrichedPromotions);

        model.addAttribute("pageTitle", "MINARI - Fashion E-Commerce");

        return "home";
    }
}