package com.minari.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Order
 * Used for API responses and requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private String status;
    private String paymentStatus;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private Double totalAmount;
    private Double subtotalAmount;
    private Double taxAmount;
    private Double shippingCost;
    private Double discountAmount;

    // Alias untuk totalAmount (untuk compatibility)
    public Double getTotal() {
        return totalAmount;
    }

    public void setTotal(Double total) {
        this.totalAmount = total;
    }

    private String shippingAddressStreet;
    private String shippingAddressCity;
    private String shippingAddressState;
    private String shippingAddressPostalCode;
    private String shippingAddressCountry;

    private String promoCode;
    private Long promotionId;
    private String notes;
    private String internalNotes;

    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;

    private Long shipmentId;
    private String shipmentStatus;
    private String trackingNumber;

    private Long paymentId;
    private String paymentMethod;
    private LocalDateTime paymentDate;

    private List<OrderItemDTO> items;

    /**
     * Nested DTO for Order Items
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private Double unitPrice;
        private Double totalPrice;
        private String notes;
    }
}
