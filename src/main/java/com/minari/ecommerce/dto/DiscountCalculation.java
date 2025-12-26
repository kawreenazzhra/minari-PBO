package com.minari.ecommerce.dto;

import com.minari.ecommerce.entity.Promotion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCalculation {
    private double discountAmount;
    private Promotion appliedPromotion;
    private double eligibleSubtotal;
}
