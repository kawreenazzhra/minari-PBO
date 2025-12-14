package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Order;
import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.dto.ViewUser;
import com.minari.ecommerce.dto.OrderDTO;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.service.CatalogService;
import com.minari.ecommerce.service.DashboardService;
import com.minari.ecommerce.service.FileUploadService;
import com.minari.ecommerce.service.OrderService;
import com.minari.ecommerce.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final DashboardService dashboardService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CatalogService catalogService;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    
    public AdminController(DashboardService dashboardService,
                          ProductService productService,
                          OrderService orderService,
                          CatalogService catalogService,
                          UserRepository userRepository,
                          FileUploadService fileUploadService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
        this.orderService = orderService;
        this.catalogService = catalogService;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
    }
    
    @GetMapping("")
    public String adminRedirect() {
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        dashboardService.showAdminDashboard();
        
        // Get current user from security context and load from repository
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : null;

        ViewUser viewUser = new ViewUser();
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(u -> {
                String fullName = u.getFullName() != null ? u.getFullName() : "Admin";
                String firstName = fullName.split(" ")[0];
                String lastName = "";
                String[] parts = fullName.split(" ");
                if (parts.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        if (sb.length() > 0) sb.append(' ');
                        sb.append(parts[i]);
                    }
                    lastName = sb.toString();
                }
                viewUser.setFirstName(firstName);
                viewUser.setLastName(lastName);
                viewUser.setEmail(u.getEmail());
            });
        }

        // Fallback if user not found
        if (viewUser.getFirstName() == null) {
            viewUser.setFirstName("Admin");
            viewUser.setLastName("");
            viewUser.setEmail(email != null ? email : "");
        }

        model.addAttribute("currentUser", viewUser);
        
        return "admin/dashboard-oop";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        List<ProductCategory> categories = catalogService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("product", new Product());
        return "admin/products";
    }

    
    @PostMapping("/products")
    public String addProduct(@ModelAttribute Product product) {
        // Set the category using CatalogService
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            ProductCategory category = catalogService.getCategoryById(product.getCategory().getId());
            product.setCategory(category);
        }
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/create")
    public String createProduct(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam Double price,
                                @RequestParam(required = false) Double discountPrice,
                                @RequestParam(required = false) Double compareAtPrice,
                                @RequestParam Integer stockQuantity,
                                @RequestParam Long categoryId,
                                @RequestParam(required = false) String brand,
                                @RequestParam(required = false) String sku,
                                @RequestParam(required = false) Boolean isFeatured,
                                @RequestParam(required = false) Boolean isActive,
                                @RequestParam(required = false) MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setDiscountPrice(discountPrice);
            product.setCompareAtPrice(compareAtPrice);
            product.setStockQuantity(stockQuantity);
            product.setBrand(brand);
            product.setSku(sku);
            product.setIsFeatured(isFeatured != null && isFeatured);
            product.setIsActive(isActive != null ? isActive : true);

            // Set category
            ProductCategory category = catalogService.getCategoryById(categoryId);
            if (category == null) {
                redirectAttributes.addFlashAttribute("error", "Category not found");
                return "redirect:/admin/products";
            }
            product.setCategory(category);

            // Handle image upload
            if (image != null && !image.isEmpty()) {
                String imagePath = fileUploadService.uploadProductImage(image);
                product.setImageUrl(imagePath);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product created successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/update")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam Double price,
                                @RequestParam(required = false) Double discountPrice,
                                @RequestParam(required = false) Double compareAtPrice,
                                @RequestParam Integer stockQuantity,
                                @RequestParam Long categoryId,
                                @RequestParam(required = false) String brand,
                                @RequestParam(required = false) String sku,
                                @RequestParam(required = false) Boolean isFeatured,
                                @RequestParam(required = false) Boolean isActive,
                                @RequestParam(required = false) MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id).orElse(null);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Product not found");
                return "redirect:/admin/products";
            }

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setDiscountPrice(discountPrice);
            product.setCompareAtPrice(compareAtPrice);
            product.setStockQuantity(stockQuantity);
            product.setBrand(brand);
            product.setSku(sku);
            product.setIsFeatured(isFeatured != null && isFeatured);
            product.setIsActive(isActive != null ? isActive : true);

            // Set category
            ProductCategory category = catalogService.getCategoryById(categoryId);
            if (category == null) {
                redirectAttributes.addFlashAttribute("error", "Category not found");
                return "redirect:/admin/products";
            }
            product.setCategory(category);

            // Handle image upload
            if (image != null && !image.isEmpty()) {
                // Delete old image if exists
                if (product.getImageUrl() != null) {
                    fileUploadService.deleteFile(product.getImageUrl());
                }
                String imagePath = fileUploadService.uploadProductImage(image);
                product.setImageUrl(imagePath);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id).orElse(null);
            if (product != null) {
                // Delete image if exists
                if (product.getImageUrl() != null) {
                    fileUploadService.deleteFile(product.getImageUrl());
                }
                productService.deleteProduct(id);
                redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete image: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
    @GetMapping("/orders")
    public String orderManagement(Model model) {
        List<OrderDTO> orders = orderService.getAllOrders("", "");
        model.addAttribute("orders", orders);
        return "admin/orders-oop";
    }

    @GetMapping("/products")
    public String productManagement(Model model) {
        List<Product> products = productService.getAllProducts();
        List<ProductCategory> categories = catalogService.getAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "admin/products";
    }

    @GetMapping("/customers")
    public String customerManagement(Model model) {
        return "admin/customers-oop";
    }
}