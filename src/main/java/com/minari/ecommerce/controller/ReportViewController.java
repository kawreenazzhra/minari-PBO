package com.minari.ecommerce.controller;

import com.minari.ecommerce.service.OrderService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ReportViewController
 * ====================
 * MVC Controller for report and dashboard pages
 * Handles server-side rendering of reports
 * 
 * Part of Phase 5: Report/Dashboard Implementation
 */
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportViewController {

    private static final Logger log = LoggerFactory.getLogger(ReportViewController.class);

    private final OrderService orderService;

    public ReportViewController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /admin/reports
     * Main reports dashboard page
     */
    @GetMapping
    public String reportsDashboard(Model model) {
        log.info("Loading reports dashboard");

        try {
            // Get overall statistics
            Map<String, Object> stats = orderService.getOrderStatistics();
            model.addAttribute("statistics", stats);

            // Get top customers
            model.addAttribute("topCustomers", orderService.getTopCustomers(5));

            // Get current month stats
            LocalDate today = LocalDate.now();
            LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = today.atTime(23, 59, 59);

            Map<String, Object> monthStats = orderService.getStatsByDateRange(
                    monthStart.toLocalDate().toString(),
                    monthEnd.toLocalDate().toString());
            model.addAttribute("monthStats", monthStats);

            model.addAttribute("currentPage", "reports");

            return "admin/reports-oop";
        } catch (Exception e) {
            log.error("Error loading reports dashboard", e);
            model.addAttribute("error", "Failed to load reports");
            return "admin/reports-oop";
        }
    }

    /**
     * GET /admin/reports/sales
     * Sales report page
     */
    @GetMapping("/sales")
    public String salesReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        log.info("Loading sales report - startDate: {}, endDate: {}", startDate, endDate);

        try {
            // Default to last 30 days if not specified
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusDays(30);

            if (startDate != null && !startDate.isBlank()) {
                start = LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.isBlank()) {
                end = LocalDate.parse(endDate);
            }

            // Get sales data
            Map<String, Object> salesReport = orderService.getStatsByDateRange(
                    start.toString(),
                    end.toString());

            model.addAttribute("salesReport", salesReport);
            model.addAttribute("startDate", start);
            model.addAttribute("endDate", end);
            model.addAttribute("currentPage", "reports");

            return "admin/reports/sales";
        } catch (Exception e) {
            log.error("Error loading sales report", e);
            model.addAttribute("error", "Failed to load sales report");
            return "admin/reports/sales";
        }
    }

    /**
     * GET /admin/reports/orders
     * Order status report page
     */
    @GetMapping("/orders")
    public String orderStatusReport(Model model) {
        log.info("Loading order status report");

        try {
            // Get order statistics
            Map<String, Object> orderStats = orderService.getOrderCountByStatus();
            model.addAttribute("orderStats", orderStats);

            // Get overall statistics
            Map<String, Object> stats = orderService.getOrderStatistics();
            model.addAttribute("statistics", stats);

            model.addAttribute("currentPage", "reports");

            return "admin/reports/orders";
        } catch (Exception e) {
            log.error("Error loading order status report", e);
            model.addAttribute("error", "Failed to load order report");
            return "admin/reports/orders";
        }
    }

    /**
     * GET /admin/reports/customers
     * Customer analysis report page
     */
    @GetMapping("/customers")
    public String customerReport(Model model) {
        log.info("Loading customer report");

        try {
            // Get top customers
            model.addAttribute("topCustomers", orderService.getTopCustomers(20));

            // Get overall statistics
            Map<String, Object> stats = orderService.getOrderStatistics();
            model.addAttribute("statistics", stats);

            model.addAttribute("currentPage", "reports");

            return "admin/reports/customers";
        } catch (Exception e) {
            log.error("Error loading customer report", e);
            model.addAttribute("error", "Failed to load customer report");
            return "admin/reports/customers";
        }
    }

    /**
     * GET /admin/reports/revenue
     * Revenue trend report page
     */
    @GetMapping("/revenue")
    public String revenueReport(
            @RequestParam(required = false) String period,
            Model model) {

        log.info("Loading revenue report - period: {}", period);

        try {
            String periodType = period != null ? period : "month";

            // Default to last 3 months
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(3);

            // Get revenue data
            Map<String, Object> revenueData = orderService.getStatsByDateRange(
                    startDate.toString(),
                    endDate.toString());

            model.addAttribute("revenueData", revenueData);
            model.addAttribute("periodType", periodType);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("currentPage", "reports");

            return "admin/reports/revenue";
        } catch (Exception e) {
            log.error("Error loading revenue report", e);
            model.addAttribute("error", "Failed to load revenue report");
            return "admin/reports/revenue";
        }
    }

    /**
     * GET /admin/reports/kpi
     * KPI dashboard page
     */
    @GetMapping("/kpi")
    public String kpiDashboard(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        log.info("Loading KPI dashboard");

        try {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusDays(30);

            if (startDate != null && !startDate.isBlank()) {
                start = LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.isBlank()) {
                end = LocalDate.parse(endDate);
            }

            // Get statistics
            Map<String, Object> stats = orderService.getOrderStatistics();
            Map<String, Object> monthStats = orderService.getStatsByDateRange(
                    start.toString(),
                    end.toString());

            model.addAttribute("statistics", stats);
            model.addAttribute("monthStats", monthStats);
            model.addAttribute("startDate", start);
            model.addAttribute("endDate", end);
            model.addAttribute("currentPage", "reports");

            return "admin/reports/kpi";
        } catch (Exception e) {
            log.error("Error loading KPI dashboard", e);
            model.addAttribute("error", "Failed to load KPI dashboard");
            return "admin/reports/kpi";
        }
    }

    /**
     * GET /admin/reports/export
     * Export report functionality
     */
    @GetMapping("/export")
    public String exportReport(
            @RequestParam String type,
            @RequestParam String format,
            Model model) {

        log.info("Exporting report - type: {}, format: {}", type, format);

        try {
            // This would typically return a file download
            // Implementation depends on report type and format

            model.addAttribute("reportType", type);
            model.addAttribute("format", format);
            model.addAttribute("message", "Export functionality will be implemented");
            model.addAttribute("currentPage", "reports");

            return "admin/reports";
        } catch (Exception e) {
            log.error("Error exporting report", e);
            model.addAttribute("error", "Failed to export report");
            return "admin/reports";
        }
    }

    /**
     * GET /admin/reports/date-range
     * Get data for date range (AJAX)
     */
    @GetMapping("/date-range")
    @ResponseBody
    public Map<String, Object> getDateRangeData(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        log.info("Fetching data for date range: {} to {}", startDate, endDate);

        try {
            Map<String, Object> data = orderService.getStatsByDateRange(startDate, endDate);
            data.put("success", true);
            return data;
        } catch (Exception e) {
            log.error("Error fetching date range data", e);
            return Map.of(
                    "success", false,
                    "error", e.getMessage());
        }
    }

    /**
     * GET /admin/reports/status-breakdown
     * Get order status breakdown (AJAX)
     */
    @GetMapping("/status-breakdown")
    @ResponseBody
    public Map<String, Object> getStatusBreakdown() {
        log.info("Fetching status breakdown");

        try {
            Map<String, Object> data = orderService.getOrderCountByStatus();
            data.put("success", true);
            return data;
        } catch (Exception e) {
            log.error("Error fetching status breakdown", e);
            return Map.of(
                    "success", false,
                    "error", e.getMessage());
        }
    }

    /**
     * GET /admin/reports/top-customers
     * Get top customers data (AJAX)
     */
    @GetMapping("/top-customers")
    @ResponseBody
    public Map<String, Object> getTopCustomersData(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Fetching top {} customers", limit);

        try {
            return Map.of(
                    "success", true,
                    "data", orderService.getTopCustomers(limit),
                    "count", orderService.getTopCustomers(limit).size());
        } catch (Exception e) {
            log.error("Error fetching top customers", e);
            return Map.of(
                    "success", false,
                    "error", e.getMessage());
        }
    }
}
