const categoryManager = {
    init: function () {
        this.initializeEventListeners();
        this.setupRealTimeValidation();
    },

    initializeEventListeners: function () {
        const categoryImageInput = document.getElementById('categoryImage');
        if (categoryImageInput) {
            categoryImageInput.addEventListener('change', (e) => {
                this.handleImageUpload(e);
            });
        }

        /*
                const editCategoryForm = document.getElementById('editCategoryForm');
                if (editCategoryForm) {
                    editCategoryForm.addEventListener('submit', (e) => {
                        this.handleFormSubmission(e);
                    });
                }
        */

        this.setupRealTimeValidation();
    },

    handleImageUpload: function (e) {
        const file = e.target.files[0];
        if (!file) return;

        const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
        if (!validTypes.includes(file.type)) {
            adminHelpers.showNotification('Please upload a valid image file (JPG, PNG, or GIF)', 'error');
            return;
        }

        if (file.size > 2 * 1024 * 1024) {
            adminHelpers.showNotification('Image size must be less than 2MB', 'error');
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            const preview = document.getElementById('imagePreview');
            const currentImage = document.getElementById('currentCategoryImage');

            preview.src = e.target.result;
            preview.style.display = 'block';

            if (currentImage) {
                currentImage.style.display = 'none';
            }
        };
        reader.readAsDataURL(file);
    },

    handleFormSubmission: function (e) {
        e.preventDefault();

        if (!this.validateForm()) {
            adminHelpers.showNotification('Please fill in all required fields correctly', 'error');
            return;
        }

        this.submitForm();
    },

    validateForm: function () {
        const categoryName = document.getElementById('categoryName').value.trim();

        if (!categoryName) {
            this.highlightField('categoryName', false);
            return false;
        }

        this.highlightField('categoryName', true);
        return true;
    },

    highlightField: function (fieldId, isValid) {
        const field = document.getElementById(fieldId);
        if (!field) return;

        if (isValid) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
        }
    },

    setupRealTimeValidation: function () {
        const categoryName = document.getElementById('categoryName');
        if (categoryName) {
            categoryName.addEventListener('input', () => {
                this.validateField('categoryName');
            });
        }
    },

    validateField: function (fieldId) {
        const field = document.getElementById(fieldId);
        if (!field) return;

        const value = field.value.trim();
        let isValid = false;

        switch (fieldId) {
            case 'categoryName':
                isValid = value.length > 0 && value.length <= 100;
                break;
            default:
                isValid = true;
        }

        this.highlightField(fieldId, isValid);
    },

    submitForm: function () {
        const submitBtn = document.querySelector('.btn-update');
        adminHelpers.showLoading(submitBtn, 'Updating...');

        setTimeout(() => {
            adminHelpers.showNotification('Category updated successfully!', 'success');

            adminHelpers.hideLoading(submitBtn);

            setTimeout(() => {
                window.location.href = '/admin/categories';
            }, 1000);
        }, 1500);
    },

    confirmDelete: function () {
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        deleteModal.show();
    },

    deleteCategory: function () {
        const deleteBtn = document.querySelector('#deleteModal .btn-delete');
        adminHelpers.showLoading(deleteBtn, 'Deleting...');

        setTimeout(() => {
            adminHelpers.showNotification('Category deleted successfully!', 'success');

            const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
            deleteModal.hide();

            adminHelpers.hideLoading(deleteBtn);

            setTimeout(() => {
                window.location.href = '/admin/categories';
            }, 1000);
        }, 1500);
    }
};

document.addEventListener('DOMContentLoaded', function () {
    categoryManager.init();
});