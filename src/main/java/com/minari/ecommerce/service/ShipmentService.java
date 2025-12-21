package com.minari.ecommerce.service;

import com.minari.ecommerce.entity.Shipment;
import com.minari.ecommerce.entity.ShipmentStatus;
import com.minari.ecommerce.entity.PaymentStatus;
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
    
    /**
     * Update shipment status and automatically mark COD payments as PAID when shipped
     */
    public Shipment updateShipmentStatus(Shipment shipment, ShipmentStatus newStatus) {
        shipment.setCurrentStatus(newStatus);
        
        // If shipment is marked as shipped and order has COD payment that's still PENDING,
        // mark the payment as PAID
        if (newStatus == ShipmentStatus.PICKED_UP && shipment.getOrder() != null) {
            if (shipment.getOrder().getPayment() != null &&
                shipment.getOrder().getPayment().getPaymentMethod().toString().equals("COD") &&
                shipment.getOrder().getPayment().getStatus() == PaymentStatus.PENDING) {
                
                shipment.getOrder().getPayment().setStatus(PaymentStatus.PAID);
                shipment.getOrder().getPayment().setPaymentDate(java.time.LocalDateTime.now());
                System.out.println("COD Payment automatically marked as PAID for shipment: " + shipment.getOrderNumber());
            }
        }
        
        return shipmentRepository.save(shipment);
    }
}