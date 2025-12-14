package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Customer API
 * Handles all customer-related API endpoints
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {

    private final CustomerService customerService;

    /**
     * GET /api/customers - Get all customers with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching all customers - page: {}, size: {}", page, size);
            
            Page<Customer> customers = customerService.getAllCustomers(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers.getContent());
            response.put("totalItems", customers.getTotalElements());
            response.put("totalPages", customers.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/{id} - Get single customer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerService.getCustomerById(#id).map(c -> c.getId() == principal.id).orElse(false)")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Long id) {
        try {
            log.info("Fetching customer with ID: {}", id);
            
            Customer customer = customerService.getCustomerById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching customer: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/search - Search customers
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchCustomers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Searching customers with keyword: {}", keyword);
            
            Page<Customer> customers = customerService.searchCustomers(keyword, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers.getContent());
            response.put("totalItems", customers.getTotalElements());
            response.put("totalPages", customers.getTotalPages());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/customers - Create new customer
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCustomer(@RequestBody Customer customer) {
        try {
            log.info("Creating new customer: {}", customer.getEmail());
            
            Customer createdCustomer = customerService.createCustomer(customer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer created successfully");
            response.put("data", createdCustomer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/customers/{id} - Update customer
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer customerDetails) {
        try {
            log.info("Updating customer with ID: {}", id);
            
            Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer updated successfully");
            response.put("data", updatedCustomer);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found or validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/customers/{id} - Delete customer
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Long id) {
        try {
            log.info("Deleting customer with ID: {}", id);
            
            customerService.deleteCustomer(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/customers/{id}/status - Toggle customer status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleCustomerStatus(@PathVariable Long id) {
        try {
            log.info("Toggling status for customer ID: {}", id);
            
            Customer customer = customerService.toggleCustomerStatus(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer status updated");
            response.put("data", customer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error toggling customer status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/customers/{id}/newsletter - Toggle newsletter subscription
     */
    @PatchMapping("/{id}/newsletter")
    public ResponseEntity<Map<String, Object>> toggleNewsletterSubscription(@PathVariable Long id) {
        try {
            log.info("Toggling newsletter subscription for customer ID: {}", id);
            
            Customer customer = customerService.toggleNewsletterSubscription(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Newsletter subscription updated");
            response.put("data", customer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error toggling newsletter subscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/customers/{id}/loyalty-points/add - Add loyalty points
     */
    @PatchMapping("/{id}/loyalty-points/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam Integer points) {
        try {
            log.info("Adding {} loyalty points to customer ID: {}", points, id);
            
            Customer customer = customerService.addLoyaltyPoints(id, points);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Loyalty points added successfully");
            response.put("data", customer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding loyalty points", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/customers/{id}/loyalty-points/deduct - Deduct loyalty points
     */
    @PatchMapping("/{id}/loyalty-points/deduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deductLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam Integer points) {
        try {
            log.info("Deducting {} loyalty points from customer ID: {}", points, id);
            
            Customer customer = customerService.deductLoyaltyPoints(id, points);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Loyalty points deducted successfully");
            response.put("data", customer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deducting loyalty points", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/stats/summary - Get customer statistics
     */
    @GetMapping("/stats/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCustomerStatistics() {
        try {
            log.info("Fetching customer statistics");
            
            CustomerService.CustomerStatistics stats = customerService.getCustomerStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching customer statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/active - Get active customers
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getActiveCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching active customers");
            
            Page<Customer> customers = customerService.getActiveCustomers(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers.getContent());
            response.put("totalItems", customers.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching active customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/inactive - Get inactive customers
     */
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getInactiveCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching inactive customers");
            
            Page<Customer> customers = customerService.getInactiveCustomers(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers.getContent());
            response.put("totalItems", customers.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching inactive customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/vip - Get VIP customers
     */
    @GetMapping("/vip")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getVIPCustomers(
            @RequestParam(defaultValue = "1000") int minPoints,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching VIP customers with minimum {} points", minPoints);
            
            Page<Customer> customers = customerService.getVIPCustomers(minPoints, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers.getContent());
            response.put("totalItems", customers.getTotalElements());
            response.put("minPoints", minPoints);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching VIP customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/newsletter-subscribers - Get newsletter subscribers
     */
    @GetMapping("/newsletter-subscribers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getNewsletterSubscribers() {
        try {
            log.info("Fetching newsletter subscribers");
            
            List<Customer> subscribers = customerService.getNewsletterSubscribers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", subscribers);
            response.put("count", subscribers.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching newsletter subscribers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/new - Get new customers for date range
     */
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getNewCustomers(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching new customers between {} and {}", startDate, endDate);
            
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            Page<Customer> customers = customerService.getNewCustomers(start, end, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers.getContent());
            response.put("totalItems", customers.getTotalElements());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching new customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/by-city/{city} - Get customers by city
     */
    @GetMapping("/by-city/{city}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCustomersByCity(@PathVariable String city) {
        try {
            log.info("Fetching customers from city: {}", city);
            
            List<Customer> customers = customerService.getCustomersByCity(city);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers);
            response.put("city", city);
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching customers by city", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/by-country/{country} - Get customers by country
     */
    @GetMapping("/by-country/{country}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCustomersByCountry(@PathVariable String country) {
        try {
            log.info("Fetching customers from country: {}", country);
            
            List<Customer> customers = customerService.getCustomersByCountry(country);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers);
            response.put("country", country);
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching customers by country", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/customers/{id}/verify-email - Verify customer email
     */
    @PatchMapping("/{id}/verify-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> verifyCustomerEmail(@PathVariable Long id) {
        try {
            log.info("Verifying email for customer ID: {}", id);
            
            Customer customer = customerService.verifyCustomerEmail(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Email verified successfully");
            response.put("data", customer);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error verifying customer email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
