package com.minari.model;

public class CartItem {
    private int itemID;
    private Product product;
    private int quantity;
    private double subtotal;

    public CartItem(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
        calculateSubtotal();
    }

    public void calculateSubtotal(){
        if (product != null){
            this.subtotal = product.getPrice() * quantity;
        } else {
            this.subtotal = 0.0;
        }
    }

    public double getSubtotal(){
        calculateSubtotal();
        return subtotal;
    }

    public int getItemID(){
        return itemID;
    }

    public Product getProduct(){
        return product;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
        calculateSubtotal();
    }
}
