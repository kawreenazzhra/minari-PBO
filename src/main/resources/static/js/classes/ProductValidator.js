/**
 * Product Validator Classes - OOP Implementation
 * 
 * Inheritance hierarchy untuk validasi product fields
 * Base class: ProductValidator (abstract pattern)
 * Subclasses: ProductNameValidator, PriceValidator, StockValidator, DescriptionValidator, ImageValidator
 * 
 * Demonstrates: Inheritance, Polymorphism, Single Responsibility
 */

/**
 * Base class untuk semua product validators
 * Defines interface yang harus diimplementasikan oleh subclasses
 */
class ProductValidator {
    constructor(fieldName) {
        this.fieldName = fieldName;
        this.errorMessage = '';
    }

    /**
     * Validate value - MUST be implemented by subclasses
     * @param {*} value - Value to validate
     * @returns {boolean} - True if valid, false otherwise
     */
    validate(value) {
        throw new Error('validate() must be implemented by subclass');
    }

    /**
     * Get error message for invalid value
     * @returns {string} - Error message
     */
    getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Get field name
     * @returns {string} - Field name
     */
    getFieldName() {
        return this.fieldName;
    }
}

/**
 * Validator untuk Product Name
 * Requirements: 3-100 characters, no special chars
 */
class ProductNameValidator extends ProductValidator {
    constructor() {
        super('Product Name');
    }

    validate(value) {
        if (!value) {
            this.errorMessage = 'Product name is required';
            return false;
        }

        if (value.trim().length < 3) {
            this.errorMessage = 'Product name must be at least 3 characters';
            return false;
        }

        if (value.length > 100) {
            this.errorMessage = 'Product name cannot exceed 100 characters';
            return false;
        }

        // Check for invalid special characters
        const invalidChars = /[<>{}[\]]/;
        if (invalidChars.test(value)) {
            this.errorMessage = 'Product name contains invalid characters';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}

/**
 * Validator untuk Product Price
 * Requirements: Must be number > 0, max 999,999,999
 */
class PriceValidator extends ProductValidator {
    constructor() {
        super('Price');
    }

    validate(value) {
        if (value === null || value === undefined || value === '') {
            this.errorMessage = 'Price is required';
            return false;
        }

        const price = parseFloat(value);

        if (isNaN(price)) {
            this.errorMessage = 'Price must be a valid number';
            return false;
        }

        if (price <= 0) {
            this.errorMessage = 'Price must be greater than 0';
            return false;
        }

        if (price > 999999999) {
            this.errorMessage = 'Price cannot exceed 999,999,999';
            return false;
        }

        // Check decimal places (max 2)
        if (!Number.isInteger(price * 100)) {
            this.errorMessage = 'Price can have maximum 2 decimal places';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}

/**
 * Validator untuk Product Stock
 * Requirements: Must be integer >= 0, max 999,999
 */
class StockValidator extends ProductValidator {
    constructor() {
        super('Stock');
    }

    validate(value) {
        if (value === null || value === undefined || value === '') {
            this.errorMessage = 'Stock is required';
            return false;
        }

        const stock = parseInt(value);

        if (isNaN(stock)) {
            this.errorMessage = 'Stock must be a valid number';
            return false;
        }

        if (stock < 0) {
            this.errorMessage = 'Stock cannot be negative';
            return false;
        }

        if (stock > 999999) {
            this.errorMessage = 'Stock cannot exceed 999,999';
            return false;
        }

        if (!Number.isInteger(stock)) {
            this.errorMessage = 'Stock must be a whole number';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}

/**
 * Validator untuk Product Description
 * Requirements: 10-1000 characters
 */
class DescriptionValidator extends ProductValidator {
    constructor() {
        super('Description');
    }

    validate(value) {
        if (!value) {
            this.errorMessage = 'Description is required';
            return false;
        }

        if (value.trim().length < 10) {
            this.errorMessage = 'Description must be at least 10 characters';
            return false;
        }

        if (value.length > 1000) {
            this.errorMessage = 'Description cannot exceed 1000 characters';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}

/**
 * Validator untuk Product Category
 * Requirements: Must select a category
 */
class CategoryValidator extends ProductValidator {
    constructor() {
        super('Category');
    }

    validate(value) {
        if (!value || value === '') {
            this.errorMessage = 'Category is required';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}

/**
 * Validator untuk Product Images
 * Requirements: JPG/PNG/GIF, max 5MB per image, min 1 image
 */
class ProductImageValidator extends ProductValidator {
    constructor() {
        super('Product Images');
        this.maxFileSize = 5 * 1024 * 1024; // 5MB
        this.allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
        this.allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif'];
    }

    /**
     * Validate single image file
     * @param {File} file - Image file to validate
     * @returns {boolean} - True if valid
     */
    validate(file) {
        if (!file) {
            this.errorMessage = 'Image is required';
            return false;
        }

        return this.validateFile(file);
    }

    /**
     * Validate multiple files
     * @param {FileList|Array} files - Multiple image files
     * @returns {boolean} - True if all valid
     */
    validateMultiple(files) {
        if (!files || files.length === 0) {
            this.errorMessage = 'At least one image is required';
            return false;
        }

        for (let i = 0; i < files.length; i++) {
            if (!this.validateFile(files[i])) {
                this.errorMessage = `Image ${i + 1}: ${this.errorMessage}`;
                return false;
            }
        }

        this.errorMessage = '';
        return true;
    }

    /**
     * Validate file format and size
     * @private
     */
    validateFile(file) {
        // Check file size
        if (file.size > this.maxFileSize) {
            this.errorMessage = `File size exceeds 5MB limit (${this.getFileSizeInMB(file.size)}MB)`;
            return false;
        }

        // Check file type by MIME type
        if (!this.allowedTypes.includes(file.type)) {
            this.errorMessage = `Invalid image format. Allowed: JPG, PNG, GIF`;
            return false;
        }

        // Check file extension
        const fileExtension = '.' + file.name.split('.').pop().toLowerCase();
        if (!this.allowedExtensions.includes(fileExtension)) {
            this.errorMessage = `Invalid file extension: ${fileExtension}`;
            return false;
        }

        return true;
    }

    /**
     * Get file size in MB
     * @private
     */
    getFileSizeInMB(sizeInBytes) {
        return (sizeInBytes / (1024 * 1024)).toFixed(2);
    }
}

/**
 * Validator untuk Product SKU (Stock Keeping Unit)
 * Requirements: 3-50 characters, alphanumeric + dash/underscore
 */
class SKUValidator extends ProductValidator {
    constructor() {
        super('SKU');
    }

    validate(value) {
        if (!value) {
            this.errorMessage = 'SKU is required';
            return false;
        }

        if (value.length < 3) {
            this.errorMessage = 'SKU must be at least 3 characters';
            return false;
        }

        if (value.length > 50) {
            this.errorMessage = 'SKU cannot exceed 50 characters';
            return false;
        }

        // Check format: alphanumeric, dash, underscore only
        const validFormat = /^[a-zA-Z0-9_-]+$/;
        if (!validFormat.test(value)) {
            this.errorMessage = 'SKU can only contain letters, numbers, dash, and underscore';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}

/**
 * Validator untuk Product Weight
 * Requirements: Optional, if provided must be number > 0, max 100kg
 */
class WeightValidator extends ProductValidator {
    constructor() {
        super('Weight');
    }

    validate(value) {
        // Weight is optional
        if (!value || value === '') {
            this.errorMessage = '';
            return true;
        }

        const weight = parseFloat(value);

        if (isNaN(weight)) {
            this.errorMessage = 'Weight must be a valid number';
            return false;
        }

        if (weight <= 0) {
            this.errorMessage = 'Weight must be greater than 0';
            return false;
        }

        if (weight > 100) {
            this.errorMessage = 'Weight cannot exceed 100 kg';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}

/**
 * Validator untuk Product Discount (optional)
 * Requirements: 0-99% discount
 */
class DiscountValidator extends ProductValidator {
    constructor() {
        super('Discount');
    }

    validate(value) {
        // Discount is optional
        if (!value || value === '') {
            this.errorMessage = '';
            return true;
        }

        const discount = parseFloat(value);

        if (isNaN(discount)) {
            this.errorMessage = 'Discount must be a valid number';
            return false;
        }

        if (discount < 0 || discount > 99) {
            this.errorMessage = 'Discount must be between 0 and 99 percent';
            return false;
        }

        this.errorMessage = '';
        return true;
    }
}
