package com.minari.ecommerce.controller;

import com.minari.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            return "search/start";
        }

        model.addAttribute("query", query);
        // Assuming ProductService has a search method
        model.addAttribute("products", productService.searchProducts(query));
        return "search/view";
    }
}
