package com.minari.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("🔧 Attempting to fix database schema...");
            // Force the customer_id column to be nullable to support Admin orders
            jdbcTemplate.execute("ALTER TABLE orders ALTER COLUMN customer_id SET NULL"); 
            // Also ensure it is not mandatory in foreign key if needed, but the column nullability is key.
            System.out.println("✅ SUCCESS: Altered orders.customer_id to be NULLABLE.");
        } catch (Exception e) {
            // It might fail if table doesn't exist or other reasons, just log info
            System.out.println("ℹ️ Schema update info (might already be fixed): " + e.getMessage());
        }
    }
}
