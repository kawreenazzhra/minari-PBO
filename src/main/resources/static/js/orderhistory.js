document.addEventListener('DOMContentLoaded', function() {
    const viewDetailsButtons = document.querySelectorAll('.btn-view-details');
    viewDetailsButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const orderNumber = this.closest('.order-card').querySelector('h3').textContent;
            console.log(`View details clicked for: ${orderNumber}`);
        });
    });

    const searchBox = document.querySelector('.search-box');
    if (searchBox) {
        searchBox.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const orderCards = document.querySelectorAll('.order-card');
            
            orderCards.forEach(card => {
                const orderText = card.textContent.toLowerCase();
                if (orderText.includes(searchTerm)) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    }

    const orderList = document.querySelector('.order-list');
    if (orderList) {
        orderList.style.opacity = '0';
        setTimeout(() => {
            orderList.style.transition = 'opacity 0.3s ease';
            orderList.style.opacity = '1';
        }, 100);
    }
});