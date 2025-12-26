package com.minari.ecommerce.config;

import com.minari.ecommerce.entity.User;
import com.minari.ecommerce.entity.Admin;
import com.minari.ecommerce.entity.Customer;
import com.minari.ecommerce.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find user by email or username
        User user = userRepository.findByEmailOrUsername(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));
        
        System.out.println("🔍 DEBUG UserDetailsService - Loading user: " + identifier);
        System.out.println("   User class type: " + user.getClass().getSimpleName());
        System.out.println("   User role: " + user.getRole());
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                getAuthorities(user)
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Check if user is instance of Admin
        if (user instanceof Admin) {
            System.out.println("   ✅ User is Admin - adding ROLE_ADMIN");
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } 
        // Check if user is instance of Customer
        else if (user instanceof Customer) {
            System.out.println("   ✅ User is Customer - adding ROLE_CUSTOMER");
            authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
        // Fallback to role field
        else if (user.getRole() != null) {
            System.out.println("   ℹ️ Using role field: " + user.getRole());
            switch (user.getRole()) {
                case ADMIN:
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    break;
                case CUSTOMER:
                    authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
                    break;
                case GUEST:
                    authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
                    break;
            }
        } else {
            System.out.println("   ⚠️ No role detected for user!");
            authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        }
        
        return authorities;
    }
}

