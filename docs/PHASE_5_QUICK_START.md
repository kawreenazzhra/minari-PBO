# Phase 5: Order Summary - Quick Start Guide

## ⚡ Quick Start (5 Minutes)

### Step 1: Load the Scripts

```html
<!-- In your HTML -->
<script src="/js/classes/OrderSummaryValidator.js"></script>
<script src="/js/classes/OrderSummaryManager.js"></script>
```

### Step 2: Initialize Manager

```javascript
const orderManager = new OrderSummaryManager();
```

### Step 3: Add Orders

```javascript
// Add a single order
const result = orderManager.addOrder({
    orderNumber: 'ORD-20241220-001',
    customerId: 'CUST-10001',
    orderDate: '2024-12-20',
    status: 'completed',
    paymentStatus: 'paid',
    total: 125.50,
    itemCount: 3,
    shippingAddress: '123 Main St, New York, NY 10001',
    trackingNumber: 'TRACK123456789'
});

if (result.success) {
    console.log('✅ Order added:', result.order);
} else {
    console.log('❌ Errors:', result.errors);
}
```

### Step 4: Get Order Data

```javascript
// Get specific order
const order = orderManager.getOrderByNumber('ORD-20241220-001');

// Get customer orders
const { orders, customer } = orderManager.getCustomerOrders('CUST-10001');

// Get statistics
const stats = orderManager.getOrderStatistics();
console.log(`Total Revenue: $${stats.totalRevenue}`);
```

### Step 5: Update Order Status

```javascript
// Update order status
const update = orderManager.updateOrderStatus('ORD-20241220-001', 'processing');

// Update payment status
const paymentUpdate = orderManager.updatePaymentStatus('ORD-20241220-001', 'paid');
```

## 📦 Common Tasks

### Task 1: Display All Orders for a Customer

```javascript
function displayCustomerOrders(customerId) {
    const result = orderManager.getCustomerOrders(customerId);
    
    if (result.success) {
        result.orders.forEach(order => {
            console.log(`Order: ${order.orderNumber} - $${order.total}`);
        });
    }
}

displayCustomerOrders('CUST-10001');
```

### Task 2: Get Orders by Status

```javascript
function getCompletedOrders() {
    const result = orderManager.getOrdersByStatus('completed');
    return result.orders;
}

const completed = getCompletedOrders();
```

### Task 3: Filter Orders by Date

```javascript
function getOrdersThisMonth() {
    const now = new Date();
    const start = new Date(now.getFullYear(), now.getMonth(), 1);
    const end = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    
    return orderManager.getOrdersByDateRange(start, end);
}
```

### Task 4: Search Orders

```javascript
function searchOrder(term) {
    const results = orderManager.searchOrders(term);
    return results;
}

// Search by order number, customer ID, or address
searchOrder('ORD-20241220');
searchOrder('CUST-10001');
searchOrder('New York');
```

### Task 5: Export Data

```javascript
// Export as JSON
function downloadJSON() {
    const json = orderManager.exportAsJSON();
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'orders.json';
    a.click();
}

// Export as CSV
function downloadCSV() {
    const csv = orderManager.exportAsCSV();
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'orders.csv';
    a.click();
}
```

## 🎯 Validator Quick Reference

### Validate Order Number

```javascript
const validator = new OrderNumberValidator();
if (validator.validate('ORD-20241220-001')) {
    console.log('✅ Valid order number');
}
```

Valid formats: `ORD-YYYYMMDD-###`

### Validate Order Status

```javascript
const statusValidator = new OrderStatusValidator();
const validStatuses = statusValidator.getValidStatuses();
// Returns: ['pending', 'processing', 'completed', 'cancelled', 'failed', 'refunded']

const color = statusValidator.getStatusColor('completed'); // Green
const icon = statusValidator.getStatusIcon('completed');   // ✅
```

### Validate Payment Status

```javascript
const paymentValidator = new PaymentStatusValidator();
if (paymentValidator.validate('paid')) {
    console.log('✅ Payment status valid');
}
```

Valid: `pending`, `paid`, `failed`, `refunded`, `partial`

### Validate Amount

```javascript
const amountValidator = new OrderTotalValidator();
if (amountValidator.validate(125.50)) {
    console.log('✅ Valid amount');
}
```

Rules: Must be > 0 and < 999,999,999

### Validate Date

```javascript
const dateValidator = new OrderDateValidator();
if (dateValidator.validate('2024-12-20')) {
    console.log('✅ Valid date');
}
```

Rules: Must be valid date and not in future

### Validate Customer

```javascript
const customerValidator = new CustomerIdValidator();
if (customerValidator.validate('CUST-10001')) {
    console.log('✅ Valid customer ID');
}
```

Valid format: `CUST-XXXXX` (where X = digits)

### Validate Item Count

```javascript
const itemValidator = new OrderItemCountValidator();
if (itemValidator.validate(3)) {
    console.log('✅ Valid item count');
}
```

Rules: 1-999 items

### Validate Address

```javascript
const addressValidator = new ShippingAddressValidator();
if (addressValidator.validate('123 Main St, New York, NY 10001')) {
    console.log('✅ Valid address');
}
```

Rules: 10-500 characters

### Validate Tracking Number

```javascript
const trackingValidator = new TrackingNumberValidator();
if (trackingValidator.validate('TRACK123456789')) {
    console.log('✅ Valid tracking number');
}
```

Rules: Optional, 5-30 alphanumeric characters if provided

## 🔍 Error Handling

### Check Validation Errors

```javascript
function addOrderWithErrorHandling(orderData) {
    const result = orderManager.addOrder(orderData);
    
    if (!result.success) {
        result.errors.forEach(error => {
            console.error(`${error.field}: ${error.message}`);
        });
    } else {
        console.log('✅ Order added successfully');
    }
}
```

### Validate Individual Fields

```javascript
function validateField(fieldName, value) {
    const validator = orderManager.validators[fieldName];
    
    if (!validator) {
        console.error(`No validator for field: ${fieldName}`);
        return false;
    }
    
    if (validator.validate(value)) {
        console.log(`✅ ${fieldName} is valid`);
        return true;
    } else {
        console.error(`❌ ${fieldName}: ${validator.getErrorMessage()}`);
        return false;
    }
}

validateField('orderNumber', 'ORD-20241220-001');
```

## 📊 Sample Data

```javascript
// Add sample orders
const sampleOrders = [
    {
        orderNumber: 'ORD-20241220-001',
        customerId: 'CUST-10001',
        orderDate: '2024-12-20',
        status: 'completed',
        paymentStatus: 'paid',
        total: 125.50,
        itemCount: 3,
        shippingAddress: '123 Main St, New York, NY 10001',
        trackingNumber: 'TRACK123456789'
    },
    {
        orderNumber: 'ORD-20241219-002',
        customerId: 'CUST-10002',
        orderDate: '2024-12-19',
        status: 'processing',
        paymentStatus: 'paid',
        total: 89.99,
        itemCount: 2,
        shippingAddress: '456 Oak Ave, Los Angeles, CA 90001',
        trackingNumber: null
    }
];

sampleOrders.forEach(order => {
    orderManager.addOrder(order);
});

const stats = orderManager.getOrderStatistics();
console.log(`Added ${stats.totalOrders} orders, Revenue: $${stats.totalRevenue}`);
```

## 🎨 HTML Template Integration

The `order-summary-oop.html` provides a complete dashboard:

```html
<!-- Link to order summary page -->
<a href="/orders/order-summary-oop.html">View Orders</a>
```

Features included:
- ✅ Real-time statistics dashboard
- ✅ Advanced filtering
- ✅ Order cards with actions
- ✅ Pagination
- ✅ Responsive design
- ✅ Status badges
- ✅ Mobile optimized

## 🔗 Connecting to Backend

```javascript
// Load orders from API
async function loadOrdersFromAPI() {
    try {
        const response = await fetch('/api/orders');
        const orders = await response.json();
        
        orders.forEach(order => {
            orderManager.addOrder(order);
        });
        
        console.log(`Loaded ${orders.length} orders`);
    } catch (error) {
        console.error('Failed to load orders:', error);
    }
}

// Save order to API
async function saveOrder(orderData) {
    try {
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderData)
        });
        
        const result = await response.json();
        return result;
    } catch (error) {
        console.error('Failed to save order:', error);
    }
}
```

## 💡 Pro Tips

1. **Always Validate** - Use validators before adding orders
2. **Use Error Handling** - Check result.success before proceeding
3. **Cache Managers** - Keep OrderSummaryManager in global scope
4. **Customer Relations** - Use getCustomerOrders for linked data
5. **Export Data** - Use JSON/CSV for backups and reporting
6. **Search First** - Use searchOrders() before manual filtering
7. **Update Status** - Use dedicated update methods for state changes
8. **Display Stats** - Show getOrderStatistics() in dashboards

## ❓ FAQ

**Q: How do I validate an order before adding?**
A: Use `validateOrder()` method to check all fields.

**Q: Can I update just the status?**
A: Yes, use `updateOrderStatus()` or `updatePaymentStatus()`.

**Q: How do I get total spent by a customer?**
A: Use `getCustomerSummary()` method.

**Q: What's the maximum order total?**
A: $999,999,999 (see OrderTotalValidator).

**Q: How do I export for reporting?**
A: Use `exportAsJSON()` or `exportAsCSV()` methods.

**Q: Is tracking number required?**
A: No, it's optional. Leave it null/empty if not available.

---

**Need More Help?** See `PHASE_5_ORDER_SUMMARY_GUIDE.md` for detailed documentation.
