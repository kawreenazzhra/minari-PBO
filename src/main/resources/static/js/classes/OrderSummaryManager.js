/**
 * OrderSummaryManager.js
 * =======================
 * Manages order summary/recap functionality
 * Handles customer order data aggregation and display
 * 
 * Part of Phase 5: Order Summary/Recap Implementation
 * Provides complete order management with customer relations
 */

/**
 * OrderSummaryManager
 * Main orchestrator for order summary functionality
 */
class OrderSummaryManager {
    constructor() {
        this.orders = [];
        this.customers = new Map();
        this.orderStatuses = ['pending', 'processing', 'completed', 'cancelled', 'failed', 'refunded'];
        this.paymentStatuses = ['pending', 'paid', 'failed', 'refunded', 'partial'];
        
        // Initialize validators
        this.validators = {
            orderNumber: new OrderNumberValidator(),
            orderStatus: new OrderStatusValidator(),
            paymentStatus: new PaymentStatusValidator(),
            orderTotal: new OrderTotalValidator(),
            orderDate: new OrderDateValidator(),
            customerId: new CustomerIdValidator(),
            itemCount: new OrderItemCountValidator(),
            shippingAddress: new ShippingAddressValidator(),
            trackingNumber: new TrackingNumberValidator()
        };
    }

    /**
     * Add order to summary
     */
    addOrder(orderData) {
        // Validate all fields
        const validation = this.validateOrder(orderData);
        if (!validation.isValid) {
            return {
                success: false,
                errors: validation.errors
            };
        }

        const order = {
            id: orderData.orderNumber,
            orderNumber: orderData.orderNumber,
            customerId: orderData.customerId,
            orderDate: new Date(orderData.orderDate),
            status: orderData.status.toLowerCase(),
            paymentStatus: orderData.paymentStatus.toLowerCase(),
            total: parseFloat(orderData.total),
            itemCount: parseInt(orderData.itemCount, 10),
            items: orderData.items || [],
            shippingAddress: orderData.shippingAddress,
            trackingNumber: orderData.trackingNumber || null,
            notes: orderData.notes || '',
            createdAt: new Date(),
            updatedAt: new Date()
        };

        this.orders.push(order);
        
        // Link to customer if not already linked
        if (!this.customers.has(orderData.customerId)) {
            this.customers.set(orderData.customerId, {
                customerId: orderData.customerId,
                orders: [order.id],
                totalSpent: order.total,
                totalOrders: 1,
                lastOrderDate: order.orderDate
            });
        } else {
            const customer = this.customers.get(orderData.customerId);
            customer.orders.push(order.id);
            customer.totalSpent += order.total;
            customer.totalOrders += 1;
            customer.lastOrderDate = new Date(Math.max(customer.lastOrderDate, order.orderDate));
        }

        return {
            success: true,
            order: order
        };
    }

    /**
     * Update order status
     */
    updateOrderStatus(orderNumber, newStatus) {
        const statusValidator = new OrderStatusValidator();
        
        if (!statusValidator.validate(newStatus)) {
            return {
                success: false,
                error: statusValidator.getErrorMessage()
            };
        }

        const order = this.getOrderByNumber(orderNumber);
        if (!order) {
            return {
                success: false,
                error: `Order ${orderNumber} not found`
            };
        }

        const oldStatus = order.status;
        order.status = newStatus.toLowerCase();
        order.updatedAt = new Date();

        return {
            success: true,
            message: `Order status updated from ${oldStatus} to ${newStatus}`,
            order: order
        };
    }

    /**
     * Update payment status
     */
    updatePaymentStatus(orderNumber, newStatus) {
        const statusValidator = new PaymentStatusValidator();
        
        if (!statusValidator.validate(newStatus)) {
            return {
                success: false,
                error: statusValidator.getErrorMessage()
            };
        }

        const order = this.getOrderByNumber(orderNumber);
        if (!order) {
            return {
                success: false,
                error: `Order ${orderNumber} not found`
            };
        }

        order.paymentStatus = newStatus.toLowerCase();
        order.updatedAt = new Date();

        return {
            success: true,
            order: order
        };
    }

    /**
     * Get order by order number
     */
    getOrderByNumber(orderNumber) {
        return this.orders.find(order => order.orderNumber === orderNumber);
    }

    /**
     * Get all orders for a customer
     */
    getCustomerOrders(customerId) {
        const customerValidator = new CustomerIdValidator();
        
        if (!customerValidator.validate(customerId)) {
            return {
                success: false,
                error: customerValidator.getErrorMessage()
            };
        }

        const customer = this.customers.get(customerId);
        if (!customer) {
            return {
                success: false,
                error: `Customer ${customerId} not found`,
                orders: []
            };
        }

        const orders = this.orders.filter(order => order.customerId === customerId);
        
        return {
            success: true,
            customer: customer,
            orders: orders,
            totalOrders: orders.length,
            totalSpent: this.calculateCustomerTotalSpent(customerId)
        };
    }

    /**
     * Get customer summary
     */
    getCustomerSummary(customerId) {
        const customer = this.customers.get(customerId);
        if (!customer) {
            return null;
        }

        const orders = this.orders.filter(order => order.customerId === customerId);
        const completedOrders = orders.filter(o => o.status === 'completed');
        const pendingOrders = orders.filter(o => o.status === 'pending');
        
        return {
            customerId: customerId,
            totalOrders: orders.length,
            completedOrders: completedOrders.length,
            pendingOrders: pendingOrders.length,
            totalSpent: customer.totalSpent,
            averageOrderValue: orders.length > 0 ? customer.totalSpent / orders.length : 0,
            lastOrderDate: customer.lastOrderDate,
            registrationDate: customer.registrationDate || null
        };
    }

    /**
     * Calculate customer total spent
     */
    calculateCustomerTotalSpent(customerId) {
        return this.orders
            .filter(order => order.customerId === customerId)
            .reduce((sum, order) => sum + order.total, 0);
    }

    /**
     * Filter orders by status
     */
    getOrdersByStatus(status) {
        const statusValidator = new OrderStatusValidator();
        
        if (!statusValidator.validate(status)) {
            return {
                success: false,
                error: statusValidator.getErrorMessage(),
                orders: []
            };
        }

        const orders = this.orders.filter(order => order.status === status.toLowerCase());
        return {
            success: true,
            status: status.toLowerCase(),
            count: orders.length,
            orders: orders
        };
    }

    /**
     * Filter orders by date range
     */
    getOrdersByDateRange(startDate, endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        if (isNaN(start.getTime()) || isNaN(end.getTime())) {
            return {
                success: false,
                error: 'Invalid date range',
                orders: []
            };
        }

        const orders = this.orders.filter(order => 
            order.orderDate >= start && order.orderDate <= end
        );

        return {
            success: true,
            startDate: start,
            endDate: end,
            count: orders.length,
            orders: orders
        };
    }

    /**
     * Get order statistics
     */
    getOrderStatistics() {
        if (this.orders.length === 0) {
            return {
                totalOrders: 0,
                totalRevenue: 0,
                averageOrderValue: 0,
                statusBreakdown: {},
                paymentStatusBreakdown: {},
                topCustomers: []
            };
        }

        const totalRevenue = this.orders.reduce((sum, order) => sum + order.total, 0);
        const completedRevenue = this.orders
            .filter(o => o.status === 'completed' && o.paymentStatus === 'paid')
            .reduce((sum, order) => sum + order.total, 0);

        // Status breakdown
        const statusBreakdown = {};
        this.orderStatuses.forEach(status => {
            statusBreakdown[status] = this.orders.filter(o => o.status === status).length;
        });

        // Payment status breakdown
        const paymentStatusBreakdown = {};
        this.paymentStatuses.forEach(status => {
            paymentStatusBreakdown[status] = this.orders.filter(o => o.paymentStatus === status).length;
        });

        // Top customers by spending
        const customerSpending = {};
        this.orders.forEach(order => {
            if (!customerSpending[order.customerId]) {
                customerSpending[order.customerId] = {
                    customerId: order.customerId,
                    totalSpent: 0,
                    orderCount: 0
                };
            }
            customerSpending[order.customerId].totalSpent += order.total;
            customerSpending[order.customerId].orderCount += 1;
        });

        const topCustomers = Object.values(customerSpending)
            .sort((a, b) => b.totalSpent - a.totalSpent)
            .slice(0, 10);

        return {
            totalOrders: this.orders.length,
            totalRevenue: totalRevenue,
            completedRevenue: completedRevenue,
            averageOrderValue: totalRevenue / this.orders.length,
            statusBreakdown: statusBreakdown,
            paymentStatusBreakdown: paymentStatusBreakdown,
            topCustomers: topCustomers,
            uniqueCustomers: this.customers.size
        };
    }

    /**
     * Search orders
     */
    searchOrders(searchTerm) {
        const term = searchTerm.toString().toLowerCase();

        return this.orders.filter(order => 
            order.orderNumber.toLowerCase().includes(term) ||
            order.customerId.toLowerCase().includes(term) ||
            order.shippingAddress.toLowerCase().includes(term)
        );
    }

    /**
     * Validate complete order
     */
    validateOrder(orderData) {
        const errors = [];

        // Required fields validation
        const fieldsToValidate = [
            { field: 'orderNumber', validator: this.validators.orderNumber, value: orderData.orderNumber },
            { field: 'customerId', validator: this.validators.customerId, value: orderData.customerId },
            { field: 'orderDate', validator: this.validators.orderDate, value: orderData.orderDate },
            { field: 'status', validator: this.validators.orderStatus, value: orderData.status },
            { field: 'paymentStatus', validator: this.validators.paymentStatus, value: orderData.paymentStatus },
            { field: 'total', validator: this.validators.orderTotal, value: orderData.total },
            { field: 'itemCount', validator: this.validators.itemCount, value: orderData.itemCount },
            { field: 'shippingAddress', validator: this.validators.shippingAddress, value: orderData.shippingAddress }
        ];

        fieldsToValidate.forEach(({ field, validator, value }) => {
            if (!validator.validate(value)) {
                errors.push({
                    field: field,
                    message: validator.getErrorMessage()
                });
            }
        });

        // Optional tracking number validation
        if (orderData.trackingNumber && !this.validators.trackingNumber.validate(orderData.trackingNumber)) {
            errors.push({
                field: 'trackingNumber',
                message: this.validators.trackingNumber.getErrorMessage()
            });
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    }

    /**
     * Export orders as JSON
     */
    exportAsJSON() {
        return JSON.stringify({
            exportDate: new Date(),
            totalOrders: this.orders.length,
            orders: this.orders,
            statistics: this.getOrderStatistics()
        }, null, 2);
    }

    /**
     * Export orders as CSV
     */
    exportAsCSV() {
        if (this.orders.length === 0) {
            return 'Order Number,Customer ID,Order Date,Status,Payment Status,Total,Item Count\n';
        }

        const headers = ['Order Number', 'Customer ID', 'Order Date', 'Status', 'Payment Status', 'Total', 'Item Count', 'Shipping Address'];
        const rows = this.orders.map(order => [
            order.orderNumber,
            order.customerId,
            order.orderDate.toISOString(),
            order.status,
            order.paymentStatus,
            order.total,
            order.itemCount,
            `"${order.shippingAddress}"`
        ]);

        return [
            headers.join(','),
            ...rows.map(row => row.join(','))
        ].join('\n');
    }

    /**
     * Clear all orders (for testing/demo)
     */
    clearAll() {
        this.orders = [];
        this.customers.clear();
        return { success: true, message: 'All orders cleared' };
    }
}

// Export manager
if (typeof module !== 'undefined' && module.exports) {
    module.exports = OrderSummaryManager;
}
