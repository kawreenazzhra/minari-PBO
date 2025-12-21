# MINARI Add Category - OOP Implementation Documentation

## Overview
Implementasi OOP dari Add Category MINARI untuk mata kuliah Pemrograman OOP. Project ini menggunakan prinsip-prinsip OOP seperti encapsulation, inheritance, composition, dan delegation pattern.

---

## Architecture Overview

```
CategoryManager (Main Class)
├── UIManager (UI Handling)
├── FormValidator (Validation Logic)
│   └── ValidationRule Classes
│       ├── CategoryNameValidator
│       ├── CategoryDescriptionValidator
│       └── ImageValidator
└── SidebarManager (Navigation)
```

---

## Class Descriptions

### 1. **ValidationRule (Base Class)**
- **File**: `ValidationRule.js`
- **Purpose**: Base class untuk semua validation rules
- **OOP Concepts**: 
  - Abstraction: Menyembunyikan logic validasi kompleks
  - Inheritance: Class lain dapat extend class ini
  - Polymorphism: Setiap validator implement `validate()` method

```javascript
class ValidationRule {
    constructor(fieldName, errorElementId) {
        this.fieldName = fieldName;
        this.errorElementId = errorElementId;
    }

    validate(value) {
        // Abstract method - harus di-override oleh subclass
        throw new Error('validate() must be implemented');
    }
}
```

### 2. **Validator Classes** (Sub-classes)

#### CategoryNameValidator
```javascript
class CategoryNameValidator extends ValidationRule {
    // Validasi category name dengan rules:
    // - Required (tidak boleh kosong)
    // - Max 100 characters
}
```

#### CategoryDescriptionValidator
```javascript
class CategoryDescriptionValidator extends ValidationRule {
    // Validasi description dengan rules:
    // - Optional
    // - Max 500 characters
}
```

#### ImageValidator
```javascript
class ImageValidator extends ValidationRule {
    // Validasi image upload dengan rules:
    // - Required
    // - Tipe: JPG, PNG, GIF
    // - Max size: 2MB
}
```

### 3. **UIManager Class**
- **File**: `UIManager.js`
- **Purpose**: Mengelola semua interaksi UI dan DOM manipulation
- **OOP Concepts**:
  - Encapsulation: Semua UI operations terbungkus dalam satu class
  - Single Responsibility: Hanya fokus pada UI management
  
**Key Methods**:
- `cacheElements()`: Cache DOM elements untuk performance
- `showValidationMessage()`: Tampilkan error/success message
- `highlightField()`: Highlight field yang error
- `showImagePreview()`: Preview image sebelum upload
- `showNotification()`: Tampilkan notification popup
- `resetForm()`: Reset form ke kondisi awal
- `getFormData()`: Get data dari form
- `isFormDirty()`: Check apakah form sudah diubah

### 4. **FormValidator Class**
- **File**: `FormValidator.js`
- **Purpose**: Mengelola validasi form dengan menggunakan validator rules
- **OOP Concepts**:
  - Composition: Menggunakan ValidationRule objects
  - Delegation: Delegate validasi ke validator rules
  - Single Responsibility: Hanya fokus pada form validation

**Key Methods**:
- `validateField()`: Validasi single field
- `validateForm()`: Validasi seluruh form
- `setupRealtimeValidation()`: Setup real-time validation saat user mengetik

### 5. **CategoryManager Class** (Main Class)
- **File**: `CategoryManager.js`
- **Purpose**: Main class yang manage keseluruhan logika
- **OOP Concepts**:
  - Composition: Menggunakan UIManager, FormValidator, SidebarManager
  - Delegation: Delegate tasks ke classes yang tepat
  - Orchestration: Mengkoordinasikan interaction antar classes

**Key Methods**:
- `initialize()`: Inisialisasi manager
- `setupEventListeners()`: Setup semua event listeners
- `handleFormSubmission()`: Handle form submission
- `handleImageUpload()`: Handle image upload
- `submitForm()`: Submit form ke server
- `handleCancel()`: Handle cancel action

### 6. **SidebarManager Class**
- **File**: `CategoryManager.js`
- **Purpose**: Mengelola sidebar navigation
- **Key Methods**:
  - `initializeSidebar()`: Set active menu item
  - `setActiveMenuItem()`: Change active menu item

---

## OOP Principles Implementation

### 1. **Encapsulation**
- Setiap class mengenkapsulasi data dan behavior-nya sendiri
- Data private disimpan di dalam class dan diakses melalui methods

```javascript
// UIManager encapsulates UI elements
class UIManager {
    constructor() {
        this.elements = {};  // Private elements storage
        this.cacheElements();
    }
    
    // Public methods untuk akses elements
    highlightField(fieldId, isValid) { ... }
    showValidationMessage(elementId, message, type) { ... }
}
```

### 2. **Inheritance**
- Validator classes inherit dari ValidationRule base class
- Reuse common functionality dan interface

```javascript
class CategoryNameValidator extends ValidationRule {
    // Inherit dari ValidationRule
    // Override validate() method
}
```

### 3. **Polymorphism**
- Setiap validator implement `validate()` method dengan behavior berbeda
- Dapat diperlakukan sebagai ValidationRule, namun behavior-nya spesifik

```javascript
// Polymorphic behavior
const validators = [
    new CategoryNameValidator(),
    new CategoryDescriptionValidator(),
    new ImageValidator()
];

validators.forEach(validator => {
    // Setiap validator validate dengan cara berbeda
    const result = validator.validate(value);
});
```

### 4. **Composition**
- CategoryManager menggunakan UIManager, FormValidator, SidebarManager
- Objects dikombinasikan untuk membentuk functionality yang lebih kompleks

```javascript
class CategoryManager {
    constructor() {
        this.uiManager = new UIManager();           // Composition
        this.formValidator = new FormValidator(this.uiManager);
        this.sidebarManager = new SidebarManager();
    }
}
```

### 5. **Delegation Pattern**
- CategoryManager delegate tasks ke objects yang specialized

```javascript
handleFormSubmission(e) {
    // Delegate validation ke FormValidator
    if (!this.formValidator.validateForm()) {
        this.uiManager.showNotification(...);
        return;
    }
    
    // Delegate UI updates ke UIManager
    this.uiManager.setSubmitButtonLoading(true);
}
```

### 6. **Single Responsibility Principle (SRP)**
- Setiap class punya satu responsibility:
  - **ValidationRule**: Validasi specific field
  - **UIManager**: Manage UI dan DOM
  - **FormValidator**: Manage form validation
  - **CategoryManager**: Orchestrate keseluruhan process
  - **SidebarManager**: Manage sidebar navigation

### 7. **Separation of Concerns**
- Validation logic terpisah dari UI logic
- Form handling terpisah dari UI updates
- Navigation terpisah dari form handling

---

## File Structure

```
src/main/resources/
├── static/
│   └── js/
│       ├── classes/
│       │   ├── ValidationRule.js      # Base validator class
│       │   ├── UIManager.js            # UI management class
│       │   └── FormValidator.js        # Form validation class
│       └── CategoryManager.js           # Main orchestrator class
└── templates/
    └── admin/
        └── add-category-oop.html        # HTML view
```

---

## Usage

### 1. Load Scripts dalam order yang benar
```html
<script src="/js/classes/ValidationRule.js"></script>
<script src="/js/classes/UIManager.js"></script>
<script src="/js/classes/FormValidator.js"></script>
<script src="/js/CategoryManager.js"></script>
```

### 2. CategoryManager akan auto-initialize
```javascript
document.addEventListener('DOMContentLoaded', function() {
    window.categoryManager = new CategoryManager();
});
```

### 3. Akses CategoryManager dari console (for debugging)
```javascript
// Get form data
window.categoryManager.uiManager.getFormData();

// Manual validate
window.categoryManager.formValidator.validateForm();

// Show notification
window.categoryManager.uiManager.showNotification('Test', 'success');
```

---

## Validation Flow

```
User Input
    ↓
setupRealtimeValidation (Event Listener)
    ↓
FormValidator.validateField()
    ↓
Get appropriate ValidationRule
    ↓
ValidationRule.validate()
    ↓
Return validation result
    ↓
UIManager.showValidationMessage()
    ↓
UIManager.highlightField()
    ↓
Update UI
```

---

## Form Submission Flow

```
User Click Submit
    ↓
handleFormSubmission()
    ↓
FormValidator.validateForm() - Validate all fields
    ↓
    ├─ isValid → submitForm()
    │             ↓
    │             setSubmitButtonLoading(true)
    │             ↓
    │             Collect form data
    │             ↓
    │             Send to server (simulated)
    │             ↓
    │             showNotification('Success')
    │             ↓
    │             Redirect
    │
    └─ !isValid → showNotification('Error')
```

---

## Advanced Features

### 1. Real-time Validation
- Validation saat user mengetik
- Validation saat field lose focus (blur)
- Immediate feedback untuk user

### 2. Image Preview
- Async image preview loading
- File size validation
- File type validation

### 3. Unsaved Changes Warning
- Warn user sebelum meninggalkan halaman
- Warn user saat cancel dengan unsaved changes

### 4. Error Handling
- Try-catch untuk API calls
- Error messages di UI
- Console logging untuk debugging

### 5. Form State Management
- Check form dirty status
- Reset form ke initial state
- Preserve user input saat ada error

---

## Benefits of OOP Approach

1. **Maintainability**: Kode terstruktur dan mudah dipahami
2. **Reusability**: Classes dapat di-reuse di project lain
3. **Testability**: Setiap class dapat di-test secara independen
4. **Scalability**: Mudah menambah features baru
5. **Flexibility**: Mudah mengubah behavior dengan override methods
6. **Readability**: Code lebih self-documenting

---

## Extension Points

### Menambah validator baru:
```javascript
class CustomValidator extends ValidationRule {
    validate(value) {
        // Custom validation logic
        return {
            isValid: true/false,
            message: 'Custom message',
            type: 'error/success/warning'
        };
    }
}

// Register di FormValidator
this.validators.customField = new CustomValidator();
```

### Menambah event handler:
```javascript
// Di CategoryManager.setupEventListeners()
element.addEventListener('event', (e) => this.handleNewEvent(e));
```

### Customize UI behavior:
```javascript
// Extend UIManager
class CustomUIManager extends UIManager {
    customMethod() { ... }
}

// Use di CategoryManager
this.uiManager = new CustomUIManager();
```

---

## Conclusion

Implementasi ini menunjukkan best practices dalam OOP:
- ✅ Clear separation of concerns
- ✅ Single responsibility principle
- ✅ DRY (Don't Repeat Yourself)
- ✅ Proper use of inheritance dan composition
- ✅ Polymorphism untuk flexibility
- ✅ Encapsulation untuk data hiding

Struktur ini membuat code lebih maintainable, testable, dan scalable untuk production applications.

---

## Author Notes
Implementasi ini disesuaikan untuk keperluan pembelajaran OOP sambil mempertahankan semua functionality dari original code. Semua fitur asli tetap ada, hanya diorganisir dengan better structure menggunakan OOP principles.
