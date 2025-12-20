package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.RegisteredCustomer;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserRepository userRepository;
    private final com.minari.ecommerce.repository.AddressRepository addressRepository;

    public ProfileController(UserRepository userRepository, com.minari.ecommerce.repository.AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @GetMapping
    public String profile(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            userRepository.findByEmail(email).ifPresent(user -> {
                model.addAttribute("user", user);
                // Fetch address for display
                if (user instanceof com.minari.ecommerce.entity.Customer) {
                    List<com.minari.ecommerce.entity.Address> addresses = addressRepository.findByCustomerId(user.getId());
                    if (!addresses.isEmpty()) {
                        // Find default or pick first
                        com.minari.ecommerce.entity.Address displayAddress = addresses.stream()
                            .filter(com.minari.ecommerce.entity.Address::getIsDefault)
                            .findFirst()
                            .orElse(addresses.get(0));
                        model.addAttribute("displayAddress", displayAddress);
                    }
                }
            });
        } else {
            model.addAttribute("user", null);
        }
        return "profile/view";
    }

    @PostMapping
    public String updateProfile(RegisteredCustomer customer, Authentication authentication) {
        String email = authentication.getName();
        User existingUser = userRepository.findByEmail(email).orElseThrow();
        existingUser.setFullName(customer.getFullName());
        existingUser.setPhone(customer.getPhone());
        userRepository.save(existingUser);
        return "redirect:/profile";
    }

    @GetMapping("/address")
    public String address(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        List<com.minari.ecommerce.entity.Address> addressList = new java.util.ArrayList<>();
        if (user instanceof com.minari.ecommerce.entity.Customer) {
            addressList = addressRepository.findByCustomerId(user.getId());
        }
        
        model.addAttribute("addresses", addressList);
        return "profile/address";
    }

    @PostMapping("/address")
    public String addAddress(com.minari.ecommerce.entity.Address address, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user instanceof com.minari.ecommerce.entity.Customer) {
            com.minari.ecommerce.entity.Customer customer = (com.minari.ecommerce.entity.Customer) user;
            address.setCustomer(customer);
            // Ensure required fields
            if (address.getCountry() == null || address.getCountry().isBlank()) address.setCountry("Indonesia");
            if (address.getState() == null || address.getState().isBlank()) address.setState("NA");
            if (address.getProvince() == null || address.getProvince().isBlank()) address.setProvince("NA");
            if (address.getCity() == null) address.setCity("Unknown");
            if (address.getZipcode() == null) address.setZipcode("00000");
            if (address.getStreetAddress() == null) address.setStreetAddress("-");
            
            addressRepository.save(address);
        }

        return "redirect:/profile/address";
    }
}