isSubmitting: false,

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

    const addCategoryForm = document.getElementById('addCategoryForm');
    if (addCategoryForm) {
        addCategoryForm.addEventListener('submit', (e) => {
            this.isSubmitting = true;
            // Form submits normally
        });
    }

    this.setupRealTimeValidation();

    const cancelBtn = document.querySelector('.btn-cancel');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', (e) => {
            this.cancelForm();
        });
    }
},

// ... (rest of methods)

window.addEventListener('beforeunload', function (e) {
    if (!categoryManager.isSubmitting && categoryManager.isFormDirty()) {
        e.preventDefault();
        e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
    }
});

handleImageUpload: function (e) {
    const file = e.target.files[0];
    const errorDiv = document.getElementById('imageError');

    if (!file) {
        this.showValidationMessage('imageError', 'Please upload a category image', 'error');
        return;
    }

    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    if (!validTypes.includes(file.type)) {
        this.showValidationMessage('imageError', 'Please upload a valid image file (JPG, PNG, or GIF)', 'error');
        e.target.value = '';
        return;
    }

    if (file.size > 2 * 1024 * 1024) {
        this.showValidationMessage('imageError', 'Image size must be less than 2MB', 'error');
        e.target.value = '';
        return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
        const preview = document.getElementById('imagePreview');
        preview.src = e.target.result;
        preview.style.display = 'block';

        this.hideValidationMessage('imageError');

        this.showValidationMessage('imageError', 'Image uploaded successfully!', 'success');
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
    let isValid = true;

    const categoryName = document.getElementById('categoryName').value.trim();
    if (!categoryName) {
        this.showValidationMessage('nameError', 'Category name is required', 'error');
        this.highlightField('categoryName', false);
        isValid = false;
    } else if (categoryName.length > 100) {
        this.showValidationMessage('nameError', 'Category name must be less than 100 characters', 'error');
        this.highlightField('categoryName', false);
        isValid = false;
    } else {
        this.hideValidationMessage('nameError');
        this.highlightField('categoryName', true);
    }

    const categoryImage = document.getElementById('categoryImage').files[0];
    if (!categoryImage) {
        this.showValidationMessage('imageError', 'Category image is required', 'error');
        isValid = false;
    } else {
        this.hideValidationMessage('imageError');
    }

    const description = document.getElementById('categoryDescription').value.trim();
    if (description.length > 500) {
        this.showValidationMessage('descriptionError', 'Description must be less than 500 characters', 'warning');
    } else {
        this.hideValidationMessage('descriptionError');
    }

    return isValid;
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

showValidationMessage: function (elementId, message, type) {
    const element = document.getElementById(elementId);
    if (!element) return;

    element.textContent = message;
    element.className = `validation-message ${type}`;
    element.style.display = 'block';
},

hideValidationMessage: function (elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.style.display = 'none';
    }
},

setupRealTimeValidation: function () {
    const categoryName = document.getElementById('categoryName');
    if (categoryName) {
        categoryName.addEventListener('input', () => {
            this.validateField('categoryName');
        });

        categoryName.addEventListener('blur', () => {
            this.validateField('categoryName');
        });
    }

    const categoryDescription = document.getElementById('categoryDescription');
    if (categoryDescription) {
        categoryDescription.addEventListener('input', () => {
            this.validateField('categoryDescription');
        });
    }
},

validateField: function (fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return;

    const value = field.value.trim();
    let isValid = true;
    let message = '';
    let messageType = 'error';

    switch (fieldId) {
        case 'categoryName':
            if (!value) {
                isValid = false;
                message = 'Category name is required';
            } else if (value.length > 100) {
                isValid = false;
                message = 'Category name must be less than 100 characters';
            } else {
                isValid = true;
                message = 'Category name looks good!';
                messageType = 'success';
            }
            this.showValidationMessage('nameError', message, messageType);
            break;

        case 'categoryDescription':
            if (value.length > 500) {
                message = 'Description must be less than 500 characters';
                messageType = 'warning';
                this.showValidationMessage('descriptionError', message, messageType);
            } else {
                this.hideValidationMessage('descriptionError');
            }
            return;

        default:
            isValid = true;
    }

    this.highlightField(fieldId, isValid);
},

submitForm: function () {
    const submitBtn = document.querySelector('.btn-add');
    adminHelpers.showLoading(submitBtn, 'Adding...');

    const formData = this.collectFormData();

    setTimeout(() => {
        try {
            console.log('Form data to be submitted:', formData);

            adminHelpers.showNotification('Category added successfully!', 'success');

            adminHelpers.hideLoading(submitBtn);

            setTimeout(() => {
                window.location.href = '/admin/categories';
            }, 1000);
        } catch (error) {
            adminHelpers.showNotification('Failed to add category: ' + error.message, 'error');
            adminHelpers.hideLoading(submitBtn);
        }
    }, 1500);
},

collectFormData: function () {
    return {
        name: document.getElementById('categoryName').value.trim(),
        description: document.getElementById('categoryDescription').value.trim(),
        status: document.querySelector('input[name="status"]:checked').value,
        image: document.getElementById('categoryImage').files[0]
    };
},

cancelForm: function () {
    if (this.isFormDirty()) {
        if (!confirm('You have unsaved changes. Are you sure you want to cancel?')) {
            return;
        }
    }
    window.location.href = '/admin/categories';
},

isFormDirty: function () {
    const categoryName = document.getElementById('categoryName').value.trim();
    const categoryDescription = document.getElementById('categoryDescription').value.trim();
    const categoryImage = document.getElementById('categoryImage').files[0];

    return categoryName !== '' || categoryDescription !== '' || categoryImage !== undefined;
},

resetForm: function () {
    const form = document.getElementById('addCategoryForm');
    if (form) {
        form.reset();
    }

    const preview = document.getElementById('imagePreview');
    if (preview) {
        preview.style.display = 'none';
    }

    document.querySelectorAll('.is-valid, .is-invalid').forEach(el => {
        el.classList.remove('is-valid', 'is-invalid');
    });

    document.querySelectorAll('.validation-message').forEach(msg => {
        msg.style.display = 'none';
    });
}
};

document.addEventListener('DOMContentLoaded', function () {
    categoryManager.init();
});

window.addEventListener('beforeunload', function (e) {
    if (!categoryManager.isSubmitting && categoryManager.isFormDirty()) {
        e.preventDefault();
        e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
    }
});