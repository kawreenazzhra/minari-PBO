// ------------------------
// Helper role (guest / logged in)
// ------------------------
function isGuestRole() {
  try {
    const r = window.NavbarRole?.getRole?.() || window.APP_ROLE || 'guest';
    return r === 'guest';
  } catch {
    return (window.APP_ROLE || 'guest') === 'guest';
  }
}

// ------------------------
// Helper format Rupiah
// ------------------------
const fmtIDR = v => {
  if (!v) return 'Rp 0';
  return new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR' })
    .format(v)
    .replace('IDR', 'Rp')
    .replace(/\s/g, '');
};

// ------------------------
// Cart & Wishlist Functions
// ------------------------
// ------------------------
// Cart & Wishlist Functions
// ------------------------

// addToCart is now handled by cart.js (window.addToCart)

// LocalStorage Wishlist Implementation
function getWishlist() {
  const saved = localStorage.getItem('minari_wishlist');
  return saved ? JSON.parse(saved) : [];
}

function saveWishlist(items) {
  localStorage.setItem('minari_wishlist', JSON.stringify(items));
}

function checkWishlistStatus() {
  const wishlist = getWishlist();
  document.querySelectorAll('.p-wish').forEach(btn => {
    const pid = Number(btn.getAttribute('data-product-id'));
    const icon = btn.querySelector('i');
    if (wishlist.includes(pid)) {
      btn.classList.add('active');
      if (icon) {
        icon.classList.remove('far');
        icon.classList.add('fas');
        icon.style.color = '#ff5978';
      }
    } else {
      btn.classList.remove('active');
      if (icon) {
        icon.classList.remove('fas');
        icon.classList.add('far');
        icon.style.color = '#6b6b6b';
      }
    }
  });
}

function addToWishlist(productId, buttonElement) {
  // Toggle wishlist
  let wishlist = getWishlist();
  const pid = Number(productId);
  const index = wishlist.indexOf(pid);

  let action = '';
  if (index === -1) {
    wishlist.push(pid);
    action = 'added';
    showToast('Added to wishlist');
  } else {
    wishlist.splice(index, 1);
    action = 'removed';
    showToast('Removed from wishlist');
  }

  saveWishlist(wishlist);
  checkWishlistStatus(); // Update UI
}

// Remove unused function
function removeFromWishlist(productId, buttonElement, productName) {
  // handled by toggle in addToWishlist
}
// ------------------------
// Initialize Event Listeners
// ------------------------
document.addEventListener('DOMContentLoaded', () => {
  // Initialize Bootstrap toast
  const toastEl = document.getElementById('miniToast');
  if (toastEl) {
    toastEl.addEventListener('hidden.bs.toast', () => {
      const toastBody = document.getElementById('toastMessage');
      if (toastBody) toastBody.style.color = '';
    });
  }

  // Check wishlist status on load
  checkWishlistStatus();

  // Add click animation for cart buttons
  const cartButtons = document.querySelectorAll('.p-cart');
  cartButtons.forEach(button => {
    button.addEventListener('click', function (e) {
      // Add bounce animation
      this.style.transform = 'scale(0.9)';
      setTimeout(() => {
        this.style.transform = '';
      }, 200);
    });
  });
});
