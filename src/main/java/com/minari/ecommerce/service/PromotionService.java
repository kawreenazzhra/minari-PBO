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
}