/**
 * CategoryManager Class (Main Class)
 * Mengelola keseluruhan logika Add Category dengan OOP
 * Menggunakan composition dan delegation pattern
 */
class CategoryManager {
    constructor() {
        this.uiManager = new UIManager();
        this.formValidator = new FormValidator(this.uiManager);
        this.sidebarManager = new SidebarManager();

        this.initialize();
    }

    /**
     * Inisialisasi CategoryManager
     */
    initialize() {
        this.setupEventListeners();
        this.setupRealtimeValidation();
        console.log('CategoryManager initialized successfully');
    }

    /**
     * Setup event listeners untuk form dan UI elements
     */
    setupEventListeners() {
        // Form submission
        const form = this.uiManager.elements.addCategoryForm;
        if (form) {
            form.addEventListener('submit', (e) => this.handleFormSubmission(e));
        }

        // Image upload
        const imageInput = this.uiManager.elements.categoryImage;
        if (imageInput) {
            imageInput.addEventListener('change', (e) => this.handleImageUpload(e));
        }

        // Cancel button
        const cancelBtn = this.uiManager.elements.cancelBtn;
        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => this.handleCancel());
        }

        // Prevent unsaved changes
        window.addEventListener('beforeunload', (e) => this.handleBeforeUnload(e));
    }

    /**
     * Setup real-time validation
     */
    setupRealtimeValidation() {
        this.formValidator.setupRealtimeValidation('categoryName');
        this.formValidator.setupRealtimeValidation('categoryDescription');
    }

    /**
     * Handle image upload
     * @param {Event} e - Change event
     */
    async handleImageUpload(e) {
        const file = e.target.files[0];

        // Validasi image
        const validationResult = this.formValidator.validateField('categoryImage', file);

        if (!validationResult.isValid) {
            e.target.value = '';
            return;
        }

        // Show preview
        try {
            await this.uiManager.showImagePreview(file);
            this.uiManager.hideValidationMessage('imageError');
        } catch (error) {
            console.error('Error showing image preview:', error);
        }
    }

    /**
     * Handle form submission
     * @param {Event} e - Submit event
     */
    handleFormSubmission(e) {
        e.preventDefault();

        // Validasi form
        if (!this.formValidator.validateForm()) {
            this.uiManager.showNotification('Please fill in all required fields correctly', 'error');
            return;
        }

        // Submit form
        this.submitForm();
    }

    /**
     * Submit form ke server
     */
    submitForm() {
        const formData = this.uiManager.getFormData();
        const originalButtonText = this.uiManager.elements.submitBtn.innerHTML;

        // Set loading state
        this.uiManager.setSubmitButtonLoading(true, originalButtonText);

        // Create FormData for multipart/form-data (for file upload)
        const form = document.getElementById('addCategoryForm');
        const formDataObj = new FormData(form);
        
        // Clear default values
        formDataObj.delete('status');
        
        // Add fields with correct names matching ProductCategory entity
        formDataObj.set('name', document.getElementById('categoryName').value);
        formDataObj.set('description', document.getElementById('categoryDescription').value);
        formDataObj.set('isActive', document.getElementById('activeStatus').checked ? 'true' : 'false');
        
        // Get the file if uploaded
        const fileInput = document.getElementById('categoryImage');
        if (fileInput.files.length > 0) {
            formDataObj.set('imageUrl', fileInput.files[0]);
        }

        // Send to backend
        fetch('/admin/categories', {
            method: 'POST',
            body: formDataObj
        })
        .then(response => {
            if (response.ok || response.status === 302 || response.status === 301) {
                this.uiManager.showNotification('Category added successfully!', 'success');
                this.uiManager.resetForm();
                
                // Redirect after delay
                setTimeout(() => {
                    window.location.href = '/admin/categories';
                }, 1000);
            } else {
                return response.text().then(text => {
                    throw new Error('Server returned status ' + response.status + ': ' + text);
                });
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
            this.uiManager.showNotification('Failed to add category: ' + error.message, 'error');
            this.uiManager.setSubmitButtonLoading(false, originalButtonText);
        });
    }

    /**
     * Handle cancel button
     */
    handleCancel() {
        if (this.uiManager.isFormDirty()) {
            if (!confirm('You have unsaved changes. Are you sure you want to cancel?')) {
                return;
            }
        }
        window.location.href = '/admin/categories';
    }

    /**
     * Handle before unload untuk warn unsaved changes
     * @param {Event} e - BeforeUnload event
     */
    handleBeforeUnload(e) {
        if (this.uiManager.isFormDirty()) {
            e.preventDefault();
            e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
        }
    }
}

/**
 * SidebarManager Class
 * Mengelola sidebar navigation
 */
class SidebarManager {
    constructor() {
        this.initializeSidebar();
    }

    /**
     * Inisialisasi sidebar dengan menandai active item
     */
    initializeSidebar() {
        const currentPage = window.location.pathname.split('/').pop();
        const sidebarItems = document.querySelectorAll('.sidebar-item');

        sidebarItems.forEach(item => {
            item.classList.remove('active');
        });

        const categoriesItem = document.querySelector('.sidebar-item[href="categoriesadmin.html"]');
        if (categoriesItem) {
            categoriesItem.classList.add('active');
        }
    }

    /**
     * Highlight menu item berdasarkan current page
     * @param {string} href - Href element
     */
    setActiveMenuItem(href) {
        const sidebarItems = document.querySelectorAll('.sidebar-item');
        sidebarItems.forEach(item => {
            item.classList.remove('active');
        });

        const activeItem = document.querySelector(`.sidebar-item[href="${href}"]`);
        if (activeItem) {
            activeItem.classList.add('active');
        }
    }
}

// Initialize CategoryManager when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    window.categoryManager = new CategoryManager();
});

// Export untuk testing atau module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { CategoryManager, SidebarManager, FormValidator, UIManager };
}
