package com.minari.model;

import java.util.List;

public class Admin extends User implements Notification {

    private String adminLevel;
    private ProductManager productManager;
    private List<Order> orderList;

    public Admin(int userId, String username, String email, String password, String adminLevel) {
        super(userId, username, email, password);
        this.adminLevel = adminLevel;
        this.productManager = new ProductManager();
    }

    @Override
    public boolean login(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            System.out.println("✅ Admin " + username + " logged in successfully!");
            return true;
        } else {
            System.out.println("❌ Login failed for admin " + username);
            return false;
        }
    }
    @Override
    public void sendNotification(String message) {
        System.out.println("📢 [Admin Notification]: " + message);
    }

    @Override
    public void showPopupNotification(String message) {
        System.out.println("🔔 Admin Popup: " + message);
    }
    public void addNewProduct(Product product) {
        productManager.addProduct(product);
        sendNotification("New product added: " + product.getName());
    }
    public void updateProduct(String productId, Product updatedProduct) {
        productManager.updateProduct(productId, updatedProduct);
        sendNotification("Product updated: " + updatedProduct.getName());
    }
    public void removeProduct(String productId) {
        boolean deleted = productManager.deleteProduct(productId);
        if (deleted)
            sendNotification("Product deleted: " + productId);
    }

    public void viewAllProducts() {
        productManager.displayAllProducts();
    }
    public void viewLowStockProducts(int threshold) {
        List<Product> lowStock = productManager.getLowStockProducts(threshold);
        System.out.println("⚠️ Low Stock Products (≤ " + threshold + "):");
        if (lowStock.isEmpty()) {
            System.out.println("All products are well-stocked!");
        } else {
            lowStock.forEach(p -> System.out.println("- " + p.getName() + " (" + p.getStock() + " left)"));
        }
    }
    public void updateOrderStatus(Order order, String newStatus) {
        order.setStatus(newStatus);
        sendNotification("Order #" + order.getOrderId() + " updated to: " + newStatus);
    }
    public void generateSalesReport() {
        System.out.println("📊 Sales Report Summary:");
        double total = 0;
        if (orderList != null) {
            for (Order order : orderList) {
                System.out.println("- Order #" + order.getOrderId() + ": Rp " + order.getTotalAmount());
                total += order.getTotalAmount();
            }
        }
        System.out.println("💰 Total Sales: Rp " + total);
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public void setProductManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
