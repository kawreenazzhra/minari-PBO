document.addEventListener('DOMContentLoaded', function () {
    const promotionForm = document.getElementById('promotionForm');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');

    const today = new Date().toISOString().split('T')[0];
    if (startDateInput) startDateInput.min = today;
    if (endDateInput) endDateInput.min = today;

    if (startDateInput) {
        startDateInput.addEventListener('change', function () {
            if (endDateInput) {
                endDateInput.min = this.value;
            }
        });
    }

    if (promotionForm) {
        promotionForm.addEventListener('submit', function (e) {
            // Basic validation
            const startDate = new Date(startDateInput.value);
            const endDate = new Date(endDateInput.value);

            if (endDate <= startDate) {
                e.preventDefault();
                alert('End date must be after start date');
                return false;
            }

            return true;
        });
    }

    const promoCodeInput = document.getElementById('promoCode');
    const promoNameInput = document.getElementById('promoName');
    if (promoCodeInput) {
        promoCodeInput.addEventListener('input', function () {
            this.value = this.value.toUpperCase();
            // Sync the hidden name field
            if (promoNameInput) {
                promoNameInput.value = this.value;
            }
        });
    }

    const discountTypeSelect = document.getElementById('discountType');
    if (discountTypeSelect) {
        discountTypeSelect.addEventListener('change', function () {
            const discountValueInput = document.getElementById('discountValue');
            if (this.value === 'PERCENTAGE') {
                discountValueInput.placeholder = 'Enter percentage (0-100)';
                discountValueInput.max = 100;
            } else {
                discountValueInput.placeholder = 'Enter fixed amount';
                discountValueInput.removeAttribute('max');
            }
        });
    }
});

// Generate random promo code
function generatePromoCode() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = '';
    for (let i = 0; i < 8; i++) {
        code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    document.getElementById('promoCode').value = code;
    // Also update the hidden name field
    const promoNameInput = document.getElementById('promoName');
    if (promoNameInput) {
        promoNameInput.value = code;
    }
}

// Toggle all products checkbox
function toggleAllProducts(checkbox) {
    const productCheckboxes = document.querySelectorAll('.product-checkbox');
    productCheckboxes.forEach(cb => {
        cb.checked = checkbox.checked;
    });
}

// Update "All Products" checkbox when individual checkboxes change
document.addEventListener('DOMContentLoaded', function () {
    const productCheckboxes = document.querySelectorAll('.product-checkbox');
    const selectAllCheckbox = document.getElementById('selectAllProducts');

    if (productCheckboxes && selectAllCheckbox) {
        productCheckboxes.forEach(cb => {
            cb.addEventListener('change', function () {
                const allChecked = Array.from(productCheckboxes).every(checkbox => checkbox.checked);
                const noneChecked = Array.from(productCheckboxes).every(checkbox => !checkbox.checked);

                if (allChecked) {
                    selectAllCheckbox.checked = true;
                    selectAllCheckbox.indeterminate = false;
                } else if (noneChecked) {
                    selectAllCheckbox.checked = false;
                    selectAllCheckbox.indeterminate = false;
                } else {
                    selectAllCheckbox.checked = false;
                    selectAllCheckbox.indeterminate = true;
                }
            });
        });
    }
});