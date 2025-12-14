/**
 * PromotionValidator.js
 * =====================
 * Base validator and 8 specialized promotion field validators
 * 
 * Part of Phase 4: Add Promotion OOP Implementation
 * Handles validation for all promotion fields with OOP inheritance pattern
 * 
 * Validators:
 * - PromotionNameValidator
 * - PromotionCodeValidator
 * - PromotionTypeValidator
 * - PromotionDiscountValidator
 * - PromotionStartDateValidator
 * - PromotionEndDateValidator
 * - PromotionMinPurchaseValidator
 * - PromotionMaxUsageValidator
 */

/**
 * Base PromotionValidator Class
 * Provides common validation interface and methods
 */
class PromotionValidator {
    constructor() {
        if (this.constructor === PromotionValidator) {
            throw new Error('PromotionValidator is abstract and cannot be instantiated directly');
        }
    }

    /**
     * Validate the value
     * @param {*} value - Value to validate
     * @returns {boolean} - True if valid, false otherwise
     */
    validate(value) {
        throw new Error('validate() method must be implemented by subclass');
    }

    /**
     * Get error message for invalid value
     * @returns {string} - Error message
     */
    getErrorMessage() {
        throw new Error('getErrorMessage() method must be implemented by subclass');
    }

    /**
     * Get field name
     * @returns {string} - Field display name
     */
    getFieldName() {
        throw new Error('getFieldName() method must be implemented by subclass');
    }

    /**
     * Helper: Check if value is empty
     * @param {*} value - Value to check
     * @returns {boolean}
     */
    isEmpty(value) {
        return value === null || value === undefined || value.toString().trim() === '';
    }

    /**
     * Helper: Check if string only contains alphanumeric and allowed special chars
     * @param {string} value - Value to check
     * @param {string} allowedChars - Additional allowed characters
     * @returns {boolean}
     */
    isValidAlphanumeric(value, allowedChars = '') {
        const pattern = new RegExp(`^[a-zA-Z0-9${allowedChars}]*$`);
        return pattern.test(value);
    }

    /**
     * Helper: Check if string only contains alphanumeric, dash, underscore
     * @param {string} value - Value to check
     * @returns {boolean}
     */
    isValidCode(value) {
        return /^[A-Z0-9\-_]+$/.test(value);
    }

    /**
     * Helper: Check if date is valid
     * @param {string} dateString - Date string
     * @returns {boolean}
     */
    isValidDate(dateString) {
        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    }

    /**
     * Helper: Check if date1 is before date2
     * @param {string} date1 - First date
     * @param {string} date2 - Second date
     * @returns {boolean}
     */
    isDateBefore(date1, date2) {
        return new Date(date1) < new Date(date2);
    }
}

/**
 * PromotionNameValidator
 * Validates promotion name field
 * Rules: 3-100 characters, no special chars
 */
class PromotionNameValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.minLength = 3;
        this.maxLength = 100;
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Promotion name is required';
            return false;
        }

        const trimmed = value.toString().trim();

        if (trimmed.length < this.minLength) {
            this.errorMessage = `Promotion name must be at least ${this.minLength} characters`;
            return false;
        }

        if (trimmed.length > this.maxLength) {
            this.errorMessage = `Promotion name cannot exceed ${this.maxLength} characters`;
            return false;
        }

        // Check for invalid special characters
        if (!/^[a-zA-Z0-9\s\-&(),.']+$/.test(trimmed)) {
            this.errorMessage = 'Promotion name contains invalid characters';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Promotion Name';
    }
}

/**
 * PromotionCodeValidator
 * Validates promotion code (coupon code)
 * Rules: 3-20 characters, uppercase alphanumeric + dash/underscore
 */
class PromotionCodeValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.minLength = 3;
        this.maxLength = 20;
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Promotion code is required';
            return false;
        }

        const code = value.toString().toUpperCase().trim();

        if (code.length < this.minLength) {
            this.errorMessage = `Promotion code must be at least ${this.minLength} characters`;
            return false;
        }

        if (code.length > this.maxLength) {
            this.errorMessage = `Promotion code cannot exceed ${this.maxLength} characters`;
            return false;
        }

        if (!this.isValidCode(code)) {
            this.errorMessage = 'Promotion code must contain only uppercase letters, numbers, dash, and underscore';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Promotion Code';
    }
}

/**
 * PromotionTypeValidator
 * Validates promotion type
 * Rules: Must be one of: percentage, fixed-amount, buy-one-get-one
 */
class PromotionTypeValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.validTypes = ['percentage', 'fixed-amount', 'buy-one-get-one', 'free-shipping'];
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Promotion type is required';
            return false;
        }

        if (!this.validTypes.includes(value.toString().toLowerCase())) {
            this.errorMessage = `Promotion type must be one of: ${this.validTypes.join(', ')}`;
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Promotion Type';
    }

    getValidTypes() {
        return this.validTypes;
    }
}

/**
 * PromotionDiscountValidator
 * Validates discount value based on promotion type
 * Rules: percentage (0-99%), fixed-amount (1-9,999,999), buy-one-get-one (1-100%)
 */
class PromotionDiscountValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.promotionType = 'percentage'; // Default
    }

    validate(value, promotionType = this.promotionType) {
        this.promotionType = promotionType;

        if (this.isEmpty(value)) {
            this.errorMessage = 'Discount value is required';
            return false;
        }

        const discountValue = parseFloat(value);

        if (isNaN(discountValue)) {
            this.errorMessage = 'Discount must be a valid number';
            return false;
        }

        switch (promotionType) {
            case 'percentage':
                return this.validatePercentage(discountValue);
            case 'fixed-amount':
                return this.validateFixedAmount(discountValue);
            case 'buy-one-get-one':
                return this.validateBOGO(discountValue);
            case 'free-shipping':
                return this.validateFreeShipping(discountValue);
            default:
                this.errorMessage = 'Invalid promotion type';
                return false;
        }
    }

    validatePercentage(value) {
        if (value <= 0 || value > 99) {
            this.errorMessage = 'Percentage discount must be between 1 and 99';
            return false;
        }

        if (!/^\d+(\.\d{1,2})?$/.test(value.toString())) {
            this.errorMessage = 'Percentage can have maximum 2 decimal places';
            return false;
        }

        return true;
    }

    validateFixedAmount(value) {
        if (value <= 0 || value > 9999999) {
            this.errorMessage = 'Fixed amount must be between 1 and 9,999,999';
            return false;
        }

        if (!/^\d+(\.\d{1,2})?$/.test(value.toString())) {
            this.errorMessage = 'Fixed amount can have maximum 2 decimal places';
            return false;
        }

        return true;
    }

    validateBOGO(value) {
        if (value <= 0 || value > 100) {
            this.errorMessage = 'Buy-One-Get-One discount must be between 1 and 100%';
            return false;
        }

        return true;
    }

    validateFreeShipping(value) {
        // Free shipping only needs to be > 0
        if (value <= 0) {
            this.errorMessage = 'Minimum purchase for free shipping must be greater than 0';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Discount Value';
    }
}

/**
 * PromotionStartDateValidator
 * Validates promotion start date
 * Rules: Must be today or later, valid date format
 */
class PromotionStartDateValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Start date is required';
            return false;
        }

        if (!this.isValidDate(value)) {
            this.errorMessage = 'Start date must be a valid date';
            return false;
        }

        const startDate = new Date(value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (startDate < today) {
            this.errorMessage = 'Start date cannot be in the past';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Start Date';
    }
}

/**
 * PromotionEndDateValidator
 * Validates promotion end date
 * Rules: Must be after start date, valid date format
 */
class PromotionEndDateValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value, startDate = null) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'End date is required';
            return false;
        }

        if (!this.isValidDate(value)) {
            this.errorMessage = 'End date must be a valid date';
            return false;
        }

        const endDate = new Date(value);

        if (startDate) {
            const start = new Date(startDate);
            if (endDate <= start) {
                this.errorMessage = 'End date must be after start date';
                return false;
            }
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'End Date';
    }
}

/**
 * PromotionMinPurchaseValidator
 * Validates minimum purchase amount
 * Rules: Optional field, if provided must be > 0, max 9,999,999
 */
class PromotionMinPurchaseValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
        }

        const minPurchase = parseFloat(value);

        if (isNaN(minPurchase)) {
            this.errorMessage = 'Minimum purchase must be a valid number';
            return false;
        }

        if (minPurchase <= 0) {
            this.errorMessage = 'Minimum purchase must be greater than 0';
            return false;
        }

        if (minPurchase > 9999999) {
            this.errorMessage = 'Minimum purchase cannot exceed 9,999,999';
            return false;
        }

        if (!/^\d+(\.\d{1,2})?$/.test(minPurchase.toString())) {
            this.errorMessage = 'Minimum purchase can have maximum 2 decimal places';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Minimum Purchase';
    }
}

/**
 * PromotionMaxUsageValidator
 * Validates maximum usage limit
 * Rules: Optional field, if provided must be >= 1, max 999,999
 */
class PromotionMaxUsageValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
        }

        const maxUsage = parseInt(value, 10);

        if (isNaN(maxUsage)) {
            this.errorMessage = 'Maximum usage must be a valid number';
            return false;
        }

        if (maxUsage < 1) {
            this.errorMessage = 'Maximum usage must be at least 1';
            return false;
        }

        if (maxUsage > 999999) {
            this.errorMessage = 'Maximum usage cannot exceed 999,999';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Maximum Usage';
    }
}

/**
 * PromotionDescriptionValidator
 * Validates promotion description
 * Rules: Optional field, 0-500 characters
 */
class PromotionDescriptionValidator extends PromotionValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.maxLength = 500;
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
        }

        const description = value.toString().trim();

        if (description.length > this.maxLength) {
            this.errorMessage = `Description cannot exceed ${this.maxLength} characters`;
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Description';
    }
}

// Export validators
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        PromotionValidator,
        PromotionNameValidator,
        PromotionCodeValidator,
        PromotionTypeValidator,
        PromotionDiscountValidator,
        PromotionStartDateValidator,
        PromotionEndDateValidator,
        PromotionMinPurchaseValidator,
        PromotionMaxUsageValidator,
        PromotionDescriptionValidator
    };
}
