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
            imageInput.addEventListener('change', (e) => this.handleImageUpload(e, 'imagePreview', 'imageError'));
        }

        // Banner upload
        const bannerInput = document.getElementById('categoryBanner');
        if (bannerInput) {
            bannerInput.addEventListener('change', (e) => this.handleImageUpload(e, 'bannerPreview', null));
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
     * @param {string} previewId - ID of the preview img element
     * @param {string} errorId - ID of the error message element (optional)
     */
    async handleImageUpload(e, previewId = 'imagePreview', errorId = 'imageError') {
        const file = e.target.files[0];

        // Basic validation for generic files if not using formValidator field
        if (errorId && this.formValidator.validateField) {
            // Validate using existing validator if errorId provided (assuming it matches field name logic)
            // For simplicity, we just check existence for now if it is the main image
            if (e.target.id === 'categoryImage') {
                const validationResult = this.formValidator.validateField('categoryImage', file);
                if (!validationResult.isValid) {
                    e.target.value = '';
                    return;
                }
            }
        }

        if (file) {
            // Show preview
            try {
                // Find preview element manually since uiManager might not map it dynamically
                const previewEl = document.getElementById(previewId);
                if (previewEl) {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        previewEl.src = e.target.result;
                        previewEl.style.display = 'block'; // Ensure it's visible if hidden
                    };
                    reader.readAsDataURL(file);
                }

                if (errorId && this.uiManager.hideValidationMessage) {
                    this.uiManager.hideValidationMessage(errorId);
                }
            } catch (error) {
                console.error('Error showing image preview:', error);
            }
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
        if (fileInput && fileInput.files.length > 0) {
            formDataObj.set('image', fileInput.files[0]); // Key must vary 'image' to match Controller
        }

        // Get the banner if uploaded
        const bannerInput = document.getElementById('categoryBanner');
        if (bannerInput && bannerInput.files.length > 0) {
            formDataObj.set('bannerImage', bannerInput.files[0]); // Key 'bannerImage'
        }

        // Send to backend
        fetch('/admin/categories', {
            method: 'POST',
            body: formDataObj
        })
            .then(response => {
                if (response.ok || response.status === 302 || response.status === 301) {
                    // Manually handle redirect if it's a redirect response type or success
                    // Since fetch follows redirects automatically usually, check if final url is different or just assume success
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
document.addEventListener('DOMContentLoaded', function () {
    window.categoryManager = new CategoryManager();
});

// Export untuk testing atau module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { CategoryManager, SidebarManager, FormValidator, UIManager };
}
