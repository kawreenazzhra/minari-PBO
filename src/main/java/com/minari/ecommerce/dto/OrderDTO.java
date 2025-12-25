package com.minari.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Order
 * Used for API responses and requests
 */
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

    public OrderDTO() {
    }

    public OrderDTO(Long id, String orderNumber, String status, String paymentStatus, Long customerId,
            String customerName, String customerEmail, String customerPhone, Double totalAmount, Double subtotalAmount,
            Double taxAmount, Double shippingCost, Double discountAmount, String shippingAddressStreet,
            String shippingAddressCity, String shippingAddressState, String shippingAddressPostalCode,
            String shippingAddressCountry, String promoCode, Long promotionId, String notes, String internalNotes,
            LocalDateTime orderDate, LocalDateTime updatedAt, LocalDateTime estimatedDeliveryDate,
            LocalDateTime actualDeliveryDate, Long shipmentId, String shipmentStatus, String trackingNumber,
            Long paymentId, String paymentMethod, LocalDateTime paymentDate, List<OrderItemDTO> items) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.subtotalAmount = subtotalAmount;
        this.taxAmount = taxAmount;
        this.shippingCost = shippingCost;
        this.discountAmount = discountAmount;
        this.shippingAddressStreet = shippingAddressStreet;
        this.shippingAddressCity = shippingAddressCity;
        this.shippingAddressState = shippingAddressState;
        this.shippingAddressPostalCode = shippingAddressPostalCode;
        this.shippingAddressCountry = shippingAddressCountry;
        this.promoCode = promoCode;
        this.promotionId = promotionId;
        this.notes = notes;
        this.internalNotes = internalNotes;
        this.orderDate = orderDate;
        this.updatedAt = updatedAt;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.shipmentId = shipmentId;
        this.shipmentStatus = shipmentStatus;
        this.trackingNumber = trackingNumber;
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.items = items;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(Double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getShippingAddressStreet() {
        return shippingAddressStreet;
    }

    public void setShippingAddressStreet(String shippingAddressStreet) {
        this.shippingAddressStreet = shippingAddressStreet;
    }

    public String getShippingAddressCity() {
        return shippingAddressCity;
    }

    public void setShippingAddressCity(String shippingAddressCity) {
        this.shippingAddressCity = shippingAddressCity;
    }

    public String getShippingAddressState() {
        return shippingAddressState;
    }

    public void setShippingAddressState(String shippingAddressState) {
        this.shippingAddressState = shippingAddressState;
    }

    public String getShippingAddressPostalCode() {
        return shippingAddressPostalCode;
    }

    public void setShippingAddressPostalCode(String shippingAddressPostalCode) {
        this.shippingAddressPostalCode = shippingAddressPostalCode;
    }

    public String getShippingAddressCountry() {
        return shippingAddressCountry;
    }

    public void setShippingAddressCountry(String shippingAddressCountry) {
        this.shippingAddressCountry = shippingAddressCountry;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public static OrderDTOBuilder builder() {
        return new OrderDTOBuilder();
    }

    public static class OrderDTOBuilder {
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

        OrderDTOBuilder() {
        }

        public OrderDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public OrderDTOBuilder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public OrderDTOBuilder status(String status) {
            this.status = status;
            return this;
        }

        public OrderDTOBuilder paymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public OrderDTOBuilder customerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public OrderDTOBuilder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public OrderDTOBuilder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public OrderDTOBuilder customerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }

        public OrderDTOBuilder totalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public OrderDTOBuilder subtotalAmount(Double subtotalAmount) {
            this.subtotalAmount = subtotalAmount;
            return this;
        }

        public OrderDTOBuilder taxAmount(Double taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        public OrderDTOBuilder shippingCost(Double shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }

        public OrderDTOBuilder discountAmount(Double discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        public OrderDTOBuilder shippingAddressStreet(String shippingAddressStreet) {
            this.shippingAddressStreet = shippingAddressStreet;
            return this;
        }

        public OrderDTOBuilder shippingAddressCity(String shippingAddressCity) {
            this.shippingAddressCity = shippingAddressCity;
            return this;
        }

        public OrderDTOBuilder shippingAddressState(String shippingAddressState) {
            this.shippingAddressState = shippingAddressState;
            return this;
        }

        public OrderDTOBuilder shippingAddressPostalCode(String shippingAddressPostalCode) {
            this.shippingAddressPostalCode = shippingAddressPostalCode;
            return this;
        }

        public OrderDTOBuilder shippingAddressCountry(String shippingAddressCountry) {
            this.shippingAddressCountry = shippingAddressCountry;
            return this;
        }

        public OrderDTOBuilder promoCode(String promoCode) {
            this.promoCode = promoCode;
            return this;
        }

        public OrderDTOBuilder promotionId(Long promotionId) {
            this.promotionId = promotionId;
            return this;
        }

        public OrderDTOBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public OrderDTOBuilder internalNotes(String internalNotes) {
            this.internalNotes = internalNotes;
            return this;
        }

        public OrderDTOBuilder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public OrderDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public OrderDTOBuilder estimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
            this.estimatedDeliveryDate = estimatedDeliveryDate;
            return this;
        }

        public OrderDTOBuilder actualDeliveryDate(LocalDateTime actualDeliveryDate) {
            this.actualDeliveryDate = actualDeliveryDate;
            return this;
        }

        public OrderDTOBuilder shipmentId(Long shipmentId) {
            this.shipmentId = shipmentId;
            return this;
        }

        public OrderDTOBuilder shipmentStatus(String shipmentStatus) {
            this.shipmentStatus = shipmentStatus;
            return this;
        }

        public OrderDTOBuilder trackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
            return this;
        }

        public OrderDTOBuilder paymentId(Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public OrderDTOBuilder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public OrderDTOBuilder paymentDate(LocalDateTime paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public OrderDTOBuilder items(List<OrderItemDTO> items) {
            this.items = items;
            return this;
        }

        public OrderDTO build() {
            return new OrderDTO(id, orderNumber, status, paymentStatus, customerId, customerName, customerEmail,
                    customerPhone, totalAmount, subtotalAmount, taxAmount, shippingCost, discountAmount,
                    shippingAddressStreet, shippingAddressCity, shippingAddressState, shippingAddressPostalCode,
                    shippingAddressCountry, promoCode, promotionId, notes, internalNotes, orderDate, updatedAt,
                    estimatedDeliveryDate, actualDeliveryDate, shipmentId, shipmentStatus, trackingNumber, paymentId,
                    paymentMethod, paymentDate, items);
        }
    }

    /**
     * Nested DTO for Order Items
     */
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private Double unitPrice;
        private Double totalPrice;
        private String variantInfo;
        private String imageUrl;
        private String notes;

        public OrderItemDTO() {
        }

        public OrderItemDTO(Long id, Long productId, String productName, String productSku, Integer quantity,
                Double unitPrice, Double totalPrice, String variantInfo, String imageUrl, String notes) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.productSku = productSku;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.variantInfo = variantInfo;
            this.imageUrl = imageUrl;
            this.notes = notes;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductSku() {
            return productSku;
        }

        public void setProductSku(String productSku) {
            this.productSku = productSku;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getVariantInfo() {
            return variantInfo;
        }

        public void setVariantInfo(String variantInfo) {
            this.variantInfo = variantInfo;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public static OrderItemDTOBuilder builder() {
            return new OrderItemDTOBuilder();
        }

        public static class OrderItemDTOBuilder {
            private Long id;
            private Long productId;
            private String productName;
            private String productSku;
            private Integer quantity;
            private Double unitPrice;
            private Double totalPrice;
            private String variantInfo;
            private String imageUrl;
            private String notes;

            OrderItemDTOBuilder() {
            }

            public OrderItemDTOBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public OrderItemDTOBuilder productId(Long productId) {
                this.productId = productId;
                return this;
            }

            public OrderItemDTOBuilder productName(String productName) {
                this.productName = productName;
                return this;
            }

            public OrderItemDTOBuilder productSku(String productSku) {
                this.productSku = productSku;
                return this;
            }

            public OrderItemDTOBuilder quantity(Integer quantity) {
                this.quantity = quantity;
                return this;
            }

            public OrderItemDTOBuilder unitPrice(Double unitPrice) {
                this.unitPrice = unitPrice;
                return this;
            }

            public OrderItemDTOBuilder totalPrice(Double totalPrice) {
                this.totalPrice = totalPrice;
                return this;
            }

            public OrderItemDTOBuilder variantInfo(String variantInfo) {
                this.variantInfo = variantInfo;
                return this;
            }

            public OrderItemDTOBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public OrderItemDTOBuilder notes(String notes) {
                this.notes = notes;
                return this;
            }

            public OrderItemDTO build() {
                return new OrderItemDTO(id, productId, productName, productSku, quantity, unitPrice, totalPrice, variantInfo, imageUrl, notes);
            }
        }
    }
}
