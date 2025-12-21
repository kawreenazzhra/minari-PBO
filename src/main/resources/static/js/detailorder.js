document.addEventListener('DOMContentLoaded', function () {
    const shippingSection = document.getElementById('shippingSection');
    const paymentSection = document.getElementById('paymentSection');
    const checkoutBtn = document.getElementById('realCheckoutBtn'); // Corrected ID
    const checkoutForm = document.getElementById('checkoutForm');

    loadSavedPreferences();

    // Navigation listeners
    if (shippingSection) {
        shippingSection.addEventListener('click', () => {
            // Append current params to keep selected items
            const currentParams = window.location.search;
            window.location.href = (window.ROUTE_SHIPPING || '/shipping-address') + currentParams;
        });
    }

    if (paymentSection) {
        paymentSection.addEventListener('click', () => {
            const currentParams = window.location.search;
            window.location.href = (window.ROUTE_PAYMENT_METHOD || '/payment-method') + currentParams;
        });
    }

    // Checkout Handler
    if (checkoutBtn && checkoutForm) {
        checkoutBtn.addEventListener('click', (e) => {
            e.preventDefault();

            // Validate address before submitting
            const addressInput = document.getElementById('inputShippingAddress');
            if (!addressInput || !addressInput.value) {
                alert('Please select a shipping address first.');
                return;
            }

            checkoutBtn.textContent = "Processing...";
            checkoutBtn.disabled = true;

            // Submit
            checkoutForm.submit();
        });
    }

    function loadSavedPreferences() {
        // 1. Payment Method
        const savedPayment = localStorage.getItem('selectedPaymentMethod');
        const paymentDisplay = document.getElementById('selectedPaymentDisplay');
        const paymentInput = document.getElementById('inputPaymentMethod');

        if (savedPayment && paymentDisplay && paymentInput) {
            const names = {
                'cod': 'Cash on Delivery',
                'virtual': 'Virtual Account Transfer',
                'ewallet': 'E-wallet'
            };

            // Backend valid values mapping
            const backendValues = {
                'cod': 'cash_on_delivery',
                'virtual': 'bank_transfer',
                'ewallet': 'e_wallet'
            };

            paymentDisplay.textContent = names[savedPayment] || savedPayment;
            // Set the hidden input to the value expected by the controller validation
            paymentInput.value = backendValues[savedPayment] || savedPayment;
        }

        // 2. Shipping Address - REMOVED
        // We now rely on URL parameters and server-side rendering for the address.
        // Restoring from localStorage caused conflicting states (e.g. showing old address despite new selection).
        // if (savedAddressData) { ... }
    }
});
