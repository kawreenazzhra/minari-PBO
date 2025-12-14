/**
 * ValidationRule Class
 * Base class untuk validation rules yang dapat di-extend
 */
class ValidationRule {
    constructor(fieldName, errorElementId) {
        this.fieldName = fieldName;
        this.errorElementId = errorElementId;
    }

    /**
     * Validasi field
     * @abstract
     * @param {string} value - Nilai yang akan divalidasi
     * @returns {Object} - {isValid: boolean, message: string, type: string}
     */
    validate(value) {
        throw new Error('validate() must be implemented');
    }
}

/**
 * CategoryNameValidator
 * Validasi untuk category name field
 */
class CategoryNameValidator extends ValidationRule {
    constructor(errorElementId = 'nameError') {
        super('categoryName', errorElementId);
        this.maxLength = 100;
        this.minLength = 1;
    }

    validate(value) {
        const trimmedValue = value.trim();

        if (!trimmedValue) {
            return {
                isValid: false,
                message: 'Category name is required',
                type: 'error'
            };
        }

        if (trimmedValue.length > this.maxLength) {
            return {
                isValid: false,
                message: `Category name must be less than ${this.maxLength} characters`,
                type: 'error'
            };
        }

        return {
            isValid: true,
            message: 'Category name looks good!',
            type: 'success'
        };
    }
}

/**
 * CategoryDescriptionValidator
 * Validasi untuk description field
 */
class CategoryDescriptionValidator extends ValidationRule {
    constructor(errorElementId = 'descriptionError') {
        super('categoryDescription', errorElementId);
        this.maxLength = 500;
    }

    validate(value) {
        const trimmedValue = value.trim();

        if (trimmedValue.length > this.maxLength) {
            return {
                isValid: false,
                message: `Description must be less than ${this.maxLength} characters`,
                type: 'warning'
            };
        }

        return {
            isValid: true,
            message: '',
            type: 'success'
        };
    }
}

/**
 * ImageValidator
 * Validasi untuk image upload
 */
class ImageValidator extends ValidationRule {
    constructor(errorElementId = 'imageError') {
        super('categoryImage', errorElementId);
        this.validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
        this.maxSize = 2 * 1024 * 1024; // 2MB
    }

    validate(file) {
        if (!file) {
            return {
                isValid: false,
                message: 'Category image is required',
                type: 'error'
            };
        }

        if (!this.validTypes.includes(file.type)) {
            return {
                isValid: false,
                message: 'Please upload a valid image file (JPG, PNG, or GIF)',
                type: 'error'
            };
        }

        if (file.size > this.maxSize) {
            return {
                isValid: false,
                message: 'Image size must be less than 2MB',
                type: 'error'
            };
        }

        return {
            isValid: true,
            message: 'Image uploaded successfully!',
            type: 'success'
        };
    }
}
