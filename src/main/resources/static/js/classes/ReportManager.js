/**
 * ReportManager.js
 * ================
 * Complete reporting and dashboard system
 * Aggregates data from orders, products, and customers
 * Generates charts, statistics, and insights
 * 
 * Part of Phase 5: Report/Dashboard Implementation
 */

/**
 * ReportManager
 * Main orchestrator for reporting functionality
 */
class ReportManager {
    constructor() {
        this.orders = [];
        this.products = [];
        this.categories = [];
        this.customers = new Map();
        
        // Initialize validators
        this.validators = {
            dateRange: new DateRangeValidator(),
            reportType: new ReportTypeValidator(),
            format: new FormatValidator(),
            categoryFilter: new CategoryFilterValidator(),
            statusFilter: new StatusFilterValidator(),
            minValue: new MinValueValidator(),
            limit: new LimitValidator()
        };
    }

    /**
     * Set data sources
     */
    setDataSources(orders, products, categories, customers) {
        this.orders = orders || [];
        this.products = products || [];
        this.categories = categories || [];
        this.customers = customers || new Map();
    }

    /**
     * Generate sales report
     */
    generateSalesReport(startDate, endDate, options = {}) {
        // Validate dates
        if (!this.validators.dateRange.validate({ startDate, endDate })) {
            return {
                success: false,
                error: this.validators.dateRange.getErrorMessage()
            };
        }

        const start = new Date(startDate);
        const end = new Date(endDate);

        const filteredOrders = this.orders.filter(order => {
            const orderDate = new Date(order.orderDate);
            return orderDate >= start && orderDate <= end && order.paymentStatus === 'paid';
        });

        const dailySales = {};
        let totalRevenue = 0;
        let totalOrders = 0;

        filteredOrders.forEach(order => {
            const dateKey = order.orderDate.toISOString().split('T')[0];
            if (!dailySales[dateKey]) {
                dailySales[dateKey] = { revenue: 0, orders: 0, items: 0 };
            }
            dailySales[dateKey].revenue += order.total;
            dailySales[dateKey].orders += 1;
            dailySales[dateKey].items += order.itemCount || 0;
            totalRevenue += order.total;
            totalOrders += 1;
        });

        return {
            success: true,
            reportType: 'sales',
            dateRange: { startDate: start, endDate: end },
            summary: {
                totalRevenue: totalRevenue,
                totalOrders: totalOrders,
                averageOrderValue: totalOrders > 0 ? totalRevenue / totalOrders : 0,
                totalItems: Object.values(dailySales).reduce((sum, day) => sum + day.items, 0)
            },
            dailySales: dailySales,
            generatedAt: new Date()
        };
    }

    /**
     * Generate order status report
     */
    generateOrderStatusReport(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        const filteredOrders = this.orders.filter(order => {
            const orderDate = new Date(order.orderDate);
            return orderDate >= start && orderDate <= end;
        });

        const statusBreakdown = {
            pending: { count: 0, total: 0, percentage: 0 },
            processing: { count: 0, total: 0, percentage: 0 },
            completed: { count: 0, total: 0, percentage: 0 },
            cancelled: { count: 0, total: 0, percentage: 0 },
            failed: { count: 0, total: 0, percentage: 0 },
            refunded: { count: 0, total: 0, percentage: 0 }
        };

        const paymentBreakdown = {
            pending: { count: 0, total: 0, percentage: 0 },
            paid: { count: 0, total: 0, percentage: 0 },
            failed: { count: 0, total: 0, percentage: 0 },
            refunded: { count: 0, total: 0, percentage: 0 },
            partial: { count: 0, total: 0, percentage: 0 }
        };

        filteredOrders.forEach(order => {
            if (statusBreakdown[order.status]) {
                statusBreakdown[order.status].count += 1;
                statusBreakdown[order.status].total += order.total;
            }
            if (paymentBreakdown[order.paymentStatus]) {
                paymentBreakdown[order.paymentStatus].count += 1;
                paymentBreakdown[order.paymentStatus].total += order.total;
            }
        });

        // Calculate percentages
        const totalCount = filteredOrders.length;
        Object.keys(statusBreakdown).forEach(status => {
            statusBreakdown[status].percentage = totalCount > 0 ? 
                (statusBreakdown[status].count / totalCount * 100).toFixed(1) : 0;
        });

        Object.keys(paymentBreakdown).forEach(status => {
            paymentBreakdown[status].percentage = totalCount > 0 ? 
                (paymentBreakdown[status].count / totalCount * 100).toFixed(1) : 0;
        });

        return {
            success: true,
            reportType: 'orderStatus',
            dateRange: { startDate: start, endDate: end },
            summary: {
                totalOrders: totalCount,
                completedOrders: statusBreakdown.completed.count,
                pendingOrders: statusBreakdown.pending.count,
                paidOrders: paymentBreakdown.paid.count
            },
            statusBreakdown: statusBreakdown,
            paymentBreakdown: paymentBreakdown,
            generatedAt: new Date()
        };
    }

    /**
     * Generate customer report
     */
    generateCustomerReport(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        const filteredOrders = this.orders.filter(order => {
            const orderDate = new Date(order.orderDate);
            return orderDate >= start && orderDate <= end;
        });

        const customerMetrics = {};
        let totalCustomers = 0;
        let newCustomers = 0;

        filteredOrders.forEach(order => {
            if (!customerMetrics[order.customerId]) {
                customerMetrics[order.customerId] = {
                    customerId: order.customerId,
                    orderCount: 0,
                    totalSpent: 0,
                    firstOrder: order.orderDate,
                    lastOrder: order.orderDate
                };
                newCustomers += 1;
            }
            customerMetrics[order.customerId].orderCount += 1;
            customerMetrics[order.customerId].totalSpent += order.total;
            customerMetrics[order.customerId].lastOrder = new Date(Math.max(
                new Date(customerMetrics[order.customerId].lastOrder),
                order.orderDate
            ));
        });

        totalCustomers = Object.keys(customerMetrics).length;

        // Top customers
        const topCustomers = Object.values(customerMetrics)
            .sort((a, b) => b.totalSpent - a.totalSpent)
            .slice(0, 10);

        // Repeat customers
        const repeatCustomers = Object.values(customerMetrics)
            .filter(c => c.orderCount > 1).length;

        return {
            success: true,
            reportType: 'customers',
            dateRange: { startDate: start, endDate: end },
            summary: {
                totalCustomers: totalCustomers,
                newCustomers: newCustomers,
                repeatCustomers: repeatCustomers,
                repeatCustomerRate: totalCustomers > 0 ? 
                    (repeatCustomers / totalCustomers * 100).toFixed(1) + '%' : '0%',
                averageOrderPerCustomer: totalCustomers > 0 ? 
                    (filteredOrders.length / totalCustomers).toFixed(2) : 0
            },
            topCustomers: topCustomers,
            generatedAt: new Date()
        };
    }

    /**
     * Generate product performance report
     */
    generateProductReport(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        const filteredOrders = this.orders.filter(order => {
            const orderDate = new Date(order.orderDate);
            return orderDate >= start && orderDate <= end;
        });

        const productSales = {};

        filteredOrders.forEach(order => {
            if (order.items && Array.isArray(order.items)) {
                order.items.forEach(item => {
                    const productId = item.productId || item.id;
                    if (!productSales[productId]) {
                        productSales[productId] = {
                            productId: productId,
                            productName: item.name || 'Unknown',
                            quantity: 0,
                            revenue: 0,
                            orders: 0
                        };
                    }
                    productSales[productId].quantity += item.quantity || 1;
                    productSales[productId].revenue += item.price * (item.quantity || 1) || 0;
                    productSales[productId].orders += 1;
                });
            }
        });

        // Top products by revenue
        const topProductsByRevenue = Object.values(productSales)
            .sort((a, b) => b.revenue - a.revenue)
            .slice(0, 10);

        // Top products by quantity
        const topProductsByQuantity = Object.values(productSales)
            .sort((a, b) => b.quantity - a.quantity)
            .slice(0, 10);

        return {
            success: true,
            reportType: 'products',
            dateRange: { startDate: start, endDate: end },
            summary: {
                totalProductsSold: Object.keys(productSales).length,
                totalQuantity: Object.values(productSales).reduce((sum, p) => sum + p.quantity, 0),
                totalRevenue: Object.values(productSales).reduce((sum, p) => sum + p.revenue, 0)
            },
            topByRevenue: topProductsByRevenue,
            topByQuantity: topProductsByQuantity,
            generatedAt: new Date()
        };
    }

    /**
     * Generate inventory report
     */
    generateInventoryReport() {
        const inventory = {
            lowStock: [],
            outOfStock: [],
            totalValue: 0,
            byCategory: {}
        };

        this.products.forEach(product => {
            const stock = product.stock || 0;
            const price = product.price || 0;
            const value = stock * price;

            inventory.totalValue += value;

            if (stock === 0) {
                inventory.outOfStock.push({
                    productId: product.id,
                    name: product.name,
                    category: product.category,
                    price: price
                });
            } else if (stock < 10) {
                inventory.lowStock.push({
                    productId: product.id,
                    name: product.name,
                    category: product.category,
                    stock: stock,
                    price: price,
                    value: value
                });
            }

            // By category
            if (!inventory.byCategory[product.category]) {
                inventory.byCategory[product.category] = {
                    total: 0,
                    value: 0,
                    items: 0
                };
            }
            inventory.byCategory[product.category].total += stock;
            inventory.byCategory[product.category].value += value;
            inventory.byCategory[product.category].items += 1;
        });

        return {
            success: true,
            reportType: 'inventory',
            summary: {
                totalProducts: this.products.length,
                outOfStockCount: inventory.outOfStock.length,
                lowStockCount: inventory.lowStock.length,
                totalInventoryValue: inventory.totalValue
            },
            lowStock: inventory.lowStock,
            outOfStock: inventory.outOfStock,
            byCategory: inventory.byCategory,
            generatedAt: new Date()
        };
    }

    /**
     * Generate revenue report with trends
     */
    generateRevenueReport(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        const filteredOrders = this.orders.filter(order => {
            const orderDate = new Date(order.orderDate);
            return orderDate >= start && orderDate <= end && order.paymentStatus === 'paid';
        });

        const weeklyRevenue = {};
        const monthlyRevenue = {};
        let totalRevenue = 0;
        let maxDailyRevenue = 0;

        filteredOrders.forEach(order => {
            const orderDate = new Date(order.orderDate);
            
            // Daily
            const dateKey = orderDate.toISOString().split('T')[0];
            const dailyRev = order.total;

            // Weekly
            const week = this.getWeekNumber(orderDate);
            const weekKey = `Week ${week}`;
            
            // Monthly
            const monthKey = orderDate.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });

            if (!weeklyRevenue[weekKey]) {
                weeklyRevenue[weekKey] = { revenue: 0, orders: 0 };
            }
            weeklyRevenue[weekKey].revenue += dailyRev;
            weeklyRevenue[weekKey].orders += 1;

            if (!monthlyRevenue[monthKey]) {
                monthlyRevenue[monthKey] = { revenue: 0, orders: 0 };
            }
            monthlyRevenue[monthKey].revenue += dailyRev;
            monthlyRevenue[monthKey].orders += 1;

            totalRevenue += dailyRev;
            maxDailyRevenue = Math.max(maxDailyRevenue, dailyRev);
        });

        return {
            success: true,
            reportType: 'revenue',
            dateRange: { startDate: start, endDate: end },
            summary: {
                totalRevenue: totalRevenue,
                orderCount: filteredOrders.length,
                averageOrderValue: filteredOrders.length > 0 ? 
                    (totalRevenue / filteredOrders.length).toFixed(2) : 0,
                maxDailyRevenue: maxDailyRevenue
            },
            weeklyRevenue: weeklyRevenue,
            monthlyRevenue: monthlyRevenue,
            generatedAt: new Date()
        };
    }

    /**
     * Get top performing items
     */
    getTopPerformers(startDate, endDate, limit = 10) {
        if (!this.validators.limit.validate(limit)) {
            return {
                success: false,
                error: this.validators.limit.getErrorMessage()
            };
        }

        const start = new Date(startDate);
        const end = new Date(endDate);

        const filteredOrders = this.orders.filter(order => {
            const orderDate = new Date(order.orderDate);
            return orderDate >= start && orderDate <= end;
        });

        // Top products
        const productMetrics = {};
        filteredOrders.forEach(order => {
            if (order.items && Array.isArray(order.items)) {
                order.items.forEach(item => {
                    const productId = item.productId || item.id;
                    if (!productMetrics[productId]) {
                        productMetrics[productId] = {
                            productId: productId,
                            name: item.name || 'Unknown',
                            quantity: 0,
                            revenue: 0,
                            popularity: 0
                        };
                    }
                    productMetrics[productId].quantity += item.quantity || 1;
                    productMetrics[productId].revenue += item.price * (item.quantity || 1) || 0;
                    productMetrics[productId].popularity += 1;
                });
            }
        });

        const topProducts = Object.values(productMetrics)
            .sort((a, b) => b.revenue - a.revenue)
            .slice(0, limit);

        // Top customers
        const customerMetrics = {};
        filteredOrders.forEach(order => {
            if (!customerMetrics[order.customerId]) {
                customerMetrics[order.customerId] = {
                    customerId: order.customerId,
                    orderCount: 0,
                    totalSpent: 0,
                    lastOrder: order.orderDate
                };
            }
            customerMetrics[order.customerId].orderCount += 1;
            customerMetrics[order.customerId].totalSpent += order.total;
            customerMetrics[order.customerId].lastOrder = new Date(Math.max(
                new Date(customerMetrics[order.customerId].lastOrder),
                order.orderDate
            ));
        });

        const topCustomers = Object.values(customerMetrics)
            .sort((a, b) => b.totalSpent - a.totalSpent)
            .slice(0, limit);

        return {
            success: true,
            dateRange: { startDate: start, endDate: end },
            topProducts: topProducts,
            topCustomers: topCustomers,
            generatedAt: new Date()
        };
    }

    /**
     * Calculate KPIs
     */
    calculateKPIs(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        const filteredOrders = this.orders.filter(order => {
            const orderDate = new Date(order.orderDate);
            return orderDate >= start && orderDate <= end;
        });

        const paidOrders = filteredOrders.filter(o => o.paymentStatus === 'paid');
        const totalRevenue = paidOrders.reduce((sum, o) => sum + o.total, 0);
        
        // Conversion metrics
        const totalOrdersPlaced = filteredOrders.length;
        const completedOrders = filteredOrders.filter(o => o.status === 'completed').length;
        const cancelledOrders = filteredOrders.filter(o => o.status === 'cancelled').length;

        // Customer metrics
        const uniqueCustomers = new Set(filteredOrders.map(o => o.customerId)).size;
        const repeatOrders = filteredOrders.filter(o => {
            const customerOrders = filteredOrders.filter(order => order.customerId === o.customerId);
            return customerOrders.length > 1;
        }).length;

        return {
            success: true,
            kpis: {
                totalRevenue: totalRevenue,
                totalOrders: totalOrdersPlaced,
                completionRate: totalOrdersPlaced > 0 ? 
                    (completedOrders / totalOrdersPlaced * 100).toFixed(1) + '%' : '0%',
                cancellationRate: totalOrdersPlaced > 0 ? 
                    (cancelledOrders / totalOrdersPlaced * 100).toFixed(1) + '%' : '0%',
                averageOrderValue: totalOrdersPlaced > 0 ? 
                    (totalRevenue / totalOrdersPlaced).toFixed(2) : 0,
                uniqueCustomers: uniqueCustomers,
                repeatOrderRate: uniqueCustomers > 0 ? 
                    (repeatOrders / uniqueCustomers / totalOrdersPlaced * 100).toFixed(1) + '%' : '0%',
                paymentSuccessRate: totalOrdersPlaced > 0 ? 
                    (paidOrders.length / totalOrdersPlaced * 100).toFixed(1) + '%' : '0%'
            },
            generatedAt: new Date()
        };
    }

    /**
     * Export report
     */
    exportReport(report, format) {
        if (!this.validators.format.validate(format)) {
            return {
                success: false,
                error: this.validators.format.getErrorMessage()
            };
        }

        if (format === 'json') {
            return {
                success: true,
                data: JSON.stringify(report, null, 2),
                mimeType: 'application/json'
            };
        } else if (format === 'csv') {
            const csv = this.convertToCSV(report);
            return {
                success: true,
                data: csv,
                mimeType: 'text/csv'
            };
        }

        return {
            success: false,
            error: 'Unsupported format'
        };
    }

    /**
     * Convert report to CSV
     */
    convertToCSV(report) {
        let csv = `Report Type: ${report.reportType}\n`;
        csv += `Generated: ${report.generatedAt}\n\n`;

        if (report.summary) {
            csv += 'SUMMARY\n';
            Object.keys(report.summary).forEach(key => {
                csv += `${key},${report.summary[key]}\n`;
            });
            csv += '\n';
        }

        return csv;
    }

    /**
     * Utility: Get week number
     */
    getWeekNumber(date) {
        const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
        const dayNum = d.getUTCDay() || 7;
        d.setUTCDate(d.getUTCDate() + 4 - dayNum);
        const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
        return Math.ceil((((d - yearStart) / 86400000) + 1) / 7);
    }
}

// Export manager
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ReportManager;
}
