document.addEventListener('DOMContentLoaded', function() {
    const backToHomeBtn = document.getElementById('backToHomeBtn');
    const reviewBtn = document.getElementById('reviewBtn');
    const orderTitle = document.getElementById('orderTitle');

    // Show order number
    const orderNumber = localStorage.getItem('currentOrderNumber') || "----";
    if (orderTitle) {
        orderTitle.textContent = `Order #${orderNumber}`;
    }

    // Back to home
    if (backToHomeBtn) {
        backToHomeBtn.addEventListener('click', function() {
            clearCartData();
            const to = window.ROUTE_HOME || '/b';
            window.location.href = to;
        });
    }

    // Go to rating page
    if (reviewBtn) {
        reviewBtn.addEventListener('click', function() {
            const lastOrder = {
                orderNumber: orderNumber,
                total: localStorage.getItem('cartTotal') || '0',
                items: JSON.parse(localStorage.getItem('selectedCartItems') || '[]')
            };

            localStorage.setItem('reviewOrder', JSON.stringify(lastOrder));
            clearCartData();

            const target = window.ROUTE_REVIEW || '/rating';
            console.log("Go to rating:", target);
            window.location.href = target;
        });
    }

    function clearCartData() {
        localStorage.removeItem('selectedCartItems');
        localStorage.removeItem('cartTotal');
        localStorage.removeItem('currentOrderNumber');
        localStorage.removeItem('selectedAddress');
        localStorage.removeItem('selectedPaymentMethod');
    }

    // Hover animation
    const confirmationIcon = document.querySelector('.confirmation-icon');
    if (confirmationIcon) {
        confirmationIcon.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.1)';
            this.style.transition = 'transform 0.3s ease';
        });

        confirmationIcon.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    }
});
