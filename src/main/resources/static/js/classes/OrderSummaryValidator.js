/**
 * OrderSummaryValidator.js
 * ======================
 * Validators for order summary/recap functionality
 * 
 * Part of Phase 5: Order Summary/Recap Implementation
 * Validates order data, customer relations, and status filtering
 * 
 * Used in:
 * - Order listing page
 * - Order detail page
 * - Order status filter
 * - Customer order history
 */

/**
 * Base OrderValidator Class
 */
class OrderValidator {
    constructor() {
        if (this.constructor === OrderValidator) {
            throw new Error('OrderValidator is abstract and cannot be instantiated directly');
        }
    }

    validate(value) {
        throw new Error('validate() method must be implemented by subclass');
    }

    getErrorMessage() {
        throw new Error('getErrorMessage() method must be implemented by subclass');
    }

    isEmpty(value) {
        return value === null || value === undefined || value.toString().trim() === '';
    }

    isValidDate(dateString) {
        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    }
}

/**
 * OrderNumberValidator
 * Validates order number/ID
 * Rules: Required, format: ORD-XXXXXXX
 */
class OrderNumberValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Order number is required';
            return false;
        }

        const orderNum = value.toString().toUpperCase();

        // Order format: ORD-20241214-001
        if (!/^ORD-\d{8}-\d{3,}$/.test(orderNum)) {
            this.errorMessage = 'Order number must be in format: ORD-YYYYMMDD-###';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Order Number';
    }
}

/**
 * OrderStatusValidator
 * Validates order status
 * Rules: One of pending, processing, completed, cancelled, failed
 */
class OrderStatusValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.validStatuses = [
            'pending',
            'processing',
            'completed',
            'cancelled',
            'failed',
            'refunded'
        ];
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Order status is required';
            return false;
        }

        if (!this.validStatuses.includes(value.toString().toLowerCase())) {
            this.errorMessage = `Status must be one of: ${this.validStatuses.join(', ')}`;
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Order Status';
    }

    getValidStatuses() {
        return this.validStatuses;
    }

    getStatusColor(status) {
        const colors = {
            'pending': '#ffc107',
            'processing': '#17a2b8',
            'completed': '#28a745',
            'cancelled': '#6c757d',
            'failed': '#dc3545',
            'refunded': '#e83e8c'
        };
        return colors[status] || '#6c757d';
    }

    getStatusIcon(status) {
        const icons = {
            'pending': '⏳',
            'processing': '⚙️',
            'completed': '✅',
            'cancelled': '❌',
            'failed': '⚠️',
            'refunded': '💰'
        };
        return icons[status] || '❓';
    }
}

/**
 * PaymentStatusValidator
 * Validates payment status
 * Rules: pending, paid, failed, refunded
 */
class PaymentStatusValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.validStatuses = ['pending', 'paid', 'failed', 'refunded', 'partial'];
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Payment status is required';
            return false;
        }

        if (!this.validStatuses.includes(value.toString().toLowerCase())) {
            this.errorMessage = `Payment status must be one of: ${this.validStatuses.join(', ')}`;
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Payment Status';
    }
}

/**
 * OrderTotalValidator
 * Validates order total amount
 * Rules: > 0, max 999,999,999
 */
class OrderTotalValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Order total is required';
            return false;
        }

        const total = parseFloat(value);

        if (isNaN(total)) {
            this.errorMessage = 'Order total must be a number';
            return false;
        }

        if (total <= 0) {
            this.errorMessage = 'Order total must be greater than 0';
            return false;
        }

        if (total > 999999999) {
            this.errorMessage = 'Order total cannot exceed 999,999,999';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Order Total';
    }
}

/**
 * OrderDateValidator
 * Validates order date
 * Rules: Valid date, not in future
 */
class OrderDateValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Order date is required';
            return false;
        }

        if (!this.isValidDate(value)) {
            this.errorMessage = 'Order date must be a valid date';
            return false;
        }

        const orderDate = new Date(value);
        const today = new Date();
        today.setHours(23, 59, 59, 999);

        if (orderDate > today) {
            this.errorMessage = 'Order date cannot be in the future';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Order Date';
    }
}

/**
 * CustomerIdValidator
 * Validates customer relationship
 * Rules: Required, valid customer ID
 */
class CustomerIdValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Customer ID is required';
            return false;
        }

        // Customer ID format: CUST-XXXXXXX
        if (!/^CUST-\d+$/.test(value.toString().toUpperCase())) {
            this.errorMessage = 'Invalid customer ID format';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Customer ID';
    }
}

/**
 * OrderItemCountValidator
 * Validates number of items in order
 * Rules: >= 1, max 999
 */
class OrderItemCountValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Item count is required';
            return false;
        }

        const count = parseInt(value, 10);

        if (isNaN(count)) {
            this.errorMessage = 'Item count must be a number';
            return false;
        }

        if (count < 1) {
            this.errorMessage = 'Order must have at least 1 item';
            return false;
        }

        if (count > 999) {
            this.errorMessage = 'Order cannot have more than 999 items';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Item Count';
    }
}

/**
 * ShippingAddressValidator
 * Validates shipping address
 * Rules: Required, min 10 chars
 */
class ShippingAddressValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Shipping address is required';
            return false;
        }

        const address = value.toString().trim();

        if (address.length < 10) {
            this.errorMessage = 'Shipping address must be at least 10 characters';
            return false;
        }

        if (address.length > 500) {
            this.errorMessage = 'Shipping address cannot exceed 500 characters';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Shipping Address';
    }
}

/**
 * TrackingNumberValidator
 * Validates shipment tracking number
 * Rules: Optional, format if provided
 */
class TrackingNumberValidator extends OrderValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
        }

        const tracking = value.toString().trim();

        // Allow alphanumeric tracking numbers, 5-30 chars
        if (!/^[A-Z0-9]{5,30}$/i.test(tracking)) {
            this.errorMessage = 'Tracking number must be 5-30 alphanumeric characters';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Tracking Number';
    }
}

// Export validators
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        OrderValidator,
        OrderNumberValidator,
        OrderStatusValidator,
        PaymentStatusValidator,
        OrderTotalValidator,
        OrderDateValidator,
        CustomerIdValidator,
        OrderItemCountValidator,
        ShippingAddressValidator,
        TrackingNumberValidator
    };
}
