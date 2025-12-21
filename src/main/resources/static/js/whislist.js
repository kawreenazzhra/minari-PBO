/* Wishlist page logic - Integrated with Laravel API */

// Configuration
// Adapted for Thymeleaf: CSRF token is expected in a meta tag named 'csrf-token' which we added to view.html
const CSRF_TOKEN = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content') || document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const IS_AUTHENTICATED = window.IS_AUTHENTICATED || false;

// Format price to IDR
const fmtIDR = (v) => {
    if (!v) return 'Rp 0';
    return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        minimumFractionDigits: 0
    }).format(v).replace('IDR', 'Rp').replace(/\s/g, '');
};

// Helper functions
function showToast(msg) {
    const el = document.getElementById('miniToast');
    if (!el) return;
    el.querySelector('.toast-body').textContent = msg;
    new bootstrap.Toast(el).show();
}

function asset(path) {
    return new URL(path, location.origin).toString();
}

// API functions
async function fetchWishlist() {
    try {
        const response = await fetch('/api/wishlist', {
            headers: {
                'Accept': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });

        if (response.status === 401) {
            return null; // Unauthorized
        }

        if (!response.ok) throw new Error('Failed to fetch wishlist');
        return await response.json();
    } catch (error) {
        console.error('Error:', error);
        return [];
    }
}

async function removeFromWishlist(id) {
    try {
        const response = await fetch(`/api/wishlist/${id}`, {
            method: 'DELETE',
            headers: {
                'X-CSRF-TOKEN': CSRF_TOKEN,
                'Accept': 'application/json'
            }
        });
        return await response.json();
    } catch (error) {
        console.error('Error:', error);
        return { success: false, message: 'Network error' };
    }
}

async function addToCart(productId) {
    try {
        const response = await fetch('/api/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': CSRF_TOKEN,
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                product_id: productId,
                quantity: 1,
                size: '',
                color: ''
            })
        });
        return await response.json();
    } catch (error) {
        console.error('Error:', error);
        return { success: false, message: 'Network error' };
    }
}

// Render functions
function renderGuestView() {
    const content = document.getElementById('wishContent');
    if (!content) return;

    // Hide wishContent, show gate
    document.getElementById('wishContent').style.display = 'none';
    document.getElementById('gate').style.display = 'block';
}

function renderEmptyView() {
     const root = document.getElementById('wishList');
     if(!root) return;
     root.innerHTML = `
        <div class="empty-wishlist text-center py-5">
            <h3 class="mb-3">Your wishlist is empty</h3>
            <p class="text-muted mb-4">Start adding items you love!</p>
            <a href="/products" class="btn btn-primary">Browse Products</a>
        </div>
    `;
}

function renderWishlistItems(items) {
    const root = document.getElementById('wishList');
    const counter = document.getElementById('resultCount');

    if (!root || !counter) return;

    counter.textContent = `Results: ${items.length} product${items.length !== 1 ? 's' : ''}`;

    if (items.length === 0) {
        renderEmptyView();
        return;
    }

    root.innerHTML = items.map(item => {
        const product = item.product || item;
        // Adjust logic for image path based on backend storage
        const imageUrl = product.imageUrl
            ? product.imageUrl
            : (product.image ? `/uploads/${product.image}` : '/images/default-product.jpg');

        return `
            <article class="w-item" data-id="${product.id}" data-wishlist-id="${item.id}">
                <div class="w-img">
                    <img src="${imageUrl}" alt="${product.name}" 
                         onerror="this.src='/images/default-product.jpg'">
                </div>
                <div class="w-info">
                    <h6 class="w-name">${product.name}</h6>
                    <div class="w-meta">
                        <span class="price">${fmtIDR(product.price)}</span>
                        ${product.size ? `<span>Size: ${product.size}</span>` : ''}
                    </div>
                </div>
                <div class="w-actions">
                    <button class="w-btn js-addcart" data-id="${product.id}">
                        Add to cart
                    </button>
                    <button class="w-remove js-remove" data-id="${item.id}" title="Remove from wishlist">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </article>
        `;
    }).join('');
}

// Main
document.addEventListener('DOMContentLoaded', async () => {
    // If we have rendered the 'gate' server-side via Thymeleaf, we might not need this,
    // but the reference code does client-side check. We will support both.
    // However, since we now have server-side templating (view.html), let's see.
    // The reference view had: <main id="gate" ... style="display: none;">
    // So it hides it by default and JS shows it if !IS_AUTHENTICATED.

    if (!IS_AUTHENTICATED) {
        renderGuestView();
        return;
    }

    const items = await fetchWishlist();
    if (items === null) {
        // Session expired
        renderGuestView();
        return;
    }

    renderWishlistItems(items);

    // Event listeners
    const wishListContainer = document.getElementById('wishList');
    if (wishListContainer) {
        wishListContainer.addEventListener('click', async (e) => {
            const removeBtn = e.target.closest('.js-remove');
            const addCartBtn = e.target.closest('.js-addcart');
            const card = e.target.closest('.w-item');

            if (!card) return;

            if (removeBtn) {
                const wishlistId = card.dataset.wishlistId;
                const result = await removeFromWishlist(wishlistId);
                if (result.success) {
                    showToast('Removed from wishlist');
                    // Refresh
                    const items = await fetchWishlist();
                    renderWishlistItems(items || []);
                } else {
                    showToast(result.message);
                }
            }

            if (addCartBtn) {
                const productId = card.dataset.id;
                const result = await addToCart(productId);
                if (result.success) {
                    showToast('Added to cart');
                } else {
                    showToast(result.message);
                }
            }
        });
    }
});