package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.service.ProductService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(@RequestParam(required = false) String search,
            @RequestParam(required = false) Long category,
            Model model) {
        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("searchQuery", search);
        } else if (category != null) {
            products = productService.getProductsByCategory(category);
            productService.getCategoryById(category)
                    .ifPresent(c -> model.addAttribute("selectedCategory", c.getName()));
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("pageTitle", "Products - Minari");

        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isEmpty()) {
            return "redirect:/products";
        }

        model.addAttribute("product", product.get());
        model.addAttribute("pageTitle", product.get().getName() + " - Minari");

        return "products/detail";
    }
}