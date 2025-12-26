package com.minari.ecommerce.controller;

import com.minari.ecommerce.entity.Suggestion;
import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.repository.UserRepository;
import com.minari.ecommerce.service.SuggestionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final UserRepository userRepository;

    public SuggestionController(SuggestionService suggestionService, UserRepository userRepository) {
        this.suggestionService = suggestionService;
        this.userRepository = userRepository;
    }

    /**
     * Customer endpoint: Submit a suggestion from the footer form
     */
    @PostMapping("/suggestion/store")
    public String storeSuggestion(@RequestParam String message, RedirectAttributes redirectAttributes) {
        try {
            // Get current user info if authenticated
            String customerEmail = null;
            String customerName = "Guest";
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                customerEmail = auth.getName();
                userRepository.findByEmail(customerEmail).ifPresent(user -> {
                    // Use the full name from user
                });
                User user = userRepository.findByEmail(customerEmail).orElse(null);
                if (user != null) {
                    customerName = user.getFullName();
                }
            }

            // Save suggestion
            suggestionService.saveSuggestion(message, customerEmail, customerName);
            
            redirectAttributes.addFlashAttribute("suggestionSuccess", "Thank you for your suggestion!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("suggestionError", "Failed to submit suggestion. Please try again.");
        }

        return "redirect:/";
    }

    /**
     * Admin endpoint: View all suggestions
     */
    @GetMapping("/admin/suggestions")
    public String viewSuggestions(Model model) {
        List<Suggestion> suggestions = suggestionService.getAllSuggestions();
        Long unreadCount = suggestionService.getUnreadCount();
        
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentUri", "/admin/suggestions");
        
        return "admin/suggestions";
    }

    /**
     * Admin endpoint: Mark suggestion as read
     */
    @PostMapping("/admin/suggestions/{id}/mark-read")
    public String markAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = suggestionService.markAsRead(id);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Suggestion marked as read");
            } else {
                redirectAttributes.addFlashAttribute("error", "Suggestion not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to mark suggestion as read");
        }
        
        return "redirect:/admin/suggestions";
    }

    /**
     * Admin endpoint: Delete suggestion
     */
    @PostMapping("/admin/suggestions/{id}/delete")
    public String deleteSuggestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = suggestionService.deleteSuggestion(id);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Suggestion deleted successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Suggestion not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete suggestion");
        }
        
        return "redirect:/admin/suggestions";
    }
}
