package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    
    // Find all suggestions ordered by creation date (newest first)
    List<Suggestion> findAllByOrderByCreatedAtDesc();
    
    // Count unread suggestions
    @Query("SELECT COUNT(s) FROM Suggestion s WHERE s.isRead = false")
    Long countUnreadSuggestions();
    
    // Find unread suggestions
    List<Suggestion> findByIsReadFalseOrderByCreatedAtDesc();
    
    // Find read suggestions
    List<Suggestion> findByIsReadTrueOrderByCreatedAtDesc();
}
