package com.minari.ui;

public class AdminDashboardPage extends WebPage {
    private Admin admin;

    public AdminDashboardPage(Admin admin) {
        this.admin = admin;
        this.pageTitle = "MINARI Admin Dashboard";
        this.pageUrl = "/admin/dashboard";
    }

    @Override
    public void render() {
        showHeader();
        System.out.println("Admin Dashboard: " + pageTitle);
        System.out.println("Welcome, " + admin.getUsername());
        showFooter();
    }

    @Override
    public void handleUserInput() {
        System.out.println("Processing admin input...");
    }

    // Methods khusus AdminDashboardPage
    public void showProductManagement() {
        System.out.println("Showing product management");
    }

    public void showOrderManagement() {
        System.out.println("Showing order management");
    }

    public void showSalesReports() {
        System.out.println("Showing sales reports");
    }
}
