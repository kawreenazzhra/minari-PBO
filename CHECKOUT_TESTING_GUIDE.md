# MINARI Checkout Testing Guide

## Application Status ✅

- **Status:** Running on http://localhost:8080
- **Build:** SUCCESS (26.107s)
- **Database:** H2 in-memory (./data/minaridb)
- **Java:** 21 LTS with Spring Boot 3.3.5

---

## Quick Start Steps

### Step 1: Access the Application
```
URL: http://localhost:8080
```

### Step 2: Login as Customer
```
Email: customer@minari.com
Password: customer123
```

### Step 3: Create Products (Admin Only)
**Note:** Products must exist before checkout testing
```
Admin URL: http://localhost:8080/admin
Admin Email: admin@minari.com
Admin Password: admin123
Steps:
1. Go to /admin/categories
2. Create categories (e.g., Shirts, Sweaters, Jeans)
3. Go to /admin/products
4. Create products with:
   - Name
   - SKU
   - Price
   - Image URL
   - Category
   - Stock quantity
```

### Step 4: Add Products to Cart (as Customer)
```
1. Browse landing page
2. Click "Add to Cart" on products
3. Cart count should increase
```

### Step 5: Proceed to Checkout
```
1. Click "Checkout" button
2. You should see:
   - Order Summary
   - Cart items with images
   - Total amount
3. If empty cart → should show error message
```

### Step 6: Select Shipping Address
```
1. Choose existing address OR
2. Add new address with:
   - Recipient Name
   - Street Address
   - Apartment/Suite
   - City
   - Province
   - Zipcode
   - Country
   - Phone Number
3. Click "Use This Address"
```

### Step 7: Select Payment Method
```
Available options:
- Cash on Delivery (COD)
- Bank Transfer
- E-Wallet (GCash)

Choose one and click "Continue"
```

### Step 8: Review & Place Order
```
1. Verify order details
2. Click "Place Order"
3. Expected behavior:
   - Order created in database
   - Cart cleared
   - Redirect to order confirmation
   - Success message displayed
```

---

## Database Verification

### Check Orders Table
```sql
SELECT * FROM orders ORDER BY created_at DESC LIMIT 5;
```

**Expected Fields (All Required):**
- order_number (e.g., "ORD-2025-12-21-001")
- total_amount (e.g., 150000.00)
- subtotal_amount (e.g., 150000.00)
- tax_amount (e.g., 0.00)
- shipping_cost (e.g., 0.00)
- discount_amount (e.g., 0.00)
- customer_id (NOT NULL ✅)
- created_at (timestamp)

### Check Order Items Table
```sql
SELECT * FROM order_items 
WHERE order_id = (SELECT MAX(id) FROM orders);
```

**Expected Fields (All Required):**
- product_name (e.g., "Premium T-Shirt") ✅ NO NULL
- product_sku (e.g., "TSH-001") ✅ NO NULL
- image_url (e.g., "http://...") ✅ NO NULL
- quantity (e.g., 2)
- unit_price (e.g., 75000.00)
- total_price (e.g., 150000.00)
- order_id (Foreign key)

---

## Error Scenarios to Test

### Scenario 1: Empty Cart Checkout
```
Expected Behavior:
- Error message: "Cart is empty"
- Button disabled or redirected
- Cannot proceed
```

### Scenario 2: Missing Shipping Address
```
Expected Behavior:
- "Please select shipping address" error
- Form highlighted with error
- Cannot place order
```

### Scenario 3: Invalid Payment Method
```
Expected Behavior:
- "Invalid payment method" error
- Cannot proceed
- Form validation message shown
```

### Scenario 4: Multiple Checkouts
```
Expected Behavior:
- Each order gets unique order_number
- All orders appear in database
- Each order has complete item details
- No NULL values in required fields
```

---

## Browser Console Checks

Open Developer Tools (F12) and check:

### Network Tab
- POST /checkout/place should return 200 OK
- Response should contain order confirmation

### Console Tab
- No JavaScript errors
- No Thymeleaf template errors
- Success messages should display

---

## Common Issues & Fixes

| Issue | Cause | Fix |
|-------|-------|-----|
| "Cart is empty" on checkout | No products added | Go back, add items, retry |
| "NULL not allowed for PRODUCT_NAME" | ProductName not set | ✅ Fixed in OrderService |
| "NULL not allowed for SUBTOTAL_AMOUNT" | SubtotalAmount not set | ✅ Fixed in OrderService |
| "No addresses found" | Customer has no address | Create new address first |
| "Order not created" | Transaction rollback | Check server logs for details |
| Products not appearing | Categories not created | Create categories in admin panel |

---

## Performance Monitoring

### Server Logs
```bash
# Last 50 lines of server output
tail -50 /path/to/application.log

# Search for errors
grep -i "error\|exception" application.log
```

### Database Connection
- HikariCP pool size: Default (10-20 connections)
- Connection time: < 100ms typical
- Query time: < 50ms typical for select operations

### Memory Usage
```bash
# Check Java process
jps -l | grep MinariApplication

# Monitor with jconsole or JProfiler
jconsole
```

---

## Test Data Summary

### Admin Account
- Email: admin@minari.com
- Password: admin123
- Role: Administrator
- Permissions: Full access to admin panel

### Customer Account
- Email: customer@minari.com
- Password: customer123
- Role: Customer
- Cart: Initially empty (ready for testing)

### Sample Products (To Be Created)
| Name | SKU | Price | Image | Stock |
|------|-----|-------|-------|-------|
| Premium T-Shirt | TSH-001 | 75,000 | /images/shirt.jpg | 50 |
| Classic Jeans | JEAN-001 | 150,000 | /images/jeans.jpg | 30 |
| Summer Sweater | SWT-001 | 120,000 | /images/sweater.jpg | 25 |

---

## Success Criteria

✅ **Checkout Flow Success When:**
1. Order created without NULL constraint errors
2. All order fields populated (totalAmount, subtotalAmount, etc.)
3. All order_item fields populated (productName, productSku, imageUrl, etc.)
4. Cart cleared after successful order
5. Confirmation page displays order details
6. Order visible in orders table
7. Order items visible in order_items table
8. No errors in server logs

---

## Debugging Tips

### Enable Debug Logging
```yaml
# application.properties
logging.level.com.minari.ecommerce=DEBUG
logging.level.org.hibernate=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Check Hibernate SQL
```yaml
# application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
```

### View Database in Browser
```
URL: http://localhost:8080/h2-console
Driver: org.h2.Driver
URL: jdbc:h2:file:./data/minaridb
User: sa
Password: (leave blank)
```

---

## Rollback Procedure (If Needed)

### 1. Stop Application
```bash
# Press Ctrl+C in terminal running mvn spring-boot:run
```

### 2. Clear Database
```bash
# Delete database file
rm -rf ./data/minaridb*
```

### 3. Restart Application
```bash
cd C:\minaripbo\minari-PBO
mvn spring-boot:run -DskipTests
```

---

## Support & References

### Documentation Files
- `CHECKOUT_FIX_SUMMARY.md` - Complete fix documentation
- `PHASE_5_BACKEND_API_GUIDE.md` - API endpoints
- `PHASE_6_CUSTOMER_MANAGEMENT_GUIDE.md` - Customer management
- `OOP_IMPLEMENTATION_GUIDE.md` - Design patterns used

### Key Classes
- `OrderService.java` - Order creation logic
- `WebOrderController.java` - Checkout endpoints
- `Order.java` - Order entity
- `OrderItem.java` - Order item entity
- `ShoppingCart.java` - Cart management

### Related Endpoints
- POST /checkout/place - Create order
- GET /checkout - Checkout page
- GET /checkout/payment - Payment method selection
- POST /checkout/payment/method - Set payment method
- GET /cart - View cart

---

**Status:** Ready for Testing  
**Last Updated:** December 21, 2025  
**Application URL:** http://localhost:8080  
**Maintained By:** Development Team
