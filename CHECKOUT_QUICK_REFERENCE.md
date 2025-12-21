# MINARI Checkout - Quick Reference Guide

## 🚀 Quick Start

### For Developers
```bash
# Build the project
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run

# Access checkout at
http://localhost:8080/checkout
```

### For QA/Testing
```
Checkout Flow:
1. Login → /login
2. Add items → /products
3. View cart → /cart
4. Checkout → /checkout
5. Select address → /checkout/address
6. Select payment → /checkout/payment
7. Place order → POST /checkout/place
8. Confirmation → /checkout/success
```

---

## 📝 Key Files Modified

| File | Changes | Impact |
|------|---------|--------|
| WebOrderController.java | 3 endpoints enhanced | Validation & error handling |
| checkout/summary.html | 5 improvements | Better UX & null safety |
| checkout/payment_selection.html | Error alert added | Consistent error display |

---

## ✅ Validation Rules

### Address Validation
```
Required: YES
Must be > 0: YES
Must exist in DB: YES
Must belong to user: YES (implicit)
```

### Payment Method Validation
```
Required: YES
Allowed values: 'cod', 'bank_transfer', 'e_wallet'
Default: 'cod'
Case-insensitive: YES
```

### Cart Validation
```
Must exist: YES
Must not be empty: YES
Items must have products: YES
Stock must be sufficient: YES
```

---

## 🔍 Endpoint Reference

### GET /checkout
```
Parameters:
  - addressId (optional): Long
  - paymentMethod (optional): String
  - error (optional): String

Returns: checkout/summary.html
Requires: Authentication
Actions:
  1. Gets shopping cart
  2. Fetches user addresses
  3. Sets selected address
  4. Sets payment method
  5. Validates cart not empty
```

### GET /checkout/address
```
Parameters: None
Returns: checkout/address_selection.html
Requires: Authentication
Actions:
  1. Gets all user addresses
  2. Shows address list
  3. Shows "Add new address" button
```

### POST /checkout/address/add
```
Parameters:
  - recipientName: String (required)
  - phone: String (required)
  - streetAddress: String (required)
  - city: String (required)
  - zipcode: String (required)
  - country: String (required)

Redirects: /checkout?addressId={id}
Requires: Authentication
Actions:
  1. Creates new Address
  2. Saves to database
  3. Redirects with new addressId
```

### GET /checkout/payment
```
Parameters:
  - addressId (required): Long

Returns: checkout/payment_selection.html
Requires: Authentication, Valid addressId
Actions:
  1. Validates addressId provided
  2. Verifies address exists
  3. Shows payment methods
```

### POST /checkout/place
```
Parameters:
  - addressId (required): Long (form hidden)
  - payment_method (required): String (form hidden)

Redirects:
  - Success: /checkout/success?orderNumber={number}
  - Error: /checkout?addressId={id}&error={message}

Requires: Authentication
Actions:
  1. Validates all inputs
  2. Creates order
  3. Creates payment
  4. Creates shipment
  5. Clears cart
  6. Sends email
```

### GET /checkout/success
```
Parameters:
  - orderNumber: String (required)

Returns: orders/success.html
Requires: Authentication
Actions:
  1. Displays order confirmation
  2. Shows order details
  3. Shows tracking info
```

---

## 🐛 Common Issues & Solutions

### Issue: "Your cart is empty"
**Cause**: Cart has no items
**Solution**: Go back to products and add items

### Issue: "Please select a shipping address"
**Cause**: No address selected
**Solution**: Click "Shipping to" and select or add address

### Issue: "Shipping address not found"
**Cause**: Selected address doesn't exist or was deleted
**Solution**: Select another address or add new one

### Issue: "Invalid payment method"
**Cause**: Invalid payment method in URL
**Solution**: Reselect payment method

### Issue: Checkout button disabled
**Cause**: No address selected
**Solution**: Click "Shipping to" and select address

---

## 🔐 Security Checks

All endpoints check:
- [x] User authentication
- [x] Session validity
- [x] CSRF tokens (implicit)
- [x] Input validation
- [x] SQL injection prevention (parameterized queries)
- [x] XSS prevention (Thymeleaf escaping)
- [x] HTTPS (should be enforced in production)

---

## 📊 Database Tables Involved

### Core Tables:
- `orders` - Order records
- `order_items` - Items in orders
- `shopping_carts` - User carts
- `cart_items` - Items in carts
- `addresses` - Shipping addresses
- `payments` - Payment records
- `shipments` - Shipment records
- `users` - User accounts
- `customers` - Customer details
- `products` - Product catalog

### Foreign Keys:
```
orders
  ├─ user_id → users.id
  ├─ customer_id → customers.id
  ├─ shipping_address_id → addresses.id
  └─ payment_id → payments.id

order_items
  ├─ order_id → orders.id
  └─ product_id → products.id

addresses
  ├─ customer_id → customers.id
  └─ user_id → users.id (implicit)

payments
  └─ order_id → orders.id

shipments
  └─ order_id → orders.id
```

---

## 💾 Database State Changes

### After Order Placement:
```
orders: +1 new record
order_items: +N new records (where N = items in cart)
payments: +1 new record
shipments: +1 new record
shopping_carts.items: CLEARED
addresses: no change (snapshot created)
```

---

## 📧 Email Notifications

### Order Confirmation Email
- **Triggered**: After successful order creation
- **Recipient**: Order email address
- **Content**: Order number, items, total, shipping address
- **Status**: ✅ Implemented via EmailService

---

## 🧮 Calculations

### Order Total
```
Total = Sum of (product_price × quantity for each item)
+ Shipping fee (currently 0)
- Discounts (currently 0)
= Final Total
```

### Stock Validation
```
For each item in cart:
  If product.available_quantity < item.quantity
    Then: Throw exception "Not enough stock for: [product]"
  Else: Proceed to create OrderItem
```

---

## 🔄 State Transitions

```
User State Flow:
┌─────────────┐
│  No Cart    │
└──────┬──────┘
       │ Add items
┌──────▼──────┐
│  Has Cart   │
└──────┬──────┘
       │ Proceed to checkout
┌──────▼──────┐
│  Checkout   │ ─── Select Address
└──────┬──────┘     Select Payment
       │
┌──────▼──────┐
│   Review    │
└──────┬──────┘
       │ Place order
┌──────▼──────┐
│   Order     │
│  Created    │
└──────┬──────┘
       │ View confirmation
┌──────▼──────┐
│ Confirmed   │
└─────────────┘

Error States:
Any step → Can return to Checkout with error message
         → User can fix issue and retry
```

---

## 🧪 Manual Test Steps

### Test 1: Complete Checkout
```
1. Login
2. Add product to cart
3. Go to checkout
4. Select address (or add new)
5. Select payment method
6. Click "Check out"
7. Verify order creation
8. Check order confirmation page
Expected: ✅ Success
```

### Test 2: Missing Address
```
1. Go to /checkout directly
2. Click "Check out" without selecting address
Expected: ✅ Error message shown, button disabled
```

### Test 3: Multiple Items
```
1. Add 3 different products
2. Complete checkout
3. Verify all 3 items in order
Expected: ✅ All items in database
```

### Test 4: Error Recovery
```
1. Start checkout with valid data
2. Clear cart manually
3. Try to submit checkout form
Expected: ✅ Error message, can fix and retry
```

---

## 📱 API Response Format

### Success Response
```json
{
  "success": true,
  "data": {
    "orderNumber": "ORD-20251221-001",
    "totalAmount": 250000,
    "status": "PENDING",
    "items": [...]
  }
}
```

### Error Response
```json
{
  "success": false,
  "error": "Please select a shipping address"
}
```

---

## 🔗 Related Endpoints

### Cart Management
- `GET /cart` - View cart
- `POST /cart/add` - Add item
- `POST /cart/remove` - Remove item
- `POST /cart/clear` - Clear cart

### User Management
- `GET /login` - Login page
- `POST /login` - Process login
- `GET /logout` - Logout
- `GET /profile` - User profile

### Order History
- `GET /orders` - List user orders
- `GET /orders/{orderNumber}` - Order details

---

## 📞 Support & Troubleshooting

### Check Build Status
```bash
mvn clean compile -DskipTests
# Should output: BUILD SUCCESS
```

### Check Server Logs
```bash
# Look for errors in logs
# Check database connectivity
# Verify email configuration
```

### Common Errors
```
Error: "Address not found"
→ Check address ID in URL
→ Verify address exists in DB

Error: "Cart is empty"
→ User must add items before checkout
→ Check cart_items table

Error: "Order creation failed"
→ Check error logs
→ Verify database connectivity
→ Check product stock
```

---

## 🎓 Learning Resources

### Key Classes to Understand
1. **WebOrderController** - Request handling
2. **OrderService** - Business logic
3. **Order** - Entity model
4. **Address** - Entity model
5. **ShoppingCart** - Entity model
6. **Payment** - Entity model

### Key Methods to Review
1. `OrderService.createOrderFromCart()`
2. `WebOrderController.placeOrder()`
3. `ShoppingCartService.getCartForUser()`
4. `AddressRepository.findByCustomer()`

---

## ✨ Best Practices

### For Developers
- Always validate user input
- Check for null before using objects
- Use proper exception handling
- Log important operations
- Use transactions for atomic operations
- Clean up resources (close connections)

### For QA
- Test all error scenarios
- Try invalid addresses
- Test empty cart
- Try invalid payment methods
- Test multiple items
- Test address addition
- Test button states

### For Operations
- Monitor database connections
- Check application logs regularly
- Monitor email delivery
- Verify backup processes
- Monitor performance metrics
- Set up alerts for errors

---

**Quick Reference Version**: 1.0  
**Last Updated**: December 21, 2025  
**Status**: ✅ Current & Accurate
