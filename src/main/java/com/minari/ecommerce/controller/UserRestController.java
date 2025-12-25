package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Address;
import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.entity.RegisteredCustomer;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.repository.AddressRepository;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UserRestController(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "User not authenticated");
            return ResponseEntity.ok(response);
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.ok(response);
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getFullName() != null ? user.getFullName() : "-");
        userData.put("phone", user.getPhone() != null ? user.getPhone() : "-");
        userData.put("email", user.getEmail() != null ? user.getEmail() : "-");
        
        // Get birth date if user is RegisteredCustomer
        if (user instanceof RegisteredCustomer) {
            RegisteredCustomer regCustomer = (RegisteredCustomer) user;
            if (regCustomer.getBirthDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                userData.put("birth_date", regCustomer.getBirthDate().format(formatter));
            } else {
                userData.put("birth_date", "-");
            }
        } else {
            userData.put("birth_date", "-");
        }

        // Get address if user is Customer
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            List<Address> addresses = addressRepository.findByCustomer(customer);
            if (!addresses.isEmpty()) {
                // Find default or pick first
                Address displayAddress = addresses.stream()
                    .filter(Address::getIsDefault)
                    .findFirst()
                    .orElse(addresses.get(0));
                userData.put("address", displayAddress.getFullAddress());
            } else {
                userData.put("address", "-");
            }
        } else {
            userData.put("address", "-");
        }

        // Determine role
        String role = "user";
        if (user.getRole() != null) {
            role = user.getRole().name().toLowerCase();
        }
        userData.put("role", role);

        response.put("success", true);
        response.put("user", userData);
        
        return ResponseEntity.ok(response);
    }
}
