package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Promotion;
import com.minari.ecommerce.repository.PromotionRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Promotion getPromotionByCode(String promoCode) {
        return promotionRepository.findByPromoCode(promoCode);
    }

    public Promotion savePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    public void updateExpiredPromotions() {
        java.util.List<Promotion> activePromotions = promotionRepository.findByIsActive(true);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        for (Promotion promo : activePromotions) {
            if (promo.getEndDate() != null && promo.getEndDate().isBefore(now)) {
                promo.setIsActive(false);
                promotionRepository.save(promo);
            }
        }
    }

    public java.util.List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActive(true);
    }
}