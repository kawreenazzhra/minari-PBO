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

        // 2. Shipping Address
        const savedAddressData = localStorage.getItem('selectedAddressData');
        const addressDisplay = document.getElementById('selectedAddressDisplay');
        const addressDetails = document.getElementById('selectedAddressDetails');

        // Hidden inputs
        const inputAddr = document.getElementById('inputShippingAddress');
        const inputCity = document.getElementById('inputShippingCity');
        const inputZip = document.getElementById('inputShippingPostalCode');

        if (savedAddressData) {
            try {
                const data = JSON.parse(savedAddressData);

                // Update Visuals
                if (addressDisplay) addressDisplay.textContent = data.display_name;
                if (addressDetails) addressDetails.textContent = data.details;

                // Update Hidden Inputs (CRITICAL for backend)
                if (inputAddr && data.address_line1) inputAddr.value = data.address_line1;
                if (inputCity && data.city) inputCity.value = data.city;
                if (inputZip && data.postal_code) inputZip.value = data.postal_code;

            } catch (e) {
                console.error("Error parsing saved address", e);
            }
        }
    }
});
