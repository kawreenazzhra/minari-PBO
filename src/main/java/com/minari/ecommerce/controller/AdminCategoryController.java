package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.service.ProductCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    
    private final ProductCategoryService categoryService;
    
    public AdminCategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping
    public String listCategories(
            @RequestParam(required = false) String search,
            Model model) {
        
        List<ProductCategory> categories = categoryService.getAllCategories();
        
        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        model.addAttribute("totalCategories", categoryService.getTotalCategories());
        model.addAttribute("activeCategories", categoryService.getActiveCategories_count());
        
        return "admin/categories";
    }
    
    @GetMapping("/add")
    public String addCategoryForm() {
        return "admin/add-category-oop";
    }
}
