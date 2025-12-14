package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Shipment;
import com.minari.ecommerce.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment trackShipment(String orderNumber) {
        return shipmentRepository.findByOrderNumber(orderNumber);
    }

    public Shipment saveShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }
}