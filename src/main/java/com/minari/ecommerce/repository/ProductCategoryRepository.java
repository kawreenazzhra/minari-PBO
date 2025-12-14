package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    Optional<ProductCategory> findByName(String name);
    Optional<ProductCategory> findByNameIgnoreCase(String name);
    boolean existsByName(String name);
    
    List<ProductCategory> findByIsActiveTrue();
    long countByIsActiveTrue();
    
    Page<ProductCategory> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name, String description, Pageable pageable);
}