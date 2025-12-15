function getOrderDetails(orderId) {
    const orderData = {
        "0101": {
            customer: "Anneiza",
            email: "janejane@yahoo.com",
            phone: "+62 812 3456 678",
            ordersCount: "1 Order",
            shippingAddress: "Kos Graha 9<br>Jl. H. Umar No.33<br>Citeureup<br>Kec. Dayeuhkolot<br>Kabupaten Bandung<br>Jawa Barat 40257<br>Indonesia",
            products: [
                {
                    name: "White jeans",
                    category: "Categories",
                    price: "Rp. 200.000,00"
                }
            ],
            courier: "J&TUH",
            service: "J&TUH EJET (Regular)",
            deliveryCost: "Rp. 0",
            subtotal: "Rp. 200.000,00",
            total: "Rp. 200.000,00",
            paymentStatus: "Paid",
            shippingStatus: "Shipped",
            date: "28.10.2025"
        },
        "0102": {
            customer: "Lisa",
            email: "lisa@example.com",
            phone: "+62 812 3456 789",
            ordersCount: "1 Order",
            shippingAddress: "Jl. Merdeka No.123<br>Jakarta Pusat<br>DKI Jakarta<br>10110<br>Indonesia",
            products: [
                {
                    name: "Blue Shirt",
                    category: "Categories",
                    price: "Rp. 175.000,00"
                }
            ],
            courier: "JNE",
            service: "REG",
            deliveryCost: "Rp. 15.000,00",
            subtotal: "Rp. 175.000,00",
            total: "Rp. 190.000,00",
            paymentStatus: "Unpaid",
            shippingStatus: "Processing",
            date: "28.10.2025"
        },
        "0103": {
            customer: "Aliyah",
            email: "aliyah@example.com",
            phone: "+62 812 3456 890",
            ordersCount: "1 Order",
            shippingAddress: "Jl. Sudirman No.456<br>Jakarta Selatan<br>DKI Jakarta<br>12190<br>Indonesia",
            products: [
                {
                    name: "Soft green cardigan",
                    category: "Categories",
                    price: "Rp. 250.000,00"
                }
            ],
            courier: "TIKI",
            service: "ONS",
            deliveryCost: "Rp. 20.000,00",
            subtotal: "Rp. 250.000,00",
            total: "Rp. 270.000,00",
            paymentStatus: "Unpaid",
            shippingStatus: "Processing",
            date: "27.10.2025"
        }
    };
    
    return orderData[orderId] || null;
}

function populateOrderDetails(orderId) {
    const order = getOrderDetails(orderId);
    if (!order) {
        alert("Order not found");
        window.location.href = "/admin/orders";
        return;
    }
    
    document.querySelector('.page-title').textContent = `Order #${orderId}`;
    
    document.querySelector('.order-detail-card:nth-child(1) .detail-row:nth-child(1) .detail-value').textContent = order.customer;
    document.querySelector('.order-detail-card:nth-child(1) .detail-row:nth-child(2) .detail-value').textContent = order.email;
    document.querySelector('.order-detail-card:nth-child(1) .detail-row:nth-child(3) .detail-value').textContent = order.phone;
    document.querySelector('.order-detail-card:nth-child(1) .detail-row:nth-child(4) .detail-value').textContent = order.ordersCount;
    
    document.querySelector('.order-detail-card:nth-child(2) .detail-value').innerHTML = order.shippingAddress;
    
    const productItem = document.querySelector('.product-item');
    productItem.querySelector('.product-name').textContent = order.products[0].name;
    productItem.querySelector('.product-category').textContent = order.products[0].category;
    productItem.querySelector('.product-price').textContent = order.products[0].price;
    
    document.querySelector('.order-detail-card:nth-child(4) .detail-row:nth-child(1) .detail-value').textContent = order.courier;
    document.querySelector('.order-detail-card:nth-child(4) .detail-row:nth-child(2) .detail-value').textContent = order.service;
    document.querySelector('.order-detail-card:nth-child(4) .detail-row:nth-child(3) .detail-value').textContent = order.deliveryCost;
    
    document.querySelector('.order-detail-card:nth-child(5) .detail-row:nth-child(1) .detail-value').textContent = order.subtotal;
    document.querySelector('.order-detail-card:nth-child(5) .detail-row:nth-child(2) .detail-value').textContent = order.deliveryCost;
    document.querySelector('.order-detail-card:nth-child(5) .detail-row:nth-child(3) .detail-value').textContent = order.total;
    
    const statusBadges = document.querySelectorAll('.order-status-badge');
    statusBadges[0].textContent = order.paymentStatus;
    statusBadges[1].textContent = order.shippingStatus;
    
    updateStatusBadges(order.paymentStatus, order.shippingStatus);
    
    updateShipmentTimeline(order.shippingStatus);
    
    document.querySelector('.order-detail-header .detail-value').textContent = order.date;
}

function updateStatusBadges(paymentStatus, shippingStatus) {
    const paymentBadge = document.querySelector('.order-status-badge:nth-child(1)');
    const shippingBadge = document.querySelector('.order-status-badge:nth-child(2)');
    
    paymentBadge.className = 'order-status-badge';
    shippingBadge.className = 'order-status-badge';
    
    if (paymentStatus === 'Paid') {
        paymentBadge.classList.add('badge-active');
    } else if (paymentStatus === 'Unpaid') {
        paymentBadge.classList.add('badge-pending');
    } else if (paymentStatus === 'Pending') {
        paymentBadge.classList.add('badge-cancelled');
    }
    
    if (shippingStatus === 'Shipped') {
        shippingBadge.classList.add('badge-active');
    } else if (shippingStatus === 'Processing') {
        shippingBadge.classList.add('badge-shipped');
    } else if (shippingStatus === 'Pending') {
        shippingBadge.classList.add('badge-cancelled');
    }
}

function updateShipmentTimeline(shippingStatus) {
    const statusDots = document.querySelectorAll('.status-dot');
    
    statusDots.forEach(dot => {
        dot.classList.remove('active');
    });
    
    if (shippingStatus === 'Shipped') {
        statusDots[0].classList.add('active');
        statusDots[1].classList.add('active');
    } else if (shippingStatus === 'Processing') {
        statusDots[0].classList.add('active');
    } else if (shippingStatus === 'Delivered') {
        statusDots.forEach(dot => dot.classList.add('active'));
    }
}

function updateOrderStatus() {
    const orderId = document.querySelector('.page-title').textContent.replace('Order #', '');
    const currentOrder = getOrderDetails(orderId);
    
    const newShippingStatus = prompt('Enter new shipping status (Processing/Shipped/Delivered):', currentOrder.shippingStatus);
    
    if (newShippingStatus && ['Processing', 'Shipped', 'Delivered'].includes(newShippingStatus)) {
        if (adminHelpers.confirmAction(`Change order status from "${currentOrder.shippingStatus}" to "${newShippingStatus}"?`)) {
            adminHelpers.showNotification(`Order status updated to: ${newShippingStatus}`, 'success');
            
            const shippingBadge = document.querySelector('.order-status-badge:nth-child(2)');
            shippingBadge.textContent = newShippingStatus;
            updateStatusBadges(currentOrder.paymentStatus, newShippingStatus);
            updateShipmentTimeline(newShippingStatus);
        }
    } else if (newShippingStatus) {
        alert('Invalid status. Please use: Processing, Shipped, or Delivered');
    }
}

function printInvoice() {
    window.print();
}

document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    let orderId = urlParams.get('id');
    
    if (!orderId) {
        const title = document.querySelector('.page-title');
        if (title) {
            const match = title.textContent.match(/#(\d+)/);
            if (match) {
                orderId = match[1];
            }
        }
    }
    
    if (!orderId) {
        orderId = '0101';
    }
    
    if (typeof populateOrderDetails === 'function') {
        populateOrderDetails(orderId);
    }
    
    const updateBtn = document.querySelector('.btn-update');
    if (updateBtn) {
        updateBtn.addEventListener('click', updateOrderStatus);
    }
    
    const printBtn = document.querySelector('.btn-cancel');
    if (printBtn && printBtn.textContent.includes('Print')) {
        printBtn.addEventListener('click', printInvoice);
    }
    
    document.querySelectorAll('.status-dot').forEach((dot, index) => {
        dot.addEventListener('click', function() {
            const statuses = ['Order Placed', 'Package Picked Up', 'In Transit', 'Out for Delivery', 'Delivered'];
            alert(`Current status: ${statuses[index]}`);
        });
    });
});