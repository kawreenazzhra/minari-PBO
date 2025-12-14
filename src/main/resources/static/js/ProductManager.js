/**
 * Product Manager - Main Orchestrator for Add Product Page
 * 
 * Coordinates:
 * - Form validation (ProductValidator classes)
 * - Image upload management (ProductImageUploadManager)
 * - Form submission and API calls
 * - Error handling and notifications
 * 
 * Demonstrates: Orchestrator pattern, Composition, Delegation
 */

class ProductManager {
    constructor() {
        this.formData = {};
        this.validators = {};
        this.imageManager = null;
        
        this.initialize();
    }

    /**
     * Initialize the product form manager
     * @private
     */
    initialize() {
        this.cacheElements();
        this.initializeValidators();
        this.initializeImageUpload();
        this.setupEventListeners();
        this.setupRealtimeValidation();
        
        console.log('✅ ProductManager initialized');
    }

    /**
     * Cache DOM elements
     * @private
     */
    cacheElements() {
        // Form elements
        this.form = document.getElementById('addProductForm');
        this.submitBtn = document.querySelector('.btn-submit');
        
        // Product fields
        this.productNameInput = document.getElementById('productName');
        this.skuInput = document.getElementById('sku');
        this.categoryInput = document.getElementById('category');
        this.descriptionInput = document.getElementById('description');
        this.priceInput = document.getElementById('price');
        this.stockInput = document.getElementById('stock');
        this.weightInput = document.getElementById('weight');
        this.discountInput = document.getElementById('discount');
        
        // Error display areas
        this.errorContainers = {};
        document.querySelectorAll('[data-error-for]').forEach(el => {
            const fieldName = el.getAttribute('data-error-for');
            this.errorContainers[fieldName] = el;
        });

        // Form status message
        this.statusMessage = document.querySelector('.form-status-message');
    }

    /**
     * Initialize validators
     * @private
     */
    initializeValidators() {
        this.validators = {
            productName: new ProductNameValidator(),
            sku: new SKUValidator(),
            category: new CategoryValidator(),
            description: new DescriptionValidator(),
            price: new PriceValidator(),
            stock: new StockValidator(),
            weight: new WeightValidator(),
            discount: new DiscountValidator(),
            images: new ProductImageValidator()
        };
    }

    /**
     * Initialize image upload manager
     * @private
     */
    initializeImageUpload() {
        const imageUploadContainer = document.getElementById('imageUploadContainer');
        if (imageUploadContainer) {
            this.imageManager = new ProductImageUploadManager('imageUploadContainer', {
                maxImages: 5,
                maxFileSize: 5 * 1024 * 1024,
                allowedTypes: ['image/jpeg', 'image/png', 'image/gif']
            });
        }
    }

    /**
     * Setup form event listeners
     * @private
     */
    setupEventListeners() {
        // Form submission
        if (this.form) {
            this.form.addEventListener('submit', (e) => this.handleFormSubmit(e));
        }

        // Reset button
        const resetBtn = this.form?.querySelector('[type="reset"]');
        if (resetBtn) {
            resetBtn.addEventListener('click', () => this.resetForm());
        }
    }

    /**
     * Setup real-time validation as user types
     * @private
     */
    setupRealtimeValidation() {
        // Product name validation
        if (this.productNameInput) {
            this.productNameInput.addEventListener('blur', () => {
                this.validateField('productName', this.productNameInput.value);
            });
            this.productNameInput.addEventListener('input', () => {
                if (this.errorContainers['productName']?.textContent) {
                    this.validateField('productName', this.productNameInput.value);
                }
            });
        }

        // SKU validation
        if (this.skuInput) {
            this.skuInput.addEventListener('blur', () => {
                this.validateField('sku', this.skuInput.value);
            });
        }

        // Price validation
        if (this.priceInput) {
            this.priceInput.addEventListener('blur', () => {
                this.validateField('price', this.priceInput.value);
            });
        }

        // Stock validation
        if (this.stockInput) {
            this.stockInput.addEventListener('blur', () => {
                this.validateField('stock', this.stockInput.value);
            });
        }

        // Description validation
        if (this.descriptionInput) {
            this.descriptionInput.addEventListener('blur', () => {
                this.validateField('description', this.descriptionInput.value);
            });
        }

        // Weight validation
        if (this.weightInput) {
            this.weightInput.addEventListener('blur', () => {
                this.validateField('weight', this.weightInput.value);
            });
        }

        // Discount validation
        if (this.discountInput) {
            this.discountInput.addEventListener('blur', () => {
                this.validateField('discount', this.discountInput.value);
            });
        }

        // Category validation
        if (this.categoryInput) {
            this.categoryInput.addEventListener('change', () => {
                this.validateField('category', this.categoryInput.value);
            });
        }
    }

    /**
     * Validate single field
     * @param {string} fieldName - Name of field to validate
     * @param {*} value - Value to validate
     * @returns {boolean} - True if valid
     */
    validateField(fieldName, value) {
        const validator = this.validators[fieldName];
        if (!validator) {
            return true;
        }

        const isValid = validator.validate(value);
        const errorContainer = this.errorContainers[fieldName];

        if (isValid) {
            // Valid - clear error
            if (errorContainer) {
                errorContainer.textContent = '';
                errorContainer.classList.remove('visible');
            }
            // Remove error class from input
            const input = this.form?.querySelector(`[id="${fieldName}"]`);
            if (input) {
                input.classList.remove('input-error');
            }
        } else {
            // Invalid - show error
            if (errorContainer) {
                errorContainer.textContent = validator.getErrorMessage();
                errorContainer.classList.add('visible');
            }
            // Add error class to input
            const input = this.form?.querySelector(`[id="${fieldName}"]`);
            if (input) {
                input.classList.add('input-error');
            }
        }

        return isValid;
    }

    /**
     * Get form data
     * @returns {Object} Form data object
     */
    getFormData() {
        return {
            productName: this.productNameInput?.value || '',
            sku: this.skuInput?.value || '',
            category: this.categoryInput?.value || '',
            description: this.descriptionInput?.value || '',
            price: parseFloat(this.priceInput?.value || 0),
            stock: parseInt(this.stockInput?.value || 0),
            weight: this.weightInput?.value ? parseFloat(this.weightInput.value) : null,
            discount: this.discountInput?.value ? parseFloat(this.discountInput.value) : 0,
            images: this.imageManager?.getImages() || []
        };
    }

    /**
     * Validate entire form
     * @returns {boolean} - True if all fields valid
     */
    validateForm() {
        let isFormValid = true;

        // Validate all fields
        const fieldsToValidate = [
            { field: 'productName', input: this.productNameInput },
            { field: 'sku', input: this.skuInput },
            { field: 'category', input: this.categoryInput },
            { field: 'description', input: this.descriptionInput },
            { field: 'price', input: this.priceInput },
            { field: 'stock', input: this.stockInput },
            { field: 'weight', input: this.weightInput },
            { field: 'discount', input: this.discountInput }
        ];

        fieldsToValidate.forEach(({ field, input }) => {
            if (input) {
                const isValid = this.validateField(field, input.value);
                if (!isValid) {
                    isFormValid = false;
                }
            }
        });

        // Validate images
        if (!this.imageManager?.validate()) {
            isFormValid = false;
        }

        return isFormValid;
    }

    /**
     * Handle form submission
     * @private
     */
    async handleFormSubmit(event) {
        event.preventDefault();

        // Disable submit button
        this.setSubmitButtonLoading(true);
        this.clearStatusMessage();

        try {
            // Validate form
            if (!this.validateForm()) {
                this.showMessage('Please fix validation errors', 'error');
                this.setSubmitButtonLoading(false);
                return;
            }

            // Get form data
            const formData = this.getFormData();

            // In real scenario, submit to backend API
            // For now, simulate success
            console.log('Product data to submit:', formData);

            // Simulate API call
            await this.submitProductToBackend(formData);

            // Success message
            this.showMessage('✅ Product added successfully!', 'success');

            // Reset form
            setTimeout(() => {
                this.resetForm();
                // Redirect to products page (in real app)
                // window.location.href = '/admin/products';
            }, 2000);

        } catch (error) {
            console.error('Form submission error:', error);
            this.showMessage(`Error: ${error.message}`, 'error');
        } finally {
            this.setSubmitButtonLoading(false);
        }
    }

    /**
     * Submit product to backend (mock implementation)
     * @private
     */
    async submitProductToBackend(formData) {
        // In production, this would send to actual API
        return new Promise((resolve) => {
            setTimeout(() => {
                console.log('✅ Product submitted to backend:', formData);
                resolve();
            }, 1500);
        });

        // Real implementation would be:
        /*
        const response = await fetch('/api/products', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        if (!response.ok) {
            throw new Error('Failed to create product');
        }
        
        return response.json();
        */
    }

    /**
     * Set submit button loading state
     * @private
     */
    setSubmitButtonLoading(isLoading) {
        if (this.submitBtn) {
            if (isLoading) {
                this.submitBtn.disabled = true;
                this.submitBtn.innerHTML = '<span class="spinner"></span> Adding product...';
                this.submitBtn.classList.add('loading');
            } else {
                this.submitBtn.disabled = false;
                this.submitBtn.innerHTML = 'Add Product';
                this.submitBtn.classList.remove('loading');
            }
        }
    }

    /**
     * Show status message
     * @private
     */
    showMessage(message, type = 'info') {
        if (this.statusMessage) {
            this.statusMessage.textContent = message;
            this.statusMessage.className = `form-status-message status-${type} visible`;
        }
    }

    /**
     * Clear status message
     * @private
     */
    clearStatusMessage() {
        if (this.statusMessage) {
            this.statusMessage.textContent = '';
            this.statusMessage.classList.remove('visible');
        }
    }

    /**
     * Reset form to initial state
     */
    resetForm() {
        if (this.form) {
            this.form.reset();
        }

        // Clear all error messages
        Object.values(this.errorContainers).forEach(container => {
            container.textContent = '';
            container.classList.remove('visible');
        });

        // Clear error class from inputs
        this.form?.querySelectorAll('.input-error').forEach(input => {
            input.classList.remove('input-error');
        });

        // Clear images
        if (this.imageManager) {
            this.imageManager.clearImages();
        }

        this.clearStatusMessage();
    }

    /**
     * Check if form has been modified
     * @returns {boolean}
     */
    isFormDirty() {
        const data = this.getFormData();
        return Object.values(data).some(value => {
            if (Array.isArray(value)) return value.length > 0;
            if (typeof value === 'string') return value.trim() !== '';
            return value !== null && value !== 0 && value !== false;
        });
    }

    /**
     * Get form validation status
     * @returns {Object} Validation status for each field
     */
    getValidationStatus() {
        const status = {};
        
        Object.entries(this.validators).forEach(([fieldName, validator]) => {
            const input = this.form?.querySelector(`[id="${fieldName}"]`);
            if (input) {
                const isValid = validator.validate(input.value);
                status[fieldName] = {
                    isValid,
                    errorMessage: validator.getErrorMessage()
                };
            }
        });

        return status;
    }
}

/**
 * Sidebar Manager for Product Admin Navigation
 * Handle sidebar menu and navigation
 */
class SidebarManager {
    constructor() {
        this.initialize();
    }

    initialize() {
        this.cacheElements();
        this.setupEventListeners();
        this.setActiveMenu();
    }

    cacheElements() {
        this.sidebar = document.querySelector('.admin-sidebar');
        this.menuItems = document.querySelectorAll('.menu-item');
        this.toggleBtn = document.querySelector('.sidebar-toggle');
    }

    setupEventListeners() {
        // Menu item click
        this.menuItems.forEach(item => {
            item.addEventListener('click', (e) => {
                // Remove active from all items
                this.menuItems.forEach(i => i.classList.remove('active'));
                // Add active to clicked item
                item.classList.add('active');
                
                // Navigate to page (in real app)
                const href = item.getAttribute('href');
                if (href && !href.startsWith('#')) {
                    // window.location.href = href;
                }
            });
        });

        // Toggle sidebar on mobile
        if (this.toggleBtn) {
            this.toggleBtn.addEventListener('click', () => {
                this.sidebar?.classList.toggle('collapsed');
            });
        }
    }

    setActiveMenu() {
        // Set active menu based on current page
        const currentPage = window.location.pathname;
        
        this.menuItems.forEach(item => {
            const href = item.getAttribute('href');
            if (href === currentPage) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });
    }

    /**
     * Navigate to page
     */
    navigate(path) {
        window.location.href = path;
    }
}

// Auto-initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.productManager = new ProductManager();
    window.sidebarManager = new SidebarManager();
    console.log('✅ Product form managers initialized');
});
