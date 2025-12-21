package com.minari.ecommerce.controller;

import com.minari.ecommerce.service.ProductCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final ProductCategoryService categoryService;

    public MenuController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String viewMenu(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "menu/view";
    }
}
