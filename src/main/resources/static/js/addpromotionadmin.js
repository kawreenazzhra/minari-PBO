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
                // validateDates(); // Removed as part of simplifying client-side validation
            }
        });
    }

    if (endDateInput) {
        // endDateInput.addEventListener('change', validateDates); // Removed as part of simplifying client-side validation
    }

    if (promotionForm) {
        // Removed hijacker to allow server-side submission
        promotionForm.addEventListener('submit', function (e) {
            // It's better to rely on HTML5 required attributes + Server validation which I added.

            return isValid;
        }

    const promoCodeInput = document.getElementById('promoCode');
        if (promoCodeInput) {
            promoCodeInput.addEventListener('input', function () {
                this.value = this.value.toUpperCase();
            });
        }

        const discountTypeSelect = document.getElementById('discountType');
        if (discountTypeSelect) {
            discountTypeSelect.addEventListener('change', function () {
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