package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_logs")
public class ShipmentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    
    private String location;
    private String description;
    
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    // Constructors
    public ShipmentLog() {}

    public ShipmentLog(String location, String description, String status, ShipmentStatus shipmentStatus) {
        this.location = location;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = shipmentStatus;
    }

    public ShipmentLog(Long id, Shipment shipment, String location, String description, LocalDateTime timestamp, ShipmentStatus status) {
        this.id = id;
        this.shipment = shipment;
        this.location = location;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}