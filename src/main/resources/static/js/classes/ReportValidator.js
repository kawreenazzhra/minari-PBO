/**
 * ReportValidator.js
 * ===================
 * Validators for report/dashboard date range and filter selections
 * 
 * Part of Phase 5: Add Report/Dashboard OOP Implementation
 * Handles validation for date ranges, metrics, and filter parameters
 */

/**
 * Base ReportValidator Class
 */
class ReportValidator {
    constructor() {
        if (this.constructor === ReportValidator) {
            throw new Error('ReportValidator is abstract and cannot be instantiated directly');
        }
    }

    validate(value) {
        throw new Error('validate() method must be implemented by subclass');
    }

    getErrorMessage() {
        throw new Error('getErrorMessage() method must be implemented by subclass');
    }

    getFieldName() {
        throw new Error('getFieldName() method must be implemented by subclass');
    }

    isEmpty(value) {
        return value === null || value === undefined || value.toString().trim() === '';
    }

    isValidDate(dateString) {
        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    }

    isDateBefore(date1, date2) {
        return new Date(date1) < new Date(date2);
    }
}

/**
 * DateRangeValidator
 * Validates start and end dates for reports
 * Rules: Valid dates, end date after start date
 */
class DateRangeValidator extends ReportValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(startDate, endDate) {
        if (this.isEmpty(startDate)) {
            this.errorMessage = 'Start date is required';
            return false;
        }

        if (this.isEmpty(endDate)) {
            this.errorMessage = 'End date is required';
            return false;
        }

        if (!this.isValidDate(startDate)) {
            this.errorMessage = 'Start date must be a valid date';
            return false;
        }

        if (!this.isValidDate(endDate)) {
            this.errorMessage = 'End date must be a valid date';
            return false;
        }

        if (!this.isDateBefore(startDate, endDate)) {
            this.errorMessage = 'End date must be after start date';
            return false;
        }

        // Check if date range is not too large (max 1 year)
        const start = new Date(startDate);
        const end = new Date(endDate);
        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays > 365) {
            this.errorMessage = 'Date range cannot exceed 365 days';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Date Range';
    }
}

/**
 * ReportTypeValidator
 * Validates report type selection
 * Rules: Must be one of valid report types
 */
class ReportTypeValidator extends ReportValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.validTypes = [
            'sales',
            'orders',
            'customers',
            'products',
            'inventory',
            'revenue',
            'top-products',
            'top-customers'
        ];
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Report type is required';
            return false;
        }

        if (!this.validTypes.includes(value.toString().toLowerCase())) {
            this.errorMessage = `Report type must be one of: ${this.validTypes.join(', ')}`;
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Report Type';
    }

    getValidTypes() {
        return this.validTypes;
    }
}

/**
 * FormatValidator
 * Validates export format
 * Rules: pdf, csv, excel, json
 */
class FormatValidator extends ReportValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.validFormats = ['pdf', 'csv', 'excel', 'json'];
    }

    validate(value) {
        if (this.isEmpty(value)) {
            this.errorMessage = 'Format is required';
            return false;
        }

        if (!this.validFormats.includes(value.toString().toLowerCase())) {
            this.errorMessage = `Format must be one of: ${this.validFormats.join(', ')}`;
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Export Format';
    }
}

/**
 * CategoryFilterValidator
 * Validates category filter selection
 * Rules: Optional, but if selected must be valid category
 */
class CategoryFilterValidator extends ReportValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.validCategories = [
            'shirtblouse',
            'dresses',
            'accessories',
            'outerwear',
            'shoes',
            'activewear'
        ];
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
        }

        if (!this.validCategories.includes(value.toString().toLowerCase())) {
            this.errorMessage = `Category must be one of: ${this.validCategories.join(', ')}`;
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Category Filter';
    }
}

/**
 * StatusFilterValidator
 * Validates order/payment status filter
 * Rules: Optional, must be valid status
 */
class StatusFilterValidator extends ReportValidator {
    constructor() {
        super();
        this.errorMessage = '';
        this.validStatuses = [
            'all',
            'pending',
            'processing',
            'completed',
            'cancelled',
            'failed',
            'refunded'
        ];
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
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
        return 'Status Filter';
    }
}

/**
 * MinValueValidator
 * Validates minimum value filter for revenue/amount
 * Rules: Optional, must be >= 0
 */
class MinValueValidator extends ReportValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
        }

        const numValue = parseFloat(value);

        if (isNaN(numValue)) {
            this.errorMessage = 'Minimum value must be a number';
            return false;
        }

        if (numValue < 0) {
            this.errorMessage = 'Minimum value cannot be negative';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Minimum Value';
    }
}

/**
 * LimitValidator
 * Validates limit for top-n reports
 * Rules: Optional, 1-1000
 */
class LimitValidator extends ReportValidator {
    constructor() {
        super();
        this.errorMessage = '';
    }

    validate(value) {
        // Optional field
        if (this.isEmpty(value)) {
            return true;
        }

        const numValue = parseInt(value, 10);

        if (isNaN(numValue)) {
            this.errorMessage = 'Limit must be a number';
            return false;
        }

        if (numValue < 1 || numValue > 1000) {
            this.errorMessage = 'Limit must be between 1 and 1000';
            return false;
        }

        return true;
    }

    getErrorMessage() {
        return this.errorMessage;
    }

    getFieldName() {
        return 'Limit';
    }
}

// Export validators
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        ReportValidator,
        DateRangeValidator,
        ReportTypeValidator,
        FormatValidator,
        CategoryFilterValidator,
        StatusFilterValidator,
        MinValueValidator,
        LimitValidator
    };
}
