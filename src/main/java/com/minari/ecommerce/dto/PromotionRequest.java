package com.minari.ecommerce.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PromotionRequest {

    @NotBlank(message = "Promotion name is required")
    @Size(min = 3, max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @Pattern(regexp = "^[A-Z0-9_-]*$", message = "Promo code can only contain uppercase letters, numbers, hyphens and underscores")
    private String promoCode;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount cannot exceed 100%")
    private BigDecimal discountValue;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    private Boolean isActive = true;

    // Banner image (optional)
    private MultipartFile bannerImage;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public MultipartFile getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(MultipartFile bannerImage) {
        this.bannerImage = bannerImage;
    }
}
