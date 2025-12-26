package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Suggestion;
import com.minari.ecommerce.repository.SuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;

    public SuggestionService(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    /**
     * Save a new suggestion
     */
    @Transactional
    public Suggestion saveSuggestion(String message, String customerEmail, String customerName) {
        Suggestion suggestion = new Suggestion(message, customerEmail, customerName);
        return suggestionRepository.save(suggestion);
    }

    /**
     * Get all suggestions ordered by date (newest first)
     */
    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get count of unread suggestions
     */
    public Long getUnreadCount() {
        return suggestionRepository.countUnreadSuggestions();
    }

    /**
     * Get unread suggestions
     */
    public List<Suggestion> getUnreadSuggestions() {
        return suggestionRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    /**
     * Mark a suggestion as read
     */
    @Transactional
    public boolean markAsRead(Long suggestionId) {
        Optional<Suggestion> suggestionOpt = suggestionRepository.findById(suggestionId);
        if (suggestionOpt.isPresent()) {
            Suggestion suggestion = suggestionOpt.get();
            suggestion.setIsRead(true);
            suggestionRepository.save(suggestion);
            return true;
        }
        return false;
    }

    /**
     * Delete a suggestion
     */
    @Transactional
    public boolean deleteSuggestion(Long suggestionId) {
        if (suggestionRepository.existsById(suggestionId)) {
            suggestionRepository.deleteById(suggestionId);
            return true;
        }
        return false;
    }
}
