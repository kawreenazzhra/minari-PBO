package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Promotion;
import com.minari.ecommerce.service.PromotionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public String listPromotions(Model model) {
        // For simplicity, we'll just show a form to add promotion
        model.addAttribute("promotion", new Promotion());
        return "admin/promotions-oop";
    }

    @PostMapping
    public String addPromotion(@ModelAttribute Promotion promotion) {
        promotionService.savePromotion(promotion);
        return "redirect:/admin/promotions";
    }
}