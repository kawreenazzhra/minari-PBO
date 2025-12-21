package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(name = "order_number")
    private String orderNumber;
    
    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate;
    
    @Column(name = "estimated_arrival")
    private LocalDateTime estimatedArrival;
    
    @Column(name = "shipment_method")
    private String shipmentMethod;
    
    @Column(name = "tracking_number")
    private String trackingNumber;
    
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
    private List<ShipmentLog> shipmentLogs = new ArrayList<>();
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private Address deliveryAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private ShipmentStatus currentStatus;
    
    public Shipment() {}

    public Shipment(String orderNumber, String trackingNumber, String shipmentMethod, Address deliveryAddress) {
        this.orderNumber = orderNumber;
        this.trackingNumber = trackingNumber;
        this.shipmentMethod = shipmentMethod;
        this.deliveryAddress = deliveryAddress;
        this.shipmentDate = LocalDateTime.now();
        this.shipmentLogs = new ArrayList<>();
        this.currentStatus = ShipmentStatus.AWAITING_PICKUP;
        this.estimatedArrival = calculateEstimatedArrival(shipmentMethod);
        
        addShipmentLog("Warehouse", "Shipment created", ShipmentStatus.AWAITING_PICKUP);
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public String getShipmentMethod() {
        return shipmentMethod;
    }

    public void setShipmentMethod(String shipmentMethod) {
        this.shipmentMethod = shipmentMethod;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public List<ShipmentLog> getShipmentLogs() {
        return shipmentLogs;
    }

    public void setShipmentLogs(List<ShipmentLog> shipmentLogs) {
        this.shipmentLogs = shipmentLogs;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public ShipmentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ShipmentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    // Business methods dari class asli
    public void addShipmentLog(String location, String description, ShipmentStatus status) {
        ShipmentLog log = new ShipmentLog();
        log.setShipment(this);
        log.setLocation(location);
        log.setDescription(description);
        log.setStatus(status);
        shipmentLogs.add(log);
        this.currentStatus = status;
    }
    
    public void markAsPickedUp() {
        addShipmentLog("Warehouse", "Picked up by courier", ShipmentStatus.PICKED_UP);
    }
    
    public void updateLocation(String location, String description) {
        addShipmentLog(location, description, ShipmentStatus.IN_TRANSIT);
    }
    
    public void markAsOutForDelivery() {
        if (deliveryAddress != null) {
            addShipmentLog(deliveryAddress.getCity(), "Out for delivery", ShipmentStatus.OUT_FOR_DELIVERY);
        } else {
            addShipmentLog("Unknown", "Out for delivery", ShipmentStatus.OUT_FOR_DELIVERY);
        }
    }
    
    public void markAsDelivered() {
        if (deliveryAddress != null) {
            addShipmentLog(deliveryAddress.getFullAddress(), "Successfully delivered", ShipmentStatus.DELIVERED);
        } else {
            addShipmentLog("Unknown", "Successfully delivered", ShipmentStatus.DELIVERED);
        }
    }
    
    public void markDeliveryFailed(String reason) {
        if (deliveryAddress != null) {
            addShipmentLog(deliveryAddress.getFullAddress(), "Delivery failed: " + reason, ShipmentStatus.DELIVERY_FAILED);
        } else {
            addShipmentLog("Unknown", "Delivery failed: " + reason, ShipmentStatus.DELIVERY_FAILED);
        }
    }
    
    public List<ShipmentLog> getRecentLogs(int count) {
        if (shipmentLogs.size() <= count) {
            return new ArrayList<>(shipmentLogs);
        }
        return shipmentLogs.subList(shipmentLogs.size() - count, shipmentLogs.size());
    }
    
    private LocalDateTime calculateEstimatedArrival(String method) {
        LocalDateTime now = LocalDateTime.now();
        if ("J&TUH".equalsIgnoreCase(method)) {
            // Check if delivery address is in Java island
            if (isInJavaIsland()) {
                return now.plusDays(3);
            } else {
                return now.plusDays(7); // 5-7 days, using maximum for safety
            }
        }

        // Fallback for other methods
        switch (method.toUpperCase()) {
            case "JNE REGULAR": return now.plusDays(5);
            case "JNE YES": return now.plusDays(2);
            case "GOJEK": return now.plusDays(1);
            case "SAME DAY": return now.plusHours(6);
            default: return now.plusDays(3);
        }
    }

    private boolean isInJavaIsland() {
        if (deliveryAddress == null || deliveryAddress.getProvince() == null) {
            return false;
        }

        String province = deliveryAddress.getProvince().toLowerCase();
        // List of provinces in Java island
        return province.contains("jakarta") ||
               province.contains("jawa barat") ||
               province.contains("jawa tengah") ||
               province.contains("jawa timur") ||
               province.contains("yogyakarta") ||
               province.contains("banten");
    }
}