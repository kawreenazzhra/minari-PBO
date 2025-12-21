package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Shipment findByOrderNumber(String orderNumber);
}