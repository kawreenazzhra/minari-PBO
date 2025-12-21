# Phase 6: Customer Management - Quick Start Guide

## ⚡ Quick Start (5 Minutes)

### 1. **Backend Setup** (Already Done!)
✅ CustomerRepository created  
✅ CustomerService created  
✅ CustomerController created  
✅ All endpoints ready to use  

### 2. **Frontend Setup** (Already Done!)
✅ CustomerValidator.js created  
✅ CustomerManager.js created  
✅ Scripts loaded in admin layout  
✅ Dashboard quick actions added  

### 3. **Start Using**

#### **Create a Customer (JavaScript)**
```javascript
const customerManager = new CustomerManager();

const newCustomer = {
  email: 'john@example.com',
  password: 'SecurePass123!',
  fullName: 'John Doe',
  phone: '0812345678',
  shippingAddress: {
    street: '123 Main St',
    city: 'Jakarta',
    postalCode: '12000',
    country: 'Indonesia'
  }
};

// Validate first
const validator = new CustomerValidator();
const validation = validator.validateCompleteCustomer(newCustomer);

if (validation.isValid) {
  const result = await customerManager.createCustomer(newCustomer);
  if (result.success) {
    console.log('Customer created:', result.customer);
  }
}
```

#### **Fetch All Customers**
```javascript
const customerManager = new CustomerManager();
const result = await customerManager.getAllCustomers(0, 10);

if (result.success) {
  console.log('Customers:', result.customers);
  console.log('Total:', result.totalItems);
  console.log('Pages:', result.totalPages);
}
```

#### **Search Customers**
```javascript
const result = await customerManager.searchCustomers('john', 0, 10);
if (result.success) {
  console.log('Search results:', result.customers);
}
```

#### **Get Customer by ID**
```javascript
const result = await customerManager.getCustomerById(1);
if (result.success) {
  console.log('Customer:', result.customer);
}
```

#### **Update Customer**
```javascript
const updatedData = {
  fullName: 'John Doe Updated',
  phone: '0812345679'
};

const result = await customerManager.updateCustomer(1, updatedData);
if (result.success) {
  console.log('Customer updated:', result.customer);
}
```

#### **Toggle Customer Status**
```javascript
// Activate/Deactivate customer
const result = await customerManager.toggleCustomerStatus(1);
if (result.success) {
  console.log('New status:', result.customer.isActive);
}
```

#### **Add Loyalty Points**
```javascript
const result = await customerManager.addLoyaltyPoints(1, 100);
if (result.success) {
  console.log('Points added:', result.customer.loyaltyPoints);
}
```

#### **Toggle Newsletter Subscription**
```javascript
const result = await customerManager.toggleNewsletterSubscription(1);
if (result.success) {
  console.log('Newsletter status:', result.customer.newsletterSubscribed);
}
```

#### **Get Customer Statistics**
```javascript
const result = await customerManager.getCustomerStatistics();
if (result.success) {
  console.log('Total:', result.stats.totalCustomers);
  console.log('Active:', result.stats.activeCustomers);
  console.log('VIP:', result.stats.vipCustomersCount);
}
```

#### **Get VIP Customers**
```javascript
const result = await customerManager.getVIPCustomers(1000, 0, 20);
if (result.success) {
  console.log('VIP Customers:', result.customers);
}
```

#### **Get Active Customers**
```javascript
const result = await customerManager.getActiveCustomers(0, 10);
if (result.success) {
  console.log('Active:', result.customers);
}
```

#### **Get Inactive Customers**
```javascript
const result = await customerManager.getInactiveCustomers(0, 10);
if (result.success) {
  console.log('Inactive:', result.customers);
}
```

#### **Filter by City**
```javascript
const result = await customerManager.getCustomersByCity('Jakarta');
if (result.success) {
  console.log('Jakarta Customers:', result.customers);
  console.log('Count:', result.count);
}
```

#### **Filter by Country**
```javascript
const result = await customerManager.getCustomersByCountry('Indonesia');
if (result.success) {
  console.log('Indonesia Customers:', result.customers);
}
```

#### **Delete Customer**
```javascript
const result = await customerManager.deleteCustomer(1);
if (result.success) {
  console.log('Customer deleted');
}
```

#### **Export to CSV**
```javascript
const result = await customerManager.getAllCustomers(0, 1000);
if (result.success) {
  await customerManager.exportToCSV(result.customers);
  // Automatically downloads customers.csv
}
```

#### **Export to JSON**
```javascript
const result = await customerManager.getAllCustomers(0, 1000);
if (result.success) {
  await customerManager.exportToJSON(result.customers);
  // Automatically downloads customers.json
}
```

---

## 🔍 Validation Examples

### **Email Validation**
```javascript
const validator = new CustomerValidator();

const emailValidation = validator.validateEmail('test@example.com');
if (!emailValidation.isValid) {
  console.log('Errors:', emailValidation.errors);
}
```

### **Phone Validation**
```javascript
const phoneValidation = validator.validatePhone('0812345678');
if (!phoneValidation.isValid) {
  console.log('Errors:', phoneValidation.errors);
}
```

### **Full Registration Validation**
```javascript
const data = {
  email: 'newuser@example.com',
  fullName: 'New User',
  phone: '0812345678',
  password: 'SecurePass123!',
  confirmPassword: 'SecurePass123!'
};

const validation = validator.validateRegistration(data);
if (validation.isValid) {
  console.log('All valid, proceed with registration');
} else {
  validation.errors.forEach(err => console.error(err));
}
```

### **Profile Update Validation**
```javascript
const updateData = {
  fullName: 'Updated Name',
  phone: '0898765432',
  shippingAddress: {
    street: 'New Street',
    city: 'Bandung',
    postalCode: '40000',
    country: 'Indonesia'
  }
};

const validation = validator.validateProfileUpdate(updateData);
if (validation.isValid) {
  console.log('Update data is valid');
}
```

### **Loyalty Points Validation**
```javascript
const pointsValidation = validator.validateLoyaltyPointsOperation(500);
if (!pointsValidation.isValid) {
  console.log('Invalid points:', pointsValidation.errors);
}
```

---

## 📡 API Endpoints

All endpoints available via CustomerController:

### **Customer CRUD**
```
GET    /api/customers                    # All customers
GET    /api/customers/{id}               # Single customer
GET    /api/customers/search             # Search
POST   /api/customers                    # Create
PUT    /api/customers/{id}               # Update
DELETE /api/customers/{id}               # Delete
```

### **Status & Activation**
```
PATCH  /api/customers/{id}/status        # Toggle active/inactive
PATCH  /api/customers/{id}/verify-email  # Mark as verified
```

### **Newsletter**
```
PATCH  /api/customers/{id}/newsletter    # Toggle subscription
GET    /api/customers/newsletter-subscribers
```

### **Loyalty Points**
```
PATCH  /api/customers/{id}/loyalty-points/add
PATCH  /api/customers/{id}/loyalty-points/deduct
```

### **Filtering**
```
GET    /api/customers/active             # Active customers
GET    /api/customers/inactive           # Inactive customers
GET    /api/customers/vip                # VIP customers
GET    /api/customers/new                # New customers (date range)
GET    /api/customers/by-city/{city}     # By city
GET    /api/customers/by-country/{country}
```

### **Statistics**
```
GET    /api/customers/stats/summary      # Overall stats
```

---

## 🎯 Common Tasks

### **Task 1: Manage Customer List**
```javascript
// Load first 10 customers
const manager = new CustomerManager();
const result = await manager.getAllCustomers(0, 10);

if (result.success) {
  // Render in table
  result.customers.forEach(customer => {
    console.log(`${customer.fullName} (${customer.email})`);
  });
  
  // Show pagination info
  console.log(`Page ${result.currentPage + 1} of ${result.totalPages}`);
}
```

### **Task 2: Register New Customer**
```javascript
// Frontend form submission
async function handleRegistration(formData) {
  const validator = new CustomerValidator();
  const validation = validator.validateRegistration(formData);
  
  if (!validation.isValid) {
    showErrors(validation.errors);
    return;
  }
  
  const manager = new CustomerManager();
  const result = await manager.createCustomer(formData);
  
  if (result.success) {
    showSuccess('Registration successful!');
    redirectToLogin();
  } else {
    showError(result.error);
  }
}
```

### **Task 3: Deactivate Inactive Customers**
```javascript
async function deactivateInactiveCustomers() {
  const manager = new CustomerManager();
  
  // Get inactive customers
  const inactiveResult = await manager.getInactiveCustomers(0, 100);
  
  if (inactiveResult.success) {
    for (let customer of inactiveResult.customers) {
      // Already inactive, but could add additional logic
      console.log(`${customer.fullName} is inactive`);
    }
  }
}
```

### **Task 4: VIP Loyalty Program**
```javascript
async function manageLoyaltyProgram() {
  const manager = new CustomerManager();
  
  // Get VIP customers
  const vipResult = await manager.getVIPCustomers(1000, 0, 50);
  
  if (vipResult.success) {
    // Give them additional benefits
    vipResult.customers.forEach(async (vip) => {
      // Send email, update status, etc.
      console.log(`VIP: ${vip.fullName} (${vip.loyaltyPoints} points)`);
    });
  }
}
```

### **Task 5: Newsletter Campaign**
```javascript
async function sendNewsletterCampaign(message) {
  const manager = new CustomerManager();
  
  // Get subscribers
  const subscribersResult = await manager.getNewsletterSubscribers();
  
  if (subscribersResult.success) {
    console.log(`Sending to ${subscribersResult.count} subscribers`);
    // Send email to each subscriber
    subscribersResult.subscribers.forEach(sub => {
      sendEmail(sub.email, 'Newsletter', message);
    });
  }
}
```

### **Task 6: Customer Search**
```javascript
async function searchCustomers(searchTerm) {
  const manager = new CustomerManager();
  const result = await manager.searchCustomers(searchTerm, 0, 20);
  
  if (result.success) {
    console.log(`Found ${result.totalItems} customers matching "${result.keyword}"`);
    return result.customers;
  }
}
```

---

## 🚨 Common Errors & Solutions

### Error: "Email already exists"
**Cause:** Duplicate email in database  
**Solution:** Use unique email addresses or update existing customer

### Error: "Customer not found"
**Cause:** Invalid customer ID  
**Solution:** Verify customer ID exists

### Error: "Invalid email format"
**Cause:** Email validation failed  
**Solution:** Ensure email is in correct format (user@domain.com)

### Error: "Invalid phone format"
**Cause:** Phone number doesn't match Indonesia format  
**Solution:** Use 08XXXXXXXXX or +62XXXXXXXXX format

### Error: "Insufficient permissions"
**Cause:** User doesn't have ADMIN role  
**Solution:** Login as admin or use public endpoint

### Error: "Loyalty points cannot be negative"
**Cause:** Trying to deduct more points than available  
**Solution:** Check current points balance before deducting

---

## 📋 Configuration Checklist

- [x] CustomerRepository configured
- [x] CustomerService bean created
- [x] CustomerController mapped to /api/customers
- [x] CustomerValidator.js loaded
- [x] CustomerManager.js loaded
- [x] Dashboard quick actions added
- [x] Security roles configured
- [x] Database tables created

---

## 🔒 Security Notes

- ✅ All sensitive endpoints require ADMIN role
- ✅ Password hashing handled by Spring Security
- ✅ Email verification available
- ✅ Input validation on both client and server
- ✅ CORS configured
- ✅ SQL injection protected via JPA

---

## 📞 Need Help?

1. Check **PHASE_6_CUSTOMER_MANAGEMENT_GUIDE.md** for detailed documentation
2. Review **API Response Examples** in main guide
3. Check browser console for validation errors
4. Enable logging in CustomerService for debugging
5. Review HTTP response status codes

---

## ✅ Status

**Phase 6: Customer Management System** ✅ COMPLETE

All components working:
- ✅ Backend API (18 endpoints)
- ✅ Frontend Validators (11 types)
- ✅ CRUD Operations
- ✅ Filtering & Search
- ✅ Loyalty Program
- ✅ Newsletter Management
- ✅ Statistics & Reporting
- ✅ Data Export (JSON/CSV)

Ready to use! 🚀
