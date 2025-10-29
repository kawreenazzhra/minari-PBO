package com.minari.model;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private int cartID;
    private Customer customer;
    private List<CartItem> items;
    private double totalAmount;

    public ShoppingCart(Customer customer){
        this.customer = customer;
        this.items = new ArrayList<>();
        this.totalAmount = 0;
    }

    public void addItem(Product product, int quantity){
        for (CartItem item : items){
            if (item.getProduct().equals(product)){
                item.setQuantity(item.getQuantity() + quantity);
                calculateTotal();
                return;
            }
        }
        CartItem newItem = new CartItem(product, quantity);
        items.add(newItem);
        calculateTotal();
    }

    public void removeItem(Product product){
        for (int i = 0; i < items.size(); i++){
            CartItem item = items.get(i);
            if (item.getProduct().equals(product)){
                items.remove(i);
                i--;
            }
        }
        calculateTotal();
    }

    public void updateQuantity(Product product, int newQuantity){
        for (CartItem item : items){
            if (item.getProduct().equals(product)){
                item.setQuantity(newQuantity);
                break;
            }
        }
        calculateTotal();
    }

    public void calculateTotal(){
        totalAmount = 0;
        for (CartItem item : items){
            totalAmount += item.getSubtotal();
        }
    }

    public void clearCart(){
        items.clear();
        totalAmount = 0;
    }

    public int getCartID(){
        return cartID;
    }

    public Customer getCustomer(){
        return customer;
    }

    public double getTotalAmount(){
        return totalAmount;
    }

    public List<CartItem> getItems(){
        return items;
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }
}
