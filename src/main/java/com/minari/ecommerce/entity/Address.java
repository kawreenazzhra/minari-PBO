package com.minari.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "street_address", nullable = false)
    private String streetAddress;
    
    @Column(name = "apartment_suite")
    private String apartmentSuite;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String province;
    
    @Column(name = "zipcode", nullable = false)
    private String zipcode;
    
    @Column(nullable = false)
    private String country;
    
    @Column(name = "recipient_name")
    private String recipientName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "is_default", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDefault = false;
    
    @Column(name = "address_type")
    private String addressType; // HOME, OFFICE, BILLING, SHIPPING
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Constructors
    public Address() {}

    public Address(Long id, String streetAddress, String apartmentSuite, String city, String state, String province, String zipcode, String country, String recipientName, String phoneNumber, Boolean isDefault, String addressType, LocalDateTime createdAt, LocalDateTime updatedAt, Customer customer) {
        this.id = id;
        this.streetAddress = streetAddress;
        this.apartmentSuite = apartmentSuite;
        this.city = city;
        this.state = state;
        this.province = province;
        this.zipcode = zipcode;
        this.country = country;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.isDefault = isDefault;
        this.addressType = addressType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.customer = customer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getApartmentSuite() {
        return apartmentSuite;
    }

    public void setApartmentSuite(String apartmentSuite) {
        this.apartmentSuite = apartmentSuite;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        address.append(streetAddress);
        if (apartmentSuite != null && !apartmentSuite.isEmpty()) {
            address.append(", ").append(apartmentSuite);
        }
        address.append(", ").append(city)
               .append(", ").append(province)
               .append(" ").append(zipcode)
               .append(", ").append(country);
        return address.toString();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}