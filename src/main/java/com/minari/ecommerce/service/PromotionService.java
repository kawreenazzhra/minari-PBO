package com.minari.ecommerce.service;

import com.minari.ecommerce.dto.DiscountCalculation;
import com.minari.ecommerce.entity.CartItem;
import com.minari.ecommerce.entity.Promotion;
import com.minari.ecommerce.entity.ShoppingCart;
import com.minari.ecommerce.repository.PromotionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionService {
    private static final Logger log = LoggerFactory.getLogger(PromotionService.class);
    
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
    
    /**
     * Calculate the best applicable discount for a shopping cart
     * @param cart The shopping cart to calculate discount for
     * @return DiscountCalculation containing discount amount, applied promotion, and eligible subtotal
     */
    public DiscountCalculation calculateBestDiscount(ShoppingCart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            return new DiscountCalculation(0.0, null, 0.0);
        }
        
        double productsSubtotal = cart.getTotalAmount();
        double bestDiscountAmount = 0.0;
        Promotion appliedPromotion = null;
        double bestEligibleSubtotal = 0.0;
        
        List<Promotion> activePromotions = getActivePromotions();
        
        for (Promotion promo : activePromotions) {
            // Check basic constraints
            if (promo.getMinPurchaseAmount() != null && productsSubtotal < promo.getMinPurchaseAmount()) {
                continue;
            }
            if (promo.getUsageLimit() != null && promo.getUsedCount() >= promo.getUsageLimit()) {
                continue;
            }
            
            // Check Category Applicability
            // Parse applicable categories
            List<Long> applicableCategoryIds = new ArrayList<>();
            String catParams = promo.getApplicableCategories();
            if (catParams != null && !catParams.isEmpty() && !catParams.equals("[]")) {
                String clean = catParams.replace("[", "").replace("]", "").replace(" ", "");
                for (String s : clean.split(",")) {
                    try {
                        applicableCategoryIds.add(Long.parseLong(s));
                    } catch(NumberFormatException e) {
                        log.warn("Invalid category ID in promotion: {}", s);
                    }
                }
            }
            
            // Calculate eligible subtotal
            double eligibleSubtotal = 0.0;
            if (applicableCategoryIds.isEmpty()) {
                // Empty categories = applies to all products
                eligibleSubtotal = productsSubtotal;
            } else {
                for (CartItem item : cart.getItems()) {
                    if (item.getProduct().getCategory() != null && 
                        applicableCategoryIds.contains(item.getProduct().getCategory().getId())) {
                        eligibleSubtotal += item.getSubtotal();
                    }
                }
            }
            
            if (eligibleSubtotal > 0) {
                double discount = 0.0;
                if (promo.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
                    discount = eligibleSubtotal * (promo.getDiscountValue() / 100.0);
                } else if (promo.getDiscountType() == Promotion.DiscountType.FIXED_AMOUNT) {
                    // Fixed amount applies but capped at eligible subtotal
                    discount = Math.min(eligibleSubtotal, promo.getDiscountValue());
                }
                
                if (discount > bestDiscountAmount) {
                    bestDiscountAmount = discount;
                    appliedPromotion = promo;
                    bestEligibleSubtotal = eligibleSubtotal;
                }
            }
        }
        
        log.info("Best discount calculated: {} for promotion: {}", 
                 bestDiscountAmount, 
                 appliedPromotion != null ? appliedPromotion.getName() : "None");
        
        return new DiscountCalculation(bestDiscountAmount, appliedPromotion, bestEligibleSubtotal);
    }
}