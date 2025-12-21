package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Promotion;
import com.minari.ecommerce.repository.PromotionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final ObjectMapper objectMapper;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
        this.objectMapper = new ObjectMapper();
    }

    public Promotion getPromotionByCode(String promoCode) {
        return promotionRepository.findByPromoCode(promoCode);
    }

    public Promotion savePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public void updateExpiredPromotions() {
        List<Promotion> activePromotions = promotionRepository.findByIsActive(true);
        LocalDateTime now = LocalDateTime.now();

        for (Promotion promo : activePromotions) {
            if (promo.getEndDate() != null && promo.getEndDate().isBefore(now)) {
                promo.setIsActive(false);
                promotionRepository.save(promo);
            }
        }
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActive(true);
    }

    /**
     * Get all valid promotions applicable to a specific product
     */
    public List<Promotion> getApplicablePromotionsForProduct(Long productId, Long categoryId) {
        List<Promotion> allActivePromotions = getActivePromotions();
        List<Promotion> applicablePromotions = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Promotion promo : allActivePromotions) {
            // Check if promotion is within date range
            if (!now.isAfter(promo.getStartDate()) || !now.isBefore(promo.getEndDate())) {
                continue;
            }

            // Check if product is excluded
            if (isProductExcluded(promo, productId)) {
                continue;
            }

            // Check if applicable to categories
            if (promo.getApplicableCategories() != null && !promo.getApplicableCategories().isEmpty()) {
                if (isProductInApplicableCategories(promo, categoryId)) {
                    applicablePromotions.add(promo);
                }
            } else {
                // No specific categories = applies to all
                applicablePromotions.add(promo);
            }
        }

        return applicablePromotions;
    }

    /**
     * Calculate discount for a product price with applicable promotions
     * Returns the best discount available
     */
    public double calculateDiscount(double price, List<Promotion> promotions) {
        double maxDiscount = 0;

        for (Promotion promo : promotions) {
            if (!promo.isValid()) {
                continue;
            }

            if (promo.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
                double discount = price * (promo.getDiscountValue() / 100);
                if (promo.getMaxDiscountAmount() != null && discount > promo.getMaxDiscountAmount()) {
                    discount = promo.getMaxDiscountAmount();
                }
                maxDiscount = Math.max(maxDiscount, discount);
            } else if (promo.getDiscountType() == Promotion.DiscountType.FIXED_AMOUNT) {
                maxDiscount = Math.max(maxDiscount, promo.getDiscountValue());
            }
        }

        return maxDiscount;
    }

    /**
     * Calculate the best discount amount for a product
     */
    public double applyBestPromotion(double price, Long productId, Long categoryId) {
        List<Promotion> applicablePromotions = getApplicablePromotionsForProduct(productId, categoryId);

        if (applicablePromotions.isEmpty()) {
            return price;
        }

        double discount = calculateDiscount(price, applicablePromotions);
        return Math.max(0, price - discount);
    }

    private boolean isProductExcluded(Promotion promo, Long productId) {
        if (promo.getExcludedProducts() == null || promo.getExcludedProducts().isEmpty()) {
            return false;
        }

        try {
            List<Long> excludedIds = objectMapper.readValue(
                    promo.getExcludedProducts(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
            return excludedIds.contains(productId);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isProductInApplicableCategories(Promotion promo, Long categoryId) {
        if (promo.getApplicableCategories() == null || promo.getApplicableCategories().isEmpty()) {
            return true;
        }

        try {
            List<Long> categoryIds = objectMapper.readValue(
                    promo.getApplicableCategories(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
            return categoryIds.contains(categoryId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculate total discount for entire cart
     * 
     * @param cart Shopping cart with items
     * @return Total discount amount
     */
    public double calculateCartDiscount(com.minari.ecommerce.entity.ShoppingCart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0.0;
        }

        double totalDiscount = 0.0;

        for (com.minari.ecommerce.entity.CartItem item : cart.getItems()) {
            if (item.getProduct() == null) {
                continue;
            }

            double unitPrice = item.getUnitPrice();
            Long productId = item.getProduct().getId();
            Long categoryId = item.getProduct().getCategory() != null ? item.getProduct().getCategory().getId() : null;

            // Get applicable promotions for this product
            List<Promotion> applicablePromotions = getApplicablePromotionsForProduct(productId, categoryId);

            if (!applicablePromotions.isEmpty()) {
                double discount = calculateDiscount(unitPrice, applicablePromotions);
                totalDiscount += discount * item.getQuantity();
            }
        }

        return totalDiscount;
    }
}