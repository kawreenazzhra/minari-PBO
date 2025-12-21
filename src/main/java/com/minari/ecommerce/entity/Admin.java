package com.minari.ecommerce.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_level", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'REGULAR'")
    private AdminLevel adminLevel = AdminLevel.REGULAR;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "permissions", columnDefinition = "TEXT")
    private String permissions; // JSON string of permissions
    
    @Override
    public boolean login(String username, String password) {
        boolean success = this.getEmail().equals(username) && 
                         this.getPassword().equals(password);
        if (success) {
            this.setLastLogin(java.time.LocalDateTime.now());
        }
        return success;
    }
    
    public enum AdminLevel {
        SUPER, REGULAR, SUPPORT
    }
}