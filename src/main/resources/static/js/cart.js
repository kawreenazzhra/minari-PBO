// cart.js — versi Laravel dengan support guest & user

document.addEventListener('DOMContentLoaded', () => {
    console.log("ROLE SEKARANG:", window.APP_ROLE);

    const selectAll = document.getElementById('selectAll');
    const cartList = document.getElementById('cartList');
    const totalPrice = document.getElementById('totalPrice');
    const checkoutBtn = document.getElementById('checkoutBtn');
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content');

    if (!cartList) return;

    const toNum = (v) => Number(String(v).replace(/[^\d]/g, '')) || 0;
    const fmtIDR = (n) => 'Rp ' + (Number(n) || 0).toLocaleString('id-ID'); // Fixed: remove dot after Rp

    function getRows() {
        return [...cartList.querySelectorAll('.cart-item')];
    }

    function computeTotal() {
        let total = 0;
        getRows().forEach(row => {
            const checked = row.querySelector('.item-check')?.checked;
            const price = Number(row.dataset.price || 0);
            const qtyInp = row.querySelector('.item-qty');
            const qty = Math.max(1, Number(qtyInp?.value || 1));
            if (checked) total += price * qty;
        });

        if (totalPrice) {
            totalPrice.textContent = fmtIDR(total);
        }

        if (checkoutBtn) {
            checkoutBtn.disabled = total === 0;
        }

        return total;
    }

    function syncSelectAll() {
        if (!selectAll) return;

        const checks = [...cartList.querySelectorAll('.item-check')];
        const checkedCount = checks.filter(c => c.checked).length;
        selectAll.checked = checkedCount > 0 && checkedCount === checks.length;
        selectAll.indeterminate = checkedCount > 0 && checkedCount < checks.length;
    }

    // === Event: Select All
    if (selectAll) {
        selectAll.addEventListener('change', () => {
            cartList.querySelectorAll('.item-check').forEach(c => c.checked = selectAll.checked);
            computeTotal();
        });
    }

    // === Update quantity via API
    async function updateQuantity(itemId, quantity, isGuest) {
        const endpoint = isGuest ? `/api/guest/cart/${itemId}` : `/api/cart/${itemId}`;
        try {
            const response = await fetch(endpoint, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken,
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ quantity: quantity })
            });

            const data = await response.json();
            if (data.success) {
                computeTotal();
            }
        } catch (error) {
            console.error('Error updating cart:', error);
        }
    }

    // === Remove item via API
    async function removeItem(itemId, isGuest, rowElement) {
        if (!confirm('Are you sure you want to remove this item?')) return;

        const endpoint = isGuest ? `/api/guest/cart/${itemId}` : `/api/cart/${itemId}`;
        try {
            const response = await fetch(endpoint, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                    'Accept': 'application/json'
                }
            });

            const data = await response.json();
            if (data.success) {
                rowElement.remove();
                computeTotal();
                syncSelectAll();

                // If cart is empty, refresh page
                if (getRows().length === 0) {
                    setTimeout(() => location.reload(), 500);
                }
            }
        } catch (error) {
            console.error('Error removing item:', error);
        }
    }

    // === Delegasi tombol +/- 
    cartList.addEventListener('click', (e) => {
        const row = e.target.closest('.cart-item');
        if (!row) return;

        const itemId = row.dataset.id;
        const isGuest = row.dataset.isGuest === 'true';
        const inp = row.querySelector('.item-qty');

        if (!inp) return;

        let quantity = Math.max(1, Number(inp.value || 1));

        if (e.target.classList.contains('btnPlus')) {
            quantity += 1;
        }

        if (e.target.classList.contains('btnMinus') && quantity > 1) {
            quantity -= 1;
        }

        if (e.target.classList.contains('remove-item') || e.target.closest('.remove-item')) {
            removeItem(itemId, isGuest, row);
            return;
        }

        // Only update if quantity changed
        if (quantity !== Number(inp.value)) {
            inp.value = quantity;

            // Update via API
            updateQuantity(itemId, quantity, isGuest);

            // Update local total
            computeTotal();
        }
    });

    // === Perubahan qty manual
    cartList.addEventListener('change', (e) => {
        const el = e.target;

        if (el.classList.contains('item-qty')) {
            const row = el.closest('.cart-item');
            if (!row) return;

            const itemId = row.dataset.id;
            const isGuest = row.dataset.isGuest === 'true';
            let quantity = Math.max(1, Number(el.value || 1));
            el.value = quantity;

            // Update via API
            updateQuantity(itemId, quantity, isGuest);
            computeTotal();
        }

        if (el.classList.contains('item-check')) {
            syncSelectAll();
            computeTotal();
        }
    });

    // === Checkout
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', () => {
            const role = window.APP_ROLE || 'guest';
            console.log('Checkout clicked, role =', role);

            // 1) Guest -> login
            if (role === 'guest') {
                const loginModalEl = document.getElementById('loginModal');
                if (loginModalEl && window.bootstrap) {
                    const modal = bootstrap.Modal.getOrCreateInstance(loginModalEl);
                    modal.show();
                } else {
                    window.location.href = '/login?redirect=' + encodeURIComponent('/cart');
                }
                return;
            }

            // 2) Admin -> no checkout
            if (role === 'admin') {
                alert('Admin cannot checkout. Please use a user account.');
                return;
            }

            // 3) User -> collect selected items
            const selectedIds = [];
            cartList.querySelectorAll('.cart-item').forEach(row => {
                const checked = row.querySelector('.item-check')?.checked;
                if (checked) selectedIds.push(row.dataset.id);
            });

            if (selectedIds.length === 0) {
                alert('Please select at least one item to checkout.');
                return;
            }

            // Redirect to payment with selected IDs
            window.location.href = '/payment?items=' + selectedIds.join(',');
        });
    }

    // init
    computeTotal();
    syncSelectAll();
});

// === Global function untuk menambahkan ke cart dari halaman lain
window.addToCart = async function (productId, productName, price, image = '', quantity = 1, size = '') {
    const role = window.APP_ROLE || 'guest';
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content');

    const endpoint = role === 'guest' ? '/api/guest/cart' : '/api/cart';
    const body = role === 'guest' ? {
        product_id: productId,
        name: productName,
        price: price,
        quantity: quantity,
        size: size,
        color: '',
        image: image
    } : {
        product_id: productId,
        quantity: quantity,
        size: size,
        color: ''
    };

    try {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken,
                'Accept': 'application/json'
            },
            body: JSON.stringify(body)
        });

        const data = await response.json();
        if (data.success) {
            // alert('Item added to cart!'); // REPLACED WITH TOAST

            const toastEl = document.getElementById('miniToast');
            if (toastEl && window.bootstrap) {
                const toastBody = document.getElementById('toastMessage');
                if (toastBody) toastBody.innerText = 'Item has been added to your cart.';
                const toast = window.bootstrap.Toast.getOrCreateInstance(toastEl);
                toast.show();
            } else {
                // Fallback if toast not present
                console.log('Item added to cart');
            }

            // Update cart count in navbar if available
            if (window.NavbarRole?.updateCartCount) {
                window.NavbarRole.updateCartCount(data.cart_count);
            }
            return true;
        }
    } catch (error) {
        console.error('Error adding to cart:', error);
    }

    return false;
};