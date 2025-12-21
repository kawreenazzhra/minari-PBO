package com.minari.ecommerce.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_sku")
    private String productSku;

    @Column(name = "variant_info")
    private String variantInfo;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "discount_price")
    private Double discountPrice;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "return_status", length = 20)
    private String returnStatus = "NONE";

    // Constructors
    public OrderItem() {
    }

    public OrderItem(Long id, Order order, Product product, String productName, String productSku, String variantInfo,
            Integer quantity, Double unitPrice, Double discountPrice, Double totalPrice, String imageUrl,
            String returnStatus) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.productName = productName;
        this.productSku = productSku;
        this.variantInfo = variantInfo;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountPrice = discountPrice;
        this.totalPrice = totalPrice;
        this.imageUrl = imageUrl;
        this.returnStatus = returnStatus;
    }

    public OrderItem(Order order, Product product, CartItem cartItem) {
        this.order = order;
        this.product = product;
        this.productName = product.getName();
        this.productSku = product.getSku();
        this.quantity = cartItem.getQuantity();
        this.unitPrice = cartItem.getUnitPrice();
        this.discountPrice = product.getDiscountPrice();
        this.totalPrice = unitPrice * quantity;
        this.imageUrl = product.getImageUrl();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public String getVariantInfo() {
        return variantInfo;
    }

    public void setVariantInfo(String variantInfo) {
        this.variantInfo = variantInfo;
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

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public Double getSubtotal() {
        return unitPrice * quantity;
    }

    public Double getDiscountedSubtotal() {
        return discountPrice != null ? discountPrice * quantity : getSubtotal();
    }
}