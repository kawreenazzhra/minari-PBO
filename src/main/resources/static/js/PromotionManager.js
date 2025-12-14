/**
 * PromotionCategoryManager.js
 * ==========================
 * Handles promotion category and applicability management
 * 
 * Features:
 * - Select categories for promotion
 * - Set applicability (all products, specific categories, specific products)
 * - Manage product/category selections
 * - Generate form data
 */

class PromotionCategoryManager {
    constructor() {
        this.errorMessage = '';
        this.selectedCategories = [];
        this.selectedProducts = [];
        this.applicability = 'all-products'; // all-products, categories, products
    }

    /**
     * Initialize category manager with DOM elements
     * @param {HTMLElement} categorySelectElement - Category multi-select
     * @param {HTMLElement} productSelectElement - Product multi-select
     * @param {HTMLElement} applicabilityRadios - Applicability radio buttons
     */
    initialize(categorySelectElement, productSelectElement, applicabilityRadios) {
        this.categorySelect = categorySelectElement;
        this.productSelect = productSelectElement;
        this.applicabilityRadios = applicabilityRadios;

        this.setupEventListeners();
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        if (this.applicabilityRadios) {
            this.applicabilityRadios.forEach(radio => {
                radio.addEventListener('change', (e) => {
                    this.setApplicability(e.target.value);
                    this.updateSelectVisibility();
                });
            });
        }

        if (this.categorySelect) {
            this.categorySelect.addEventListener('change', () => {
                this.updateSelectedCategories();
            });
        }

        if (this.productSelect) {
            this.productSelect.addEventListener('change', () => {
                this.updateSelectedProducts();
            });
        }
    }

    /**
     * Set applicability type
     * @param {string} type - 'all-products', 'categories', or 'products'
     */
    setApplicability(type) {
        if (['all-products', 'categories', 'products'].includes(type)) {
            this.applicability = type;
        }
    }

    /**
     * Get applicability type
     * @returns {string}
     */
    getApplicability() {
        return this.applicability;
    }

    /**
     * Update selected categories from select element
     */
    updateSelectedCategories() {
        if (!this.categorySelect) return;

        const options = this.categorySelect.selectedOptions;
        this.selectedCategories = Array.from(options).map(option => ({
            id: option.value,
            name: option.textContent
        }));
    }

    /**
     * Update selected products from select element
     */
    updateSelectedProducts() {
        if (!this.productSelect) return;

        const options = this.productSelect.selectedOptions;
        this.selectedProducts = Array.from(options).map(option => ({
            id: option.value,
            name: option.textContent
        }));
    }

    /**
     * Update category select visibility
     */
    updateSelectVisibility() {
        if (this.categorySelect) {
            this.categorySelect.parentElement.style.display = 
                this.applicability === 'categories' ? 'block' : 'none';
        }

        if (this.productSelect) {
            this.productSelect.parentElement.style.display = 
                this.applicability === 'products' ? 'block' : 'none';
        }
    }

    /**
     * Validate applicability
     * @returns {boolean}
     */
    validate() {
        this.errorMessage = '';

        if (this.applicability === 'categories' && this.selectedCategories.length === 0) {
            this.errorMessage = 'At least one category must be selected';
            return false;
        }

        if (this.applicability === 'products' && this.selectedProducts.length === 0) {
            this.errorMessage = 'At least one product must be selected';
            return false;
        }

        return true;
    }

    /**
     * Get form data
     * @returns {Object}
     */
    getFormData() {
        return {
            applicability: this.applicability,
            categories: this.selectedCategories,
            products: this.selectedProducts
        };
    }

    /**
     * Get error message
     * @returns {string}
     */
    getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Get selected categories count
     * @returns {number}
     */
    getCategoryCount() {
        return this.selectedCategories.length;
    }

    /**
     * Get selected products count
     * @returns {number}
     */
    getProductCount() {
        return this.selectedProducts.length;
    }

    /**
     * Clear selections
     */
    clear() {
        this.selectedCategories = [];
        this.selectedProducts = [];
        this.applicability = 'all-products';

        if (this.categorySelect) {
            this.categorySelect.value = '';
        }

        if (this.productSelect) {
            this.productSelect.value = '';
        }

        this.updateSelectVisibility();
    }
}

/**
 * PromotionManager
 * ===============
 * Main orchestrator for Add Promotion form
 * 
 * Handles:
 * - Form initialization
 * - Field validation
 * - Form submission
 * - Error display
 * - Success handling
 */

class PromotionManager {
    constructor() {
        this.validators = {};
        this.categoryManager = new PromotionCategoryManager();
        this.cachedElements = {};
        this.isDirty = false;
    }

    /**
     * Initialize form on page load
     */
    async initialize() {
        try {
            this.cacheElements();
            this.initializeValidators();
            this.initializeCategoryManager();
            this.setupEventListeners();
            this.setupRealtimeValidation();
            console.log('✅ PromotionManager initialized');
        } catch (error) {
            console.error('❌ Error initializing PromotionManager:', error);
        }
    }

    /**
     * Cache DOM elements
     */
    cacheElements() {
        this.cachedElements = {
            form: document.getElementById('promotionForm'),
            promotionName: document.getElementById('promotionName'),
            promotionCode: document.getElementById('promotionCode'),
            promotionType: document.getElementById('promotionType'),
            discountValue: document.getElementById('discountValue'),
            discountLabel: document.getElementById('discountLabel'),
            startDate: document.getElementById('startDate'),
            endDate: document.getElementById('endDate'),
            minPurchase: document.getElementById('minPurchase'),
            maxUsage: document.getElementById('maxUsage'),
            description: document.getElementById('description'),
            descriptionCount: document.getElementById('descriptionCount'),
            categorySelect: document.getElementById('promotionCategories'),
            productSelect: document.getElementById('promotionProducts'),
            applicabilityRadios: document.querySelectorAll('input[name="applicability"]'),
            
            // Submit/Reset buttons
            submitBtn: document.querySelector('button[type="submit"]'),
            resetBtn: document.querySelector('button[type="reset"]'),
            
            // Status/Error containers
            statusMessage: document.getElementById('statusMessage'),
            errorContainers: {}
        };

        // Cache error containers
        const errorInputs = ['promotionName', 'promotionCode', 'promotionType', 
                            'discountValue', 'startDate', 'endDate'];
        errorInputs.forEach(field => {
            this.cachedElements.errorContainers[field] = 
                document.querySelector(`[data-error-for="${field}"]`);
        });
    }

    /**
     * Initialize all validators
     */
    initializeValidators() {
        this.validators = {
            promotionName: new PromotionNameValidator(),
            promotionCode: new PromotionCodeValidator(),
            promotionType: new PromotionTypeValidator(),
            discountValue: new PromotionDiscountValidator(),
            startDate: new PromotionStartDateValidator(),
            endDate: new PromotionEndDateValidator(),
            minPurchase: new PromotionMinPurchaseValidator(),
            maxUsage: new PromotionMaxUsageValidator(),
            description: new PromotionDescriptionValidator()
        };
    }

    /**
     * Initialize category manager
     */
    initializeCategoryManager() {
        this.categoryManager.initialize(
            this.cachedElements.categorySelect,
            this.cachedElements.productSelect,
            this.cachedElements.applicabilityRadios
        );
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        // Form submission
        if (this.cachedElements.form) {
            this.cachedElements.form.addEventListener('submit', 
                (e) => this.handleFormSubmit(e));
        }

        // Reset button
        if (this.cachedElements.resetBtn) {
            this.cachedElements.resetBtn.addEventListener('click', 
                () => this.resetForm());
        }

        // Promotion type change
        if (this.cachedElements.promotionType) {
            this.cachedElements.promotionType.addEventListener('change', 
                (e) => this.onPromotionTypeChange(e));
        }

        // Description character count
        if (this.cachedElements.description) {
            this.cachedElements.description.addEventListener('input', 
                (e) => this.updateDescriptionCount(e));
        }

        // Mark form as dirty
        const inputs = this.cachedElements.form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('change', () => {
                this.isDirty = true;
            });
        });
    }

    /**
     * Setup real-time validation on blur/change
     */
    setupRealtimeValidation() {
        const fields = ['promotionName', 'promotionCode', 'promotionType', 
                       'discountValue', 'startDate', 'endDate', 'minPurchase', 
                       'maxUsage', 'description'];

        fields.forEach(field => {
            const element = this.cachedElements[field];
            if (element) {
                const event = field === 'description' ? 'blur' : 'blur';
                element.addEventListener(event, () => {
                    this.validateField(field, element.value);
                });

                if (field === 'promotionType') {
                    element.addEventListener('change', () => {
                        this.validateField(field, element.value);
                    });
                }
            }
        });

        // Validate end date when start date changes
        if (this.cachedElements.startDate) {
            this.cachedElements.startDate.addEventListener('change', () => {
                if (this.cachedElements.endDate && this.cachedElements.endDate.value) {
                    this.validateField('endDate', this.cachedElements.endDate.value);
                }
            });
        }
    }

    /**
     * Handle promotion type change
     * Update discount label based on type
     */
    onPromotionTypeChange(event) {
        const type = event.target.value;
        const labels = {
            'percentage': 'Discount Percentage (%)',
            'fixed-amount': 'Discount Amount (Rp)',
            'buy-one-get-one': 'Get Discount (%)',
            'free-shipping': 'Min Purchase for Free Shipping (Rp)'
        };

        if (this.cachedElements.discountLabel) {
            this.cachedElements.discountLabel.textContent = labels[type] || 'Discount Value';
        }

        // Update discount input placeholder
        if (this.cachedElements.discountValue) {
            const placeholders = {
                'percentage': 'e.g., 25 (for 25%)',
                'fixed-amount': 'e.g., 50000',
                'buy-one-get-one': 'e.g., 50 (for 50%)',
                'free-shipping': 'e.g., 250000'
            };
            this.cachedElements.discountValue.placeholder = placeholders[type] || '';
        }

        // Clear discount value
        if (this.cachedElements.discountValue) {
            this.cachedElements.discountValue.value = '';
        }
    }

    /**
     * Update description character count
     */
    updateDescriptionCount(event) {
        const count = event.target.value.length;
        if (this.cachedElements.descriptionCount) {
            this.cachedElements.descriptionCount.textContent = `${count}/500`;
        }
    }

    /**
     * Validate single field
     * @param {string} fieldName - Field name
     * @param {*} value - Field value
     * @returns {boolean}
     */
    validateField(fieldName, value) {
        const validator = this.validators[fieldName];
        if (!validator) return true;

        let isValid = false;

        // Special validation for fields that depend on other fields
        if (fieldName === 'discountValue') {
            const promotionType = this.cachedElements.promotionType?.value;
            isValid = validator.validate(value, promotionType);
        } else if (fieldName === 'endDate') {
            const startDate = this.cachedElements.startDate?.value;
            isValid = validator.validate(value, startDate);
        } else {
            isValid = validator.validate(value);
        }

        // Show/hide error
        const errorContainer = this.cachedElements.errorContainers[fieldName];
        if (errorContainer) {
            if (!isValid) {
                errorContainer.textContent = validator.getErrorMessage();
                errorContainer.style.display = 'block';
                if (this.cachedElements[fieldName]) {
                    this.cachedElements[fieldName].classList.add('is-invalid');
                }
            } else {
                errorContainer.textContent = '';
                errorContainer.style.display = 'none';
                if (this.cachedElements[fieldName]) {
                    this.cachedElements[fieldName].classList.remove('is-invalid');
                }
            }
        }

        return isValid;
    }

    /**
     * Validate entire form
     * @returns {boolean}
     */
    validateForm() {
        let isValid = true;

        const fields = [
            'promotionName',
            'promotionCode',
            'promotionType',
            'discountValue',
            'startDate',
            'endDate',
            'minPurchase',
            'maxUsage',
            'description'
        ];

        fields.forEach(field => {
            const element = this.cachedElements[field];
            if (element) {
                if (!this.validateField(field, element.value)) {
                    isValid = false;
                }
            }
        });

        // Validate category manager
        if (!this.categoryManager.validate()) {
            console.error('❌', this.categoryManager.getErrorMessage());
            isValid = false;
        }

        return isValid;
    }

    /**
     * Get form data
     * @returns {Object}
     */
    getFormData() {
        const data = {
            promotionName: this.cachedElements.promotionName?.value || '',
            promotionCode: this.cachedElements.promotionCode?.value || '',
            promotionType: this.cachedElements.promotionType?.value || '',
            discountValue: parseFloat(this.cachedElements.discountValue?.value) || 0,
            startDate: this.cachedElements.startDate?.value || '',
            endDate: this.cachedElements.endDate?.value || '',
            minPurchase: this.cachedElements.minPurchase?.value ? 
                parseFloat(this.cachedElements.minPurchase.value) : null,
            maxUsage: this.cachedElements.maxUsage?.value ? 
                parseInt(this.cachedElements.maxUsage.value) : null,
            description: this.cachedElements.description?.value || '',
            ...this.categoryManager.getFormData()
        };

        return data;
    }

    /**
     * Handle form submission
     */
    async handleFormSubmit(event) {
        event.preventDefault();

        // Validate form
        if (!this.validateForm()) {
            this.showStatus('❌ Please fix validation errors', 'error');
            return;
        }

        // Get form data
        const data = this.getFormData();

        // Show loading state
        this.setSubmitButtonLoading(true);

        try {
            await this.submitPromotionToBackend(data);
        } catch (error) {
            console.error('Error submitting form:', error);
            this.showStatus('❌ Error creating promotion', 'error');
        } finally {
            this.setSubmitButtonLoading(false);
        }
    }

    /**
     * Submit to backend API
     */
    async submitPromotionToBackend(data) {
        // For now, just log the data
        console.log('Promotion data to submit:', data);

        // Mock API call
        return new Promise((resolve) => {
            setTimeout(() => {
                this.showStatus('✅ Promotion created successfully!', 'success');
                this.resetForm();
                resolve();
            }, 1500);
        });

        // Real implementation (when backend is ready):
        /*
        const response = await fetch('/api/promotions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        this.showStatus('✅ Promotion created successfully!', 'success');
        this.resetForm();
        */
    }

    /**
     * Set submit button loading state
     */
    setSubmitButtonLoading(isLoading) {
        if (this.cachedElements.submitBtn) {
            if (isLoading) {
                this.cachedElements.submitBtn.disabled = true;
                this.cachedElements.submitBtn.innerHTML = 
                    '<span class="spinner-border spinner-border-sm me-2"></span>Loading...';
            } else {
                this.cachedElements.submitBtn.disabled = false;
                this.cachedElements.submitBtn.innerHTML = 'Create Promotion';
            }
        }
    }

    /**
     * Show status message
     */
    showStatus(message, type = 'info') {
        if (!this.cachedElements.statusMessage) return;

        this.cachedElements.statusMessage.textContent = message;
        this.cachedElements.statusMessage.className = `alert alert-${type === 'error' ? 'danger' : type}`;
        this.cachedElements.statusMessage.style.display = 'block';

        // Auto-hide success message after 5 seconds
        if (type === 'success') {
            setTimeout(() => {
                this.cachedElements.statusMessage.style.display = 'none';
            }, 5000);
        }
    }

    /**
     * Reset form
     */
    resetForm() {
        if (this.cachedElements.form) {
            this.cachedElements.form.reset();
        }

        this.categoryManager.clear();

        // Clear errors
        Object.values(this.cachedElements.errorContainers).forEach(container => {
            if (container) {
                container.textContent = '';
                container.style.display = 'none';
            }
        });

        // Remove error class
        const inputs = this.cachedElements.form?.querySelectorAll('.is-invalid');
        inputs?.forEach(input => input.classList.remove('is-invalid'));

        // Reset status
        if (this.cachedElements.statusMessage) {
            this.cachedElements.statusMessage.style.display = 'none';
        }

        // Reset dirty flag
        this.isDirty = false;

        // Reset description count
        if (this.cachedElements.descriptionCount) {
            this.cachedElements.descriptionCount.textContent = '0/500';
        }

        console.log('Form reset');
    }

    /**
     * Check if form is dirty
     */
    isFormDirty() {
        return this.isDirty;
    }

    /**
     * Get validation status
     */
    getValidationStatus() {
        const fields = ['promotionName', 'promotionCode', 'promotionType', 
                       'discountValue', 'startDate', 'endDate'];
        const status = {};

        fields.forEach(field => {
            const element = this.cachedElements[field];
            if (element) {
                status[field] = this.validateField(field, element.value);
            }
        });

        return status;
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    window.promotionManager = new PromotionManager();
    window.promotionManager.initialize();
});

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { PromotionCategoryManager, PromotionManager };
}
