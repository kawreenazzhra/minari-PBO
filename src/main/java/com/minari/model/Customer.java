package com.minari.model;

import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User implements Notification {
    private String fullName;
    private String address;
    private String phoneNumber;
    private ShoppingCart cart;
    private List<order> orderHistory;

    public Customer(int userId, String username, String email, String password,
                    String fullName, String address, String phoneNumber) {
        super(userId, username, email, password);
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cart = new ShoppingCart(this);
        this.orderHistory = new ArrayList<>();
    }

    @Override
    public boolean login(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            System.out.println("Customer " + username + " logged in successfully!");
            return true;
        }
        System.out.println("Login failed for customer " + username);
        return false;
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("📩 Email sent to " + email + ": " + message);
    }

    @Override
    public void showPopupNotification(String message) {
        System.out.println("🔔 Popup: " + message);
    }

    public void addToCart(Product product, int quantity) {
        cart.addItem(product, quantity);
    }

    public void checkout() {
        System.out.println("Customer " + username + " is checking out...");
    }
}
