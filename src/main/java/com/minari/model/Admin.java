package com.minari.model;

public class Admin extends User implements Notification {
    private String adminLevel;
    private ProductManager manager;

    public Admin(int userId, String username, String email, String password, String adminLevel) {
        super(userId, username, email, password);
        this.adminLevel = adminLevel;
        this.manager = new ProductManager();
    }

    @Override
    public boolean login(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            System.out.println("Admin " + username + " logged in successfully!");
            return true;
        }
        System.out.println("Login failed for admin " + username);
        return false;
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("📢 Admin notification: " + message);
    }

    @Override
    public void showPopupNotification(String message) {
        System.out.println("🔔 Admin popup: " + message);
    }

    public void addNewProduct(Product product) {
        manager.addProduct(product);
        System.out.println("Product added by admin: " + product.getName());
    }

     public void updateProductInfo(Product product) {
        productManager.updateProduct(product);
        System.out.println("✏️ Product updated: " + product.getName());
    }


    public void removeProduct(Product product) {
        manager.deleteProduct(product);
        System.out.println("Product removed: " + product.getName());
    }

    public void viewAllProducts() {
        System.out.println("📦 All Products in System:");
        for (Product p : productManager.getAllProducts()) {
            System.out.println("- " + p.getName() + " | Stock: " + p.getStock());
        }
    }

    public void updateOrderStatus(Order order, String newStatus) {
        order.setStatus(newStatus);
        sendNotification("Order #" + order.getOrderId() + " status updated to " + newStatus);
    }

    public void generateSalesReport() {
        System.out.println("📊 Generating sales report...");
        double totalSales = 0;
        if (orderList != null) {
            for (Order o : orderList) {
                totalSales += o.getTotalAmount();
            }
        }
        System.out.println("Total sales amount: Rp " + totalSales);
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
}
