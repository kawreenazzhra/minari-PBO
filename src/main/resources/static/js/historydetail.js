document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderNumber = urlParams.get('order');
    
    if (orderNumber) {
        console.log(`Loading details for order: ${orderNumber}`);
    }

    const backBtn = document.querySelector('.back-btn');
    if (backBtn) {
        backBtn.addEventListener('click', function(e) {
            e.preventDefault();
            window.history.back();
        });
    }

    const needHelpBtn = document.querySelector('.btn-secondary');
    const orderAgainBtn = document.querySelector('.btn-primary');
    
    if (needHelpBtn) {
        needHelpBtn.addEventListener('click', function() {
            window.location.href = '/help';
        });
    }
    
    if (orderAgainBtn) {
        orderAgainBtn.addEventListener('click', function() {
            const orderNumber = document.querySelector('.page-title').textContent.split('#')[1];
            alert('Items from this order have been added to your cart!');
        });
    }

    const mainContent = document.querySelector('.main-content');
    if (mainContent) {
        mainContent.style.opacity = '0';
        setTimeout(() => {
            mainContent.style.transition = 'opacity 0.3s ease';
            mainContent.style.opacity = '1';
        }, 100);
    }
});
