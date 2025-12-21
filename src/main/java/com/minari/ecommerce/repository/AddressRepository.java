package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minari.ecommerce.entity.Customer;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomer(Customer customer);
    
    @Query("SELECT a FROM Address a WHERE a.customer = ?1 AND (a.addressType IS NULL OR a.addressType != 'SHIPPING_ORDER')")
    List<Address> findSavedAddressesByCustomer(Customer customer);
}