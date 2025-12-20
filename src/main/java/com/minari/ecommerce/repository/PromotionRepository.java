package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Promotion findByPromoCode(String promoCode);

    java.util.List<Promotion> findByIsActive(Boolean isActive);

    java.util.List<Promotion> findByPromoCodeContainingIgnoreCase(String promoCode);
}