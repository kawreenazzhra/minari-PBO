package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Product;
import com.minari.ecommerce.entity.ProductCategory;
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

import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CatalogService catalogService;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final com.minari.ecommerce.repository.ProductReviewRepository reviewRepository;
    private final com.minari.ecommerce.repository.PromotionRepository promotionRepository;
    private final com.minari.ecommerce.service.PromotionService promotionService;

    public AdminController(DashboardService dashboardService,
            ProductService productService,
            OrderService orderService,
            CatalogService catalogService,
            UserRepository userRepository,
            FileUploadService fileUploadService,
            com.minari.ecommerce.repository.ProductReviewRepository reviewRepository,
            com.minari.ecommerce.repository.PromotionRepository promotionRepository,
            com.minari.ecommerce.service.PromotionService promotionService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
        this.orderService = orderService;
        this.catalogService = catalogService;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.reviewRepository = reviewRepository;
        this.promotionRepository = promotionRepository;
        this.promotionService = promotionService;
    }

    @ModelAttribute
    public void addAttributes(Model model, jakarta.servlet.http.HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
    }

    // ... (rest of existing methods, but keeping them as is, only showing
    // additions/replacements for new sections)

    @GetMapping("/reviews")
    public String reviewManagement(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("reviews", reviewRepository.findByReviewTextContainingIgnoreCase(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("reviews", reviewRepository.findAll());
        }
        return "admin/reviews";
    }

    @GetMapping("/promotions")
    public String promotionManagement(@RequestParam(required = false) String keyword, Model model) {
        promotionService.updateExpiredPromotions();
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("promotions", promotionRepository.findByPromoCodeContainingIgnoreCase(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("promotions", promotionRepository.findAll());
        }
        return "admin/promotions";
    }

    @GetMapping("/promotions/add")
    public String showAddPromotionForm(Model model) {
        model.addAttribute("categories", catalogService.getAllCategories());
        model.addAttribute("products", productService.getAllProducts());
        return "admin/promotions/add";
    }

    @PostMapping("/promotions")
    public String addPromotion(@RequestParam String name,
            @RequestParam String code,
            @RequestParam String description,
            @RequestParam(required = false, defaultValue = "PERCENTAGE") String discountType,
            @RequestParam Double discountValue,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam Boolean isActive,
            @RequestParam(required = false) Integer usageLimit,
            @RequestParam(required = false) Double minPurchaseAmount,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> productIds,
            RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Promotion name is required");
                return "redirect:/admin/promotions/add";
            }
            if (code == null || code.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Promo code is required");
                return "redirect:/admin/promotions/add";
            }
            
            com.minari.ecommerce.entity.Promotion promo = new com.minari.ecommerce.entity.Promotion();
            promo.setName(name);
            promo.setPromoCode(code);
            promo.setDescription(description);
            promo.setDiscountType(com.minari.ecommerce.entity.Promotion.DiscountType.valueOf(discountType.toUpperCase()));
            promo.setDiscountValue(discountValue);
            promo.setStartDate(startDate.atStartOfDay());
            promo.setEndDate(endDate.atTime(23, 59, 59));
            promo.setIsActive(isActive);
            promo.setUsageLimit(usageLimit);
            promo.setMinPurchaseAmount(minPurchaseAmount != null ? minPurchaseAmount : 0.0);

            if (categoryIds != null && !categoryIds.isEmpty()) {
                // Store as JSON-like string for now
                promo.setApplicableCategories(categoryIds.toString());
            }
            
            if (productIds != null && !productIds.isEmpty()) {
                // Store selected products - using excludedProducts field to represent applicable products
                // Note: This field name is misleading but kept for database compatibility
                promo.setExcludedProducts(productIds.toString());
            }

            promotionRepository.save(promo);
            redirectAttributes.addFlashAttribute("success", "Promotion created successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid discount type: " + discountType);
            return "redirect:/admin/promotions/add";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating promotion: " + e.getMessage());
            return "redirect:/admin/promotions/add";
        }
        return "redirect:/admin/promotions";
    }

    @GetMapping("/promotions/{id}/edit")
    public String showEditPromotionForm(@PathVariable Long id, Model model) {
        com.minari.ecommerce.entity.Promotion promotion = promotionRepository.findById(id).orElse(null);
        if (promotion == null) {
            return "redirect:/admin/promotions";
        }
        model.addAttribute("promotion", promotion);
        model.addAttribute("categories", catalogService.getAllCategories());
        // Simple logic to pass selected IDs if needed, though Thymeleaf handles
        // iteration
        return "admin/promotions/edit";
    }

    @PostMapping("/promotions/update/{id}")
    public String updatePromotion(@PathVariable Long id,
            @RequestParam String code,
            @RequestParam String description,
            @RequestParam Double discountValue,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam Boolean isActive,
            @RequestParam(required = false) Integer usageLimit,
            @RequestParam(required = false) Double minPurchaseAmount,
            @RequestParam(required = false) List<Long> categoryIds,
            RedirectAttributes redirectAttributes) {
        try {
            com.minari.ecommerce.entity.Promotion promo = promotionRepository.findById(id).orElse(null);
            if (promo != null) {
                promo.setPromoCode(code);
                promo.setDescription(description);
                promo.setDiscountValue(discountValue);
                promo.setStartDate(startDate.atStartOfDay());
                promo.setEndDate(endDate.atTime(23, 59, 59));
                promo.setIsActive(isActive);
                promo.setUsageLimit(usageLimit);
                promo.setMinPurchaseAmount(minPurchaseAmount != null ? minPurchaseAmount : 0.0);

                if (categoryIds != null && !categoryIds.isEmpty()) {
                    promo.setApplicableCategories(categoryIds.toString());
                } else {
                    promo.setApplicableCategories(null);
                }

                promotionRepository.save(promo);
                redirectAttributes.addFlashAttribute("success", "Promotion updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Promotion not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating promotion: " + e.getMessage());
        }
        return "redirect:/admin/promotions";
    }

    @PostMapping("/promotions/delete/{id}")
    public String deletePromotion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            promotionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Promotion deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting promotion: " + e.getMessage());
        }
        return "redirect:/admin/promotions";

    }

    @GetMapping("")
    public String adminRedirect() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Initialize defaults to prevent 500 errors
        java.util.Map<String, Object> emptyStats = new java.util.HashMap<>();
        emptyStats.put("totalOrders", 0);
        emptyStats.put("totalRevenue", 0);
        emptyStats.put("pendingOrders", 0);
        emptyStats.put("totalProducts", 0);

        model.addAttribute("stats", emptyStats);
        model.addAttribute("recentOrders", java.util.Collections.emptyList());
        model.addAttribute("topProducts", java.util.Collections.emptyList());
        model.addAttribute("recentReviews", java.util.Collections.emptyList());

        // Fetch stats
        try {
            java.util.Map<String, Object> stats = orderService.getOrderStatistics();
            if (stats != null) {
                model.addAttribute("stats", stats);
            }

            // Recent orders (fetch 5 most recent)
            List<OrderDTO> allOrders = orderService.getAllOrders(null, null);
            if (allOrders != null) {
                int limit = Math.min(allOrders.size(), 5);
                model.addAttribute("recentOrders", allOrders.subList(0, limit));
            }

            // Top products
            try {
                model.addAttribute("topProducts", orderService.getTopSellingProducts(5));
            } catch (Exception e) {
                // Keep default empty list
            }

            // Recent Reviews
            try {
                List<java.util.Map<String, Object>> recentReviews = reviewRepository.findAll().stream()
                        .sorted((a, b) -> b.getReviewDate().compareTo(a.getReviewDate()))
                        .limit(3)
                        .map(r -> {
                            java.util.Map<String, Object> map = new java.util.HashMap<>();
                            map.put("customerName",
                                    r.getCustomer() != null ? r.getCustomer().getFullName() : "Anonymous");
                            map.put("rating", r.getRating());
                            map.put("comment", r.getReviewText());
                            map.put("createdAt", r.getReviewDate());
                            return map;
                        })
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("recentReviews", recentReviews);
            } catch (Exception e) {
                // Keep default empty list
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get current user details
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null) ? auth.getName() : null;
        ViewUser viewUser = new ViewUser();
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(u -> {
                viewUser.setFirstName(u.getFullName());
                viewUser.setEmail(u.getEmail());
            });
        }
        model.addAttribute("currentUser", viewUser);

        return "admin/dashboard";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        try {
            List<ProductCategory> categories = catalogService.getAllCategories();
            model.addAttribute("categories", categories != null ? categories : new java.util.ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("categories", new java.util.ArrayList<>());
            model.addAttribute("error", "Error loading categories for product creation");
        }
        return "admin/products/add";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        List<ProductCategory> categories = catalogService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/products/edit";
    }



    @PostMapping("/products")
    public String createProduct(@RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") Double price,
            @RequestParam(value = "discountPrice", required = false) Double discountPrice,
            @RequestParam(value = "compareAtPrice", required = false) Double compareAtPrice,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "sku", required = false) String sku,
            @RequestParam(value = "isFeatured", required = false) Boolean isFeatured,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "image", required = false) MultipartFile image,
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
            product.setBrand(brand);
            if (sku != null && sku.trim().isEmpty()) {
                product.setSku(null);
            } else {
                product.setSku(sku);
            }
            product.setIsFeatured(isFeatured != null && isFeatured);
            product.setIsActive(isActive != null ? isActive : true);
            if (tags != null && !tags.isEmpty()) {
                product.setTags(String.join(",", tags));
            }

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

    @PostMapping("/products/{productId}/update")
    public String updateProduct(@PathVariable("productId") Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") Double price,
            @RequestParam(value = "discountPrice", required = false) Double discountPrice,
            @RequestParam(value = "compareAtPrice", required = false) Double compareAtPrice,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "sku", required = false) String sku,
            @RequestParam(value = "isFeatured", required = false) Boolean isFeatured,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "image", required = false) MultipartFile image,
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
            if (sku != null && sku.trim().isEmpty()) {
                product.setSku(null);
            } else {
                product.setSku(sku);
            }
            product.setIsFeatured(isFeatured != null && isFeatured);
            product.setIsActive(isActive != null ? isActive : true);

            if (tags != null && !tags.isEmpty()) {
                product.setTags(String.join(",", tags));
            } else {
                product.setTags(null);
            }

            // Set category
            ProductCategory category = catalogService.getCategoryById(categoryId);
            if (category == null) {
                redirectAttributes.addFlashAttribute("error", "Category not found");
                return "redirect:/admin/products/" + id + "/edit";
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
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String orderManagement(@RequestParam(required = false) String keyword, Model model) {
        List<OrderDTO> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = orderService.searchOrders(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            orders = orderService.getAllOrders("", "");
        }
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        OrderDTO order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);
        return "admin/orders/detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String trackingNumber,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderDetails(id, status, trackingNumber);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/products")
    public String productManagement(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.searchProducts(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            products = productService.getAllProducts();
        }
        List<ProductCategory> categories = catalogService.getAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "admin/products";
    }

    // --- Category Management ---

    @GetMapping("/categories")
    public String listCategories(@RequestParam(required = false) String keyword, Model model) {
        try {
            List<ProductCategory> categories;
            if (keyword != null && !keyword.trim().isEmpty()) {
                categories = catalogService.searchCategories(keyword);
                model.addAttribute("keyword", keyword);
            } else {
                categories = catalogService.getAllCategories();
            }
            model.addAttribute("categories", categories != null ? categories : new java.util.ArrayList<>());
            return "admin/category-list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading categories: " + e.getMessage());
            model.addAttribute("categories", new java.util.ArrayList<>());
            return "admin/category-list";
        }
    }

    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        System.out.println("DEBUG: showAddCategoryForm called");
        model.addAttribute("category", new ProductCategory());
        return "admin/categories/add";
    }

    @PostMapping("/categories")
    public String addCategory(@RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile background_image,
            RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: addCategory called for name: " + name);
        try {
            ProductCategory category = new ProductCategory();
            category.setName(name);
            category.setDescription(description);
            category.setIsActive(true); // Set active by default

            if (image != null && !image.isEmpty()) {
                System.out.println("DEBUG: Uploading category icon...");
                String imagePath = fileUploadService.uploadCategoryImage(image);
                category.setImageUrl(imagePath);
            }

            if (background_image != null && !background_image.isEmpty()) {
                System.out.println("DEBUG: Uploading category banner...");
                String bannerPath = fileUploadService.uploadCategoryImage(background_image);
                category.setBannerUrl(bannerPath);
            }

            System.out.println("DEBUG: Saving category to DB...");
            catalogService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category created successfully!");
            System.out.println("DEBUG: Category created successfully: " + name);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to create category: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        ProductCategory category = catalogService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        return "admin/categories/edit";
    }

    @PostMapping("/categories/{id}/update")
    public String updateCategory(@PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile background_image,
            RedirectAttributes redirectAttributes) {
        try {
            ProductCategory category = catalogService.getCategoryById(id);
            if (category == null) {
                redirectAttributes.addFlashAttribute("error", "Category not found");
                return "redirect:/admin/categories";
            }
            category.setName(name);
            category.setDescription(description);

            if (image != null && !image.isEmpty()) {
                // Should delete old image ideally
                if (category.getImageUrl() != null) {
                    try {
                        fileUploadService.deleteFile(category.getImageUrl());
                    } catch (Exception ignored) {
                    }
                }
                String imagePath = fileUploadService.uploadCategoryImage(image);
                category.setImageUrl(imagePath);
            }

            if (background_image != null && !background_image.isEmpty()) {
                if (category.getBannerUrl() != null) {
                    try {
                        fileUploadService.deleteFile(category.getBannerUrl());
                    } catch (Exception ignored) {
                    }
                }
                String bannerPath = fileUploadService.uploadCategoryImage(background_image);
                category.setBannerUrl(bannerPath);
            }

            catalogService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProductCategory category = catalogService.getCategoryById(id);
            if (category != null && category.getImageUrl() != null) {
                try {
                    fileUploadService.deleteFile(category.getImageUrl());
                } catch (Exception ignored) {
                }
            }
            catalogService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}