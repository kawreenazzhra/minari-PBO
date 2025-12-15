document.addEventListener('DOMContentLoaded', function() {
    const promotionForm = document.getElementById('promotionForm');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');

    const today = new Date().toISOString().split('T')[0];
    if (startDateInput) startDateInput.min = today;
    if (endDateInput) endDateInput.min = today;

    if (startDateInput) {
        startDateInput.addEventListener('change', function() {
            if (endDateInput) {
                endDateInput.min = this.value;
                validateDates();
            }
        });
    }

    if (endDateInput) {
        endDateInput.addEventListener('change', validateDates);
    }

    if (promotionForm) {
        promotionForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            if (validateForm()) {
                createPromotion();
            }
        });
    }

    function validateForm() {
        let isValid = true;
        const promoCode = document.getElementById('promoCode')?.value.trim();
        const discountType = document.getElementById('discountType')?.value;
        const discountValue = document.getElementById('discountValue')?.value;
        const description = document.getElementById('description')?.value.trim();
        const startDate = document.getElementById('startDate')?.value;
        const endDate = document.getElementById('endDate')?.value;

        clearErrors();

        if (!promoCode) {
            showError('promoCodeError', 'Promo code is required');
            isValid = false;
        }

        if (!discountType) {
            showError('discountTypeError', 'Discount type is required');
            isValid = false;
        }

        if (!discountValue || discountValue <= 0) {
            showError('discountValueError', 'Valid discount value is required');
            isValid = false;
        }

        if (discountType === 'percentage' && discountValue > 100) {
            showError('discountValueError', 'Percentage discount cannot exceed 100%');
            isValid = false;
        }

        if (!description) {
            showError('descriptionError', 'Description is required');
            isValid = false;
        }

        if (!startDate) {
            showError('startDateError', 'Start date is required');
            isValid = false;
        }

        if (!endDate) {
            showError('endDateError', 'End date is required');
            isValid = false;
        }

        if (startDate && endDate && startDate > endDate) {
            showError('endDateError', 'End date must be after start date');
            isValid = false;
        }

        return isValid;
    }

    function validateDates() {
        const startDate = startDateInput?.value;
        const endDate = endDateInput?.value;
        
        if (startDate && endDate && startDate > endDate) {
            showError('endDateError', 'End date must be after start date');
            return false;
        } else {
            clearError('endDateError');
            return true;
        }
    }

    function showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }

    function clearError(elementId) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }
    }

    function clearErrors() {
        const errorElements = document.querySelectorAll('.validation-message.error');
        errorElements.forEach(element => {
            element.textContent = '';
            element.style.display = 'none';
        });
    }

    function createPromotion() {
        const promotionData = {
            promoCode: document.getElementById('promoCode').value.trim().toUpperCase(),
            description: document.getElementById('description').value.trim(),
            discountType: document.getElementById('discountType').value,
            discountValue: document.getElementById('discountValue').value,
            minPurchase: document.getElementById('minPurchase').value || 0,
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value,
            usageLimit: document.getElementById('usageLimit').value || null,
            status: document.getElementById('status').value,
            applicableProducts: Array.from(document.getElementById('applicableProducts').selectedOptions).map(option => option.value),
            createdAt: new Date().toISOString()
        };

        const submitBtn = promotionForm.querySelector('.btn-submit');
        adminHelpers.showLoading(submitBtn, 'Creating...');

        setTimeout(() => {
            try {
                let promotions = JSON.parse(localStorage.getItem('minari_promotions')) || [];
                promotionData.id = Date.now();
                promotions.push(promotionData);
                localStorage.setItem('minari_promotions', JSON.stringify(promotions));

                adminHelpers.showNotification('Promotion created successfully!', 'success');
                
                adminHelpers.hideLoading(submitBtn);
                
                setTimeout(() => {
                    window.location.href = '/admin/promotions';
                }, 1500);
            } catch (error) {
                adminHelpers.showNotification('Failed to create promotion: ' + error.message, 'error');
                adminHelpers.hideLoading(submitBtn);
            }
        }, 1000);
    }

    const promoCodeInput = document.getElementById('promoCode');
    if (promoCodeInput) {
        promoCodeInput.addEventListener('input', function() {
            this.value = this.value.toUpperCase();
        });
    }

    const discountTypeSelect = document.getElementById('discountType');
    if (discountTypeSelect) {
        discountTypeSelect.addEventListener('change', function() {
            const discountValueInput = document.getElementById('discountValue');
            if (this.value === 'percentage') {
                discountValueInput.placeholder = 'Enter percentage (0-100)';
                discountValueInput.max = 100;
            } else {
                discountValueInput.placeholder = 'Enter fixed amount';
                discountValueInput.max = null;
            }
        });
    }
});