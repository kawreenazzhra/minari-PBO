package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Customer management
 * Handles business logic for customer operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Get all customers with pagination
     */
    public Page<Customer> getAllCustomers(int page, int size) {
        log.info("Fetching all customers - page: {}, size: {}", page, size);
        return customerRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Get customer by ID
     */
    public Optional<Customer> getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id);
    }

    /**
     * Get customer by email
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("Fetching customer with email: {}", email);
        return customerRepository.findByEmail(email);
    }

    /**
     * Create new customer
     */
    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {}", customer.getEmail());
        
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }
        
        if (customer.getPhone() != null && customerRepository.existsByPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists: " + customer.getPhone());
        }
        
        customer.setIsActive(true);
        customer.setEmailVerified(false);
        customer.setLoyaltyPoints(0);
        customer.setNewsletterSubscribed(false);
        customer.setMemberSince(LocalDateTime.now());
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    /**
     * Update customer
     */
    public Customer updateCustomer(Long id, Customer customerDetails) {
        log.info("Updating customer with ID: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        if (customerDetails.getFullName() != null) {
            customer.setFullName(customerDetails.getFullName());
        }
        
        if (customerDetails.getPhone() != null && !customerDetails.getPhone().equals(customer.getPhone())) {
            if (customerRepository.existsByPhone(customerDetails.getPhone())) {
                throw new IllegalArgumentException("Phone number already exists: " + customerDetails.getPhone());
            }
            customer.setPhone(customerDetails.getPhone());
        }
        
        if (customerDetails.getAvatarUrl() != null) {
            customer.setAvatarUrl(customerDetails.getAvatarUrl());
        }
        
        if (customerDetails.getShippingAddress() != null) {
            customer.setShippingAddress(customerDetails.getShippingAddress());
        }
        
        customer.setUpdatedAt(LocalDateTime.now());
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", id);
        return updatedCustomer;
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        customerRepository.delete(customer);
        log.info("Customer deleted successfully: {}", id);
    }

    /**
     * Activate/Deactivate customer
     */
    public Customer toggleCustomerStatus(Long id) {
        log.info("Toggling status for customer ID: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        customer.setIsActive(!customer.getIsActive());
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer status toggled: {} - New status: {}", id, updatedCustomer.getIsActive());
        return updatedCustomer;
    }

    /**
     * Subscribe/Unsubscribe from newsletter
     */
    public Customer toggleNewsletterSubscription(Long id) {
        log.info("Toggling newsletter subscription for customer ID: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        customer.setNewsletterSubscribed(!customer.getNewsletterSubscribed());
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Newsletter subscription toggled: {} - New status: {}", id, updatedCustomer.getNewsletterSubscribed());
        return updatedCustomer;
    }

    /**
     * Add loyalty points to customer
     */
    public Customer addLoyaltyPoints(Long id, Integer points) {
        log.info("Adding {} loyalty points to customer ID: {}", points, id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Loyalty points added: {} - Total points: {}", id, updatedCustomer.getLoyaltyPoints());
        return updatedCustomer;
    }

    /**
     * Deduct loyalty points
     */
    public Customer deductLoyaltyPoints(Long id, Integer points) {
        log.info("Deducting {} loyalty points from customer ID: {}", points, id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        Integer newPoints = customer.getLoyaltyPoints() - points;
        if (newPoints < 0) {
            log.warn("Insufficient loyalty points for customer ID: {}", id);
            newPoints = 0;
        }
        
        customer.setLoyaltyPoints(newPoints);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Loyalty points deducted: {} - Remaining points: {}", id, updatedCustomer.getLoyaltyPoints());
        return updatedCustomer;
    }

    /**
     * Search customers
     */
    public Page<Customer> searchCustomers(String keyword, int page, int size) {
        log.info("Searching customers with keyword: {}", keyword);
        return customerRepository.searchCustomers(keyword, PageRequest.of(page, size));
    }

    /**
     * Get active customers
     */
    public Page<Customer> getActiveCustomers(int page, int size) {
        log.info("Fetching active customers - page: {}, size: {}", page, size);
        return customerRepository.findByIsActive(true, PageRequest.of(page, size));
    }

    /**
     * Get inactive customers
     */
    public Page<Customer> getInactiveCustomers(int page, int size) {
        log.info("Fetching inactive customers - page: {}, size: {}", page, size);
        return customerRepository.findByIsActive(false, PageRequest.of(page, size));
    }

    /**
     * Get VIP customers
     */
    public Page<Customer> getVIPCustomers(int minPoints, int page, int size) {
        log.info("Fetching VIP customers with minimum {} points", minPoints);
        return customerRepository.findVIPCustomers(minPoints, PageRequest.of(page, size));
    }

    /**
     * Get newsletter subscribers
     */
    public List<Customer> getNewsletterSubscribers() {
        log.info("Fetching newsletter subscribers");
        return customerRepository.findByNewsletterSubscribed(true);
    }

    /**
     * Get new customers for date range
     */
    public Page<Customer> getNewCustomers(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        log.info("Fetching new customers between {} and {}", startDate, endDate);
        return customerRepository.findNewCustomersByDateRange(startDate, endDate, PageRequest.of(page, size));
    }

    /**
     * Get customers by city
     */
    public List<Customer> getCustomersByCity(String city) {
        log.info("Fetching customers from city: {}", city);
        return customerRepository.findCustomersByCity(city);
    }

    /**
     * Get customers by country
     */
    public List<Customer> getCustomersByCountry(String country) {
        log.info("Fetching customers from country: {}", country);
        return customerRepository.findCustomersByCountry(country);
    }

    /**
     * Get total customer count
     */
    public Long getTotalCustomerCount() {
        log.info("Fetching total customer count");
        return customerRepository.count();
    }

    /**
     * Get active customer count
     */
    public Long getActiveCustomerCount() {
        log.info("Fetching active customer count");
        return customerRepository.countByStatus(true);
    }

    /**
     * Get inactive customer count
     */
    public Long getInactiveCustomerCount() {
        log.info("Fetching inactive customer count");
        return customerRepository.countByStatus(false);
    }

    /**
     * Get new customers today count
     */
    public Long getNewCustomersToday() {
        log.info("Fetching new customers today count");
        return customerRepository.countNewCustomersToday();
    }

    /**
     * Get top loyal customers
     */
    public List<Customer> getTopLoyalCustomers() {
        log.info("Fetching top loyal customers");
        return customerRepository.findTopLoyaltyCustomers();
    }

    /**
     * Get recently active customers
     */
    public Page<Customer> getRecentlyActiveCustomers(int page, int size) {
        log.info("Fetching recently active customers");
        return customerRepository.findRecentlyActiveCustomers(PageRequest.of(page, size));
    }

    /**
     * Get customers with orders
     */
    public Page<Customer> getCustomersWithOrders(int page, int size) {
        log.info("Fetching customers with orders");
        return customerRepository.findCustomersWithOrders(PageRequest.of(page, size));
    }

    /**
     * Get customers with saved addresses
     */
    public Page<Customer> getCustomersWithSavedAddresses(int page, int size) {
        log.info("Fetching customers with saved addresses");
        return customerRepository.findCustomersWithSavedAddresses(PageRequest.of(page, size));
    }

    /**
     * Get inactive customers (no orders in specified days)
     */
    public List<Customer> getInactiveCustomersForDays(int days) {
        log.info("Fetching inactive customers (no orders in {} days)", days);
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return customerRepository.findInactiveCustomers(date);
    }

    /**
     * Update last login timestamp
     */
    public void updateLastLogin(Long customerId) {
        log.info("Updating last login for customer ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
        
        customer.setLastLogin(LocalDateTime.now());
        customerRepository.save(customer);
    }

    /**
     * Verify customer email
     */
    public Customer verifyCustomerEmail(Long customerId) {
        log.info("Verifying email for customer ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
        
        customer.setEmailVerified(true);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Email verified for customer ID: {}", customerId);
        return updatedCustomer;
    }

    /**
     * Get customer statistics
     */
    public CustomerStatistics getCustomerStatistics() {
        log.info("Fetching customer statistics");
        
        Long totalCustomers = getTotalCustomerCount();
        Long activeCustomers = getActiveCustomerCount();
        Long inactiveCustomers = getInactiveCustomerCount();
        Long newCustomersToday = getNewCustomersToday();
        List<Customer> vipCustomers = getTopLoyalCustomers();
        
        return CustomerStatistics.builder()
                .totalCustomers(totalCustomers)
                .activeCustomers(activeCustomers)
                .inactiveCustomers(inactiveCustomers)
                .newCustomersToday(newCustomersToday)
                .vipCustomersCount((long) vipCustomers.size())
                .build();
    }

    /**
     * Statistics DTO for customer data
     */
    @Builder
    @AllArgsConstructor
    public static class CustomerStatistics {
        public Long totalCustomers;
        public Long activeCustomers;
        public Long inactiveCustomers;
        public Long newCustomersToday;
        public Long vipCustomersCount;
    }
}
