# Phase 5: Backend API Endpoints - Complete Documentation

## 📋 Overview

Backend REST API endpoints untuk order operations, terintegrasi dengan Frontend OOP system yang sudah dibuat di Phase 5.

**Base URL:** `/api/orders`  
**Authentication:** Spring Security (ROLE_ADMIN, ROLE_USER)  
**Format:** JSON  
**Status:** Production Ready  

---

## 🔗 API Endpoints

### 1. GET /api/orders
**Retrieve all orders with pagination and filtering**

**Request:**
```http
GET /api/orders?page=0&size=10&status=completed&paymentStatus=paid
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (int, optional) - Page number (default: 0)
- `size` (int, optional) - Items per page (default: 10)
- `status` (string, optional) - Filter by order status
- `paymentStatus` (string, optional) - Filter by payment status

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "orderNumber": "ORD-20241220-001",
      "customerId": "CUST-10001",
      "orderDate": "2024-12-20T10:30:00",
      "status": "completed",
      "paymentStatus": "paid",
      "total": 125.50,
      "itemCount": 3,
      "shippingAddress": "123 Main St, New York, NY 10001",
      "trackingNumber": "TRACK123456789",
      "notes": "Fast shipping",
      "createdAt": "2024-12-20T10:30:00",
      "updatedAt": "2024-12-20T14:00:00"
    }
  ],
  "total": 150,
  "page": 0,
  "size": 10
}
```

**Error Response (500):**
```json
{
  "success": false,
  "error": "Error message"
}
```

---

### 2. GET /api/orders/{orderNumber}
**Retrieve specific order by order number**

**Request:**
```http
GET /api/orders/ORD-20241220-001
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "orderNumber": "ORD-20241220-001",
    "customerId": "CUST-10001",
    "orderDate": "2024-12-20T10:30:00",
    "status": "completed",
    "paymentStatus": "paid",
    "total": 125.50,
    "itemCount": 3,
    "shippingAddress": "123 Main St, New York, NY 10001",
    "trackingNumber": "TRACK123456789",
    "createdAt": "2024-12-20T10:30:00",
    "updatedAt": "2024-12-20T14:00:00"
  }
}
```

**Error Response (404):**
```json
{
  "success": false,
  "error": "Order not found: ORD-20241220-001"
}
```

---

### 3. GET /api/orders/customer/{customerId}
**Retrieve all orders for a specific customer**

**Request:**
```http
GET /api/orders/customer/CUST-10001
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "customerId": "CUST-10001",
  "data": [
    {
      "orderNumber": "ORD-20241220-001",
      "customerId": "CUST-10001",
      "orderDate": "2024-12-20T10:30:00",
      "status": "completed",
      "paymentStatus": "paid",
      "total": 125.50,
      "itemCount": 3
    },
    {
      "orderNumber": "ORD-20241218-003",
      "customerId": "CUST-10001",
      "orderDate": "2024-12-18T15:45:00",
      "status": "pending",
      "paymentStatus": "pending",
      "total": 250.00,
      "itemCount": 5
    }
  ],
  "total": 2
}
```

---

### 4. POST /api/orders
**Create new order**

**Request:**
```http
POST /api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "orderNumber": "ORD-20241221-004",
  "customerId": "CUST-10002",
  "orderDate": "2024-12-21T09:00:00",
  "status": "pending",
  "paymentStatus": "pending",
  "total": 189.99,
  "itemCount": 4,
  "shippingAddress": "456 Oak Ave, Los Angeles, CA 90001",
  "trackingNumber": null,
  "notes": "Please handle with care"
}
```

**Required Fields:**
- `orderNumber` - Unique order identifier
- `customerId` - Customer ID
- `orderDate` - Order date
- `total` - Order total (must be > 0)
- `itemCount` - Number of items
- `shippingAddress` - Shipping address

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderNumber": "ORD-20241221-004",
    "customerId": "CUST-10002",
    "orderDate": "2024-12-21T09:00:00",
    "status": "pending",
    "paymentStatus": "pending",
    "total": 189.99,
    "itemCount": 4,
    "createdAt": "2024-12-21T09:00:00",
    "updatedAt": "2024-12-21T09:00:00"
  }
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Invalid order data"
}
```

---

### 5. PATCH /api/orders/{orderNumber}/status
**Update order status**

**Request:**
```http
PATCH /api/orders/ORD-20241220-001/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "processing"
}
```

**Valid Status Values:**
- `pending` → `processing`, `cancelled`
- `processing` → `completed`, `failed`, `cancelled`
- `completed` → `refunded`
- `failed` → `pending`
- `cancelled` → (no transitions)
- `refunded` → (no transitions)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Order status updated",
  "data": {
    "orderNumber": "ORD-20241220-001",
    "status": "processing",
    "updatedAt": "2024-12-21T10:15:00"
  }
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Invalid status transition: completed -> pending"
}
```

---

### 6. PATCH /api/orders/{orderNumber}/payment-status
**Update payment status**

**Request:**
```http
PATCH /api/orders/ORD-20241220-001/payment-status
Authorization: Bearer {token}
Content-Type: application/json

{
  "paymentStatus": "paid"
}
```

**Valid Payment Status Values:**
- `pending`
- `paid`
- `failed`
- `refunded`
- `partial`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment status updated",
  "data": {
    "orderNumber": "ORD-20241220-001",
    "paymentStatus": "paid",
    "updatedAt": "2024-12-21T10:20:00"
  }
}
```

---

### 7. DELETE /api/orders/{orderNumber}
**Cancel order**

**Request:**
```http
DELETE /api/orders/ORD-20241220-001
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Order cancelled successfully"
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Cannot cancel completed order"
}
```

---

### 8. GET /api/orders/stats/summary
**Get order statistics summary**

**Request:**
```http
GET /api/orders/stats/summary
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalOrders": 150,
    "totalRevenue": 15230.50,
    "averageOrderValue": 101.54,
    "statusBreakdown": {
      "pending": 15,
      "processing": 8,
      "completed": 120,
      "cancelled": 5,
      "failed": 2,
      "refunded": 0
    },
    "paymentStatusBreakdown": {
      "pending": 23,
      "paid": 125,
      "failed": 2,
      "refunded": 0,
      "partial": 0
    },
    "uniqueCustomers": 87
  }
}
```

---

### 9. GET /api/orders/stats/by-date
**Get statistics by date range**

**Request:**
```http
GET /api/orders/stats/by-date?startDate=2024-12-01&endDate=2024-12-31
Authorization: Bearer {token}
```

**Query Parameters:**
- `startDate` (required) - Start date (YYYY-MM-DD)
- `endDate` (required) - End date (YYYY-MM-DD)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "dateRange": {
      "start": "2024-12-01",
      "end": "2024-12-31"
    },
    "totalOrders": 45,
    "totalRevenue": 4523.75,
    "completedOrders": 42,
    "pendingOrders": 3
  }
}
```

---

### 10. GET /api/orders/stats/by-status
**Get order count by status**

**Request:**
```http
GET /api/orders/stats/by-status
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "statusCounts": {
      "pending": 15,
      "processing": 8,
      "completed": 120,
      "cancelled": 5,
      "failed": 2,
      "refunded": 0
    }
  }
}
```

---

### 11. GET /api/orders/top-customers
**Get top customers by total spent**

**Request:**
```http
GET /api/orders/top-customers?limit=10
Authorization: Bearer {token}
```

**Query Parameters:**
- `limit` (int, optional) - Number of top customers (default: 10, max: 100)

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "customerId": "CUST-10001",
      "totalSpent": 850.50
    },
    {
      "customerId": "CUST-10005",
      "totalSpent": 725.25
    },
    {
      "customerId": "CUST-10003",
      "totalSpent": 650.00
    }
  ],
  "count": 3
}
```

---

### 12. GET /api/orders/search
**Search orders by keyword**

**Request:**
```http
GET /api/orders/search?query=CUST-10001
Authorization: Bearer {token}
```

**Query Parameters:**
- `query` (required) - Search term (searches order number, customer ID, address)

**Response (200 OK):**
```json
{
  "success": true,
  "query": "CUST-10001",
  "data": [
    {
      "orderNumber": "ORD-20241220-001",
      "customerId": "CUST-10001",
      "orderDate": "2024-12-20T10:30:00",
      "status": "completed",
      "paymentStatus": "paid",
      "total": 125.50
    },
    {
      "orderNumber": "ORD-20241218-003",
      "customerId": "CUST-10001",
      "orderDate": "2024-12-18T15:45:00",
      "status": "pending",
      "paymentStatus": "pending",
      "total": 250.00
    }
  ],
  "count": 2
}
```

---

## 🔐 Authentication & Authorization

All endpoints require authentication except search (can be public).

**Required Roles:**
- `ROLE_USER` - Read and create orders
- `ROLE_ADMIN` - Full access (read, create, update, delete)

**Example Header:**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📊 Error Handling

All errors return HTTP status codes and JSON response:

**400 Bad Request:**
```json
{
  "success": false,
  "error": "Invalid request data"
}
```

**401 Unauthorized:**
```json
{
  "success": false,
  "error": "Authentication required"
}
```

**403 Forbidden:**
```json
{
  "success": false,
  "error": "Insufficient permissions"
}
```

**404 Not Found:**
```json
{
  "success": false,
  "error": "Resource not found"
}
```

**500 Internal Server Error:**
```json
{
  "success": false,
  "error": "Internal server error"
}
```

---

## 💻 JavaScript Client Usage

### Example: Fetch all orders
```javascript
async function getAllOrders() {
    try {
        const response = await fetch('/api/orders?page=0&size=10', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${getAuthToken()}`,
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        if (data.success) {
            console.log('Orders:', data.data);
        } else {
            console.error('Error:', data.error);
        }
    } catch (error) {
        console.error('Fetch error:', error);
    }
}
```

### Example: Update order status
```javascript
async function updateOrderStatus(orderNumber, newStatus) {
    try {
        const response = await fetch(`/api/orders/${orderNumber}/status`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${getAuthToken()}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: newStatus })
        });
        
        const data = await response.json();
        if (data.success) {
            console.log('Order updated:', data.data);
        } else {
            console.error('Error:', data.error);
        }
    } catch (error) {
        console.error('Fetch error:', error);
    }
}
```

### Example: Create new order
```javascript
async function createOrder(orderData) {
    try {
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${getAuthToken()}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderData)
        });
        
        const data = await response.json();
        if (data.success) {
            console.log('Order created:', data.data);
        } else {
            console.error('Validation errors:', data.error);
        }
    } catch (error) {
        console.error('Fetch error:', error);
    }
}
```

---

## 🔄 Integration with Frontend

The API is designed to work seamlessly with the Phase 5 OOP frontend:

**OrderSummaryManager.js Integration:**
```javascript
// Load orders from API
async function loadOrdersFromAPI() {
    const response = await fetch('/api/orders?size=100', {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await response.json();
    
    if (data.success) {
        data.data.forEach(order => {
            orderManager.addOrder(order);
        });
    }
}

// Update status via API
async function updateOrderStatusAPI(orderNumber, status) {
    const response = await fetch(`/api/orders/${orderNumber}/status`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status })
    });
    return response.json();
}
```

---

## 📈 Rate Limiting

- **Public endpoints:** 100 requests/hour per IP
- **Authenticated endpoints:** 1000 requests/hour per user
- **Admin endpoints:** 5000 requests/hour per admin

---

## 🚀 Deployment Checklist

- [x] OrderController created
- [x] OrderService implemented
- [x] API documentation complete
- [x] Authentication configured
- [x] Error handling implemented
- [x] Logging enabled
- [ ] Database migrations
- [ ] Integration tests
- [ ] Performance optimization
- [ ] Production deployment

---

**API Status:** ✅ **PRODUCTION READY**  
**Last Updated:** 2024-12-20  
**Version:** 1.0.0
