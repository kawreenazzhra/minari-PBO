/**
 * FormValidator Class
 * Mengelola validasi form dengan menggunakan validation rules
 */
class FormValidator {
    constructor(uiManager) {
        this.uiManager = uiManager;
        this.validators = {};
        this.initializeValidators();
    }

    /**
     * Inisialisasi semua validators
     */
    initializeValidators() {
        this.validators = {
            categoryName: new CategoryNameValidator('nameError'),
            categoryDescription: new CategoryDescriptionValidator('descriptionError'),
            categoryImage: new ImageValidator('imageError')
        };
    }

    /**
     * Validasi single field
     * @param {string} fieldName - Nama field
     * @param {*} value - Nilai field
     * @returns {Object} Hasil validasi
     */
    validateField(fieldName, value) {
        const validator = this.validators[fieldName];
        if (!validator) {
            console.warn(`Validator untuk ${fieldName} tidak ditemukan`);
            return { isValid: true };
        }

        const result = validator.validate(value);

        // Update UI berdasarkan hasil validasi
        if (result.message) {
            this.uiManager.showValidationMessage(
                validator.errorElementId,
                result.message,
                result.type
            );
        } else {
            this.uiManager.hideValidationMessage(validator.errorElementId);
        }

        // Highlight field jika error
        if (fieldName !== 'categoryImage') {
            this.uiManager.highlightField(fieldName, result.isValid);
        }

        return result;
    }

    /**
     * Validasi seluruh form
     * @returns {boolean} True jika form valid
     */
    validateForm() {
        const formData = this.uiManager.getFormData();
        let isValid = true;

        // Validasi category name
        const nameValidation = this.validateField('categoryName', formData.name);
        if (!nameValidation.isValid) {
            isValid = false;
        }

        // Validasi category description
        const descValidation = this.validateField('categoryDescription', formData.description);
        if (!descValidation.isValid) {
            isValid = false;
        }

        // Validasi category image
        const imageValidation = this.validateField('categoryImage', formData.image);
        if (!imageValidation.isValid) {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Validasi real-time saat user mengetik
     * @param {string} fieldName - Nama field
     */
    setupRealtimeValidation(fieldName) {
        const element = document.getElementById(fieldName);
        if (!element) return;

        element.addEventListener('input', () => {
            const value = element.value;
            this.validateField(fieldName, value);
        });

        element.addEventListener('blur', () => {
            const value = element.value;
            this.validateField(fieldName, value);
        });
    }
}
