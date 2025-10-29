package com.minari.model;

public class OrderItem {
    private int orderItemID;
    private Product product; // ASSOCIATION: OrderItem mengacu ke Product
    private int quantity;
    private double price;

    public OrderItem(CartItem cartItem) {
        this.orderItemID = generateOrderItemID();
        this.product = cartItem.getProduct(); // ASSOCIATION: Product dari CartItem
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getSubtotal();
    }

    // Constructor alternatif langsung dengan Product
    public OrderItem(Product product, int quantity) {
        this.orderItemID = generateOrderItemID();
        this.product = product; // ASSOCIATION: Reference ke Product
        this.quantity = quantity;
        this.price = product.getPrice() * quantity;
    }

    // Methods sesuai diagram
    public Product getProduct() {
        return product; // ASSOCIATION: Mengembalikan reference ke Product
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    // ASSOCIATION: Product bisa diganti (karena Association, bukan Composition)
    public void setProduct(Product product) {
        this.product = product;
        recalculatePrice();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculatePrice();
    }

    private void recalculatePrice() {
        if (product != null) {
            this.price = product.getPrice() * quantity;
        }
    }

    private int generateOrderItemID() {
        return (int) (System.currentTimeMillis() % 100000);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
