package com.minari.ecommerce.config;

import com.minari.ecommerce.entity.*;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.repository.ProductRepository;
import com.minari.ecommerce.repository.ProductCategoryRepository;
import com.minari.ecommerce.repository.PromotionRepository;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final PromotionRepository promotionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
            ProductRepository productRepository,
            ProductCategoryRepository categoryRepository,
            PromotionRepository promotionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.promotionRepository = promotionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🌱 Starting data seeding...");

        // Only create users - Categories and Promotions will be created by Admin via UI
        createUsers();
        // createPromotions(); // REMOVED to ensure empty home page

        System.out.println("🎉 Data seeding completed successfully!");
        System.out.println("==========================================");
        System.out.println("DEMO ACCOUNTS:");
        System.out.println("Admin: admin@minari.com / admin123");
        System.out.println("Customer: customer@minari.com / customer123");
        System.out.println("==========================================");
        System.out.println("📌 Please create categories from admin panel:");
        System.out.println("   1. Go to /admin/categories");
        System.out.println("   2. Add categories (Shirts, Sweaters, etc.)");
        System.out.println("   3. Then add products from /admin/products");
        System.out.println("==========================================");
    }

    private void createUsers() {
        System.out.println("👥 Creating default users...");

        try {
            // CREATE ADMIN USER
            if (userRepository.findByEmail("admin@minari.com").isEmpty()) {
                Admin admin = new Admin();
                admin.setEmail("admin@minari.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Minari Administrator");
                admin.setPhone("+628123456789");
                admin.setIsActive(true);

                userRepository.save(admin);
                userRepository.flush();
                System.out.println("   ✅ Admin user created: admin@minari.com / admin123");
            } else {
                System.out.println("   ℹ️ Admin user already exists");
            }

            // CREATE CUSTOMER USER
            if (userRepository.findByEmail("customer@minari.com").isEmpty()) {
                RegisteredCustomer customer = new RegisteredCustomer();
                customer.setEmail("customer@minari.com");
                customer.setPassword(passwordEncoder.encode("customer123"));
                customer.setFullName("Sari Dewi");
                customer.setPhone("+628987654321");
                customer.setIsActive(true);

                userRepository.save(customer);
                userRepository.flush();
                System.out.println("   ✅ Customer user created: customer@minari.com / customer123");
            } else {
                System.out.println("   ℹ️ Customer user already exists");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating users: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to seed data", e);
        }
    }

    // private void createPromotions() { ... } (Removed)
}