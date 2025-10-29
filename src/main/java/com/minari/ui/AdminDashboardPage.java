package com.minari.ui;

import com.minari.model.Admin;

public class AdminDashboardPage extends WebPage {
    private Admin admin;

    
    public AdminDashboardPage(Admin admin) {
        this.admin = admin;
    }

    
    @Override
    public void render() {
        showHeader();
        System.out.println("Rendering Admin Dashboard for " + admin.getName());
        showProductManagement();
        showOrderManagement();
        showSalesReports();
        showFooter();
    }

    @Override
    public void handleUserInput() {
        System.out.println("Handling admin-specific input...");
    }

    @Override
    public void showHeader() {
        System.out.println("=== Admin Dashboard Header ===");
    }

    @Override
    public void showFooter() {
        System.out.println("=== End of Admin Dashboard ===");
    }

    
    public void showProductManagement() {
        System.out.println("Accessing product management...");
    }

    public void showOrderManagement() {
        System.out.println("Accessing order management...");
    }

    public void showSalesReports() {
        System.out.println("Accessing sales reports...");
    }
}
