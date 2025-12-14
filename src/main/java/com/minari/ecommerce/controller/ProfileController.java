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

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String profile(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("user", user);
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
}