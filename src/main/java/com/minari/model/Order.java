package com.minari.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderID;
    private Customer customer;
    private Date orderDate;
    private String status;
    private double totalAmount;
    private List<OrderItem> items; // COMPOSITION: Order memiliki OrderItem
    private Payment payment;

    public Order(Customer customer, ShoppingCart cart) {
        this.orderID = generateOrderID();
        this.customer = customer;
        this.orderDate = new Date();
        this.status = "PENDING";
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        
        // COMPOSITION: Order membuat OrderItem dari CartItem
        // Jika Order dihapus, OrderItem juga ikut hilang
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(cartItem);
            this.items.add(orderItem);
            this.totalAmount += orderItem.getPrice();
        }
    }

    public void placeOrder() {
        this.status = "CONFIRMED";
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public void processPayment(String paymentMethod) {
        this.payment = new Payment(this, paymentMethod);
        this.payment.processPayment();
        this.status = "PAID";
    }

    public void cancelOrder() {
        this.status = "CANCELLED";
    }

    public int getOrderID() {
        return orderID;
    }

    // COMPOSITION: Method untuk mengelola lifecycle OrderItem
    public void addOrderItem(OrderItem item) {
        items.add(item);
        recalculateTotal();
    }

    public void removeOrderItem(OrderItem item) {
        items.remove(item);
        recalculateTotal();
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items); // Return copy untuk encapsulation
    }

    // COMPOSITION: Jika Order dihapus, semua OrderItem juga hilang
    public void clearOrder() {
        items.clear();
        totalAmount = 0.0;
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();
    }

    private int generateOrderID() {
        return (int) (System.currentTimeMillis() % 1000000);
    }
}