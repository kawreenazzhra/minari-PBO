/**
 * UIManager Class
 * Mengelola semua interaksi UI dan DOM manipulation
 */
class UIManager {
    constructor() {
        this.elements = {};
        this.cacheElements();
    }

    /**
     * Cache semua DOM elements yang diperlukan
     */
    cacheElements() {
        this.elements = {
            categoryName: document.getElementById('categoryName'),
            categoryDescription: document.getElementById('categoryDescription'),
            categoryImage: document.getElementById('categoryImage'),
            imagePreview: document.getElementById('imagePreview'),
            addCategoryForm: document.getElementById('addCategoryForm'),
            submitBtn: document.querySelector('.btn-add'),
            cancelBtn: document.querySelector('.btn-cancel'),
            activeStatus: document.getElementById('activeStatus'),
            inactiveStatus: document.getElementById('inactiveStatus'),
            // Error elements
            nameError: document.getElementById('nameError'),
            descriptionError: document.getElementById('descriptionError'),
            imageError: document.getElementById('imageError')
        };
    }

    /**
     * Menampilkan validation message
     * @param {string} elementId - ID element untuk error message
     * @param {string} message - Pesan yang akan ditampilkan
     * @param {string} type - Tipe message (error, success, warning)
     */
    showValidationMessage(elementId, message, type = 'error') {
        const element = document.getElementById(elementId);
        if (!element) return;

        element.textContent = message;
        element.className = `validation-message ${type}`;
        element.style.display = 'block';
    }

    /**
     * Menyembunyikan validation message
     * @param {string} elementId - ID element untuk error message
     */
    hideValidationMessage(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = 'none';
        }
    }

    /**
     * Highlight field berdasarkan validitas
     * @param {string} fieldId - ID field
     * @param {boolean} isValid - Status validitas
     */
    highlightField(fieldId, isValid) {
        const field = document.getElementById(fieldId);
        if (!field) return;

        if (isValid) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
        }
    }

    /**
     * Show image preview
     * @param {File} file - File image
     */
    showImagePreview(file) {
        return new Promise((resolve) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                this.elements.imagePreview.src = e.target.result;
                this.elements.imagePreview.classList.add('show');
                resolve();
            };
            reader.readAsDataURL(file);
        });
    }

    /**
     * Menampilkan notification
     * @param {string} message - Pesan notification
     * @param {string} type - Tipe notification (success, error, warning)
     */
    showNotification(message, type = 'info') {
        const existingNotifications = document.querySelectorAll('.custom-notification');
        existingNotifications.forEach(notification => notification.remove());

        const notification = document.createElement('div');
        const alertType = type === 'success' ? 'alert-success' : (type === 'error' ? 'alert-danger' : 'alert-warning');
        notification.className = `custom-notification alert ${alertType} alert-dismissible fade show`;

        const icons = {
            success: 'fa-check-circle',
            error: 'fa-exclamation-circle',
            warning: 'fa-exclamation-triangle'
        };

        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="fas ${icons[type] || 'fa-info-circle'} me-2"></i>
                <span>${message}</span>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 4000);
    }

    /**
     * Reset form ke kondisi awal
     */
    resetForm() {
        this.elements.addCategoryForm.reset();
        this.elements.imagePreview.classList.remove('show');

        document.querySelectorAll('.is-valid, .is-invalid').forEach(el => {
            el.classList.remove('is-valid', 'is-invalid');
        });

        document.querySelectorAll('.validation-message').forEach(msg => {
            msg.style.display = 'none';
        });
    }

    /**
     * Set submit button loading state
     * @param {boolean} isLoading - Status loading
     * @param {string} originalText - Text asli button
     */
    setSubmitButtonLoading(isLoading, originalText = '<i class="fas fa-plus"></i> Add Category') {
        const btn = this.elements.submitBtn;
        if (isLoading) {
            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Adding...';
            btn.disabled = true;
        } else {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    }

    /**
     * Get form data
     * @returns {Object} Form data object
     */
    getFormData() {
        return {
            name: this.elements.categoryName.value.trim(),
            description: this.elements.categoryDescription.value.trim(),
            status: document.querySelector('input[name="status"]:checked').value,
            image: this.elements.categoryImage.files[0]
        };
    }

    /**
     * Check apakah form sudah berubah
     * @returns {boolean}
     */
    isFormDirty() {
        const formData = this.getFormData();
        return formData.name !== '' || formData.description !== '' || formData.image !== undefined;
    }
}
