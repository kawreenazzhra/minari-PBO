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
// Toast Notification
// ------------------------
function showToast(message, subtext = '') {
  let box = document.getElementById('miniToast');
  if (!box) {
    box = document.createElement('div');
    box.id = 'miniToast';
    box.className = 'mini-toast';
    box.innerHTML = '<span id="mtMsg"></span><small id="mtSub"></small>';
    document.body.appendChild(box);
  }
  box.querySelector('#mtMsg').textContent = message;
  box.querySelector('#mtSub').textContent = subtext || '';
  box.classList.add('show');
  clearTimeout(box._hideT);
  box._hideT = setTimeout(() => box.classList.remove('show'), 1800);
}

// ------------------------
// Cart & Wishlist Functions
// ------------------------
// ------------------------
// Cart & Wishlist Functions
// ------------------------

// addToCart is now handled by cart.js (window.addToCart)

// Wrapper untuk addToCart dari category grid
function addToCartFromCategory(productId) {
  // Get product info dari DOM
  const cardElement = document.querySelector(`[data-product-id="${productId}"]`);
  if (!cardElement) {
    console.error('Product element not found');
    return;
  }

  const productName = cardElement.querySelector('.p-name')?.textContent || 'Product';
  const priceText = cardElement.querySelector('.current-price')?.textContent || 'Rp 0';
  const price = Number(String(priceText).replace(/[^\d]/g, '')) || 0;
  const imageUrl = cardElement.querySelector('.p-thumb img')?.src || '';

  // Call global addToCart function
  window.addToCart(productId, productName, price, imageUrl, 1);
}

// LocalStorage Wishlist Implementation
function getWishlist() {
  const saved = localStorage.getItem('minari_wishlist');
  return saved ? JSON.parse(saved) : [];
}

function saveWishlist(items) {
  localStorage.setItem('minari_wishlist', JSON.stringify(items));
}

function checkWishlistStatus() {
  const role = window.APP_ROLE || 'guest';
  
  if (role === 'guest') {
    // For guest users, use localStorage
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
  } else {
    // For authenticated users, fetch from API
    checkWishlistStatusAPI();
  }
}

// Check wishlist status from API (for authenticated users)
async function checkWishlistStatusAPI() {
  try {
    const response = await fetch('/api/wishlist', {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      }
    });

    if (!response.ok) {
      console.error('Error fetching wishlist');
      return;
    }

    const wishlistItems = await response.json();
    const wishlistProductIds = wishlistItems.map(item => item.product_id);

    document.querySelectorAll('.p-wish').forEach(btn => {
      const pid = Number(btn.getAttribute('data-product-id'));
      const icon = btn.querySelector('i');
      
      if (wishlistProductIds.includes(pid)) {
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
  } catch (error) {
    console.error('Error checking wishlist status:', error);
  }
}

function addToWishlist(productId, buttonElement) {
  // Check if user is authenticated
  const role = window.APP_ROLE || 'guest';
  
  if (role === 'guest') {
    // For guest users, use localStorage
    let wishlist = getWishlist();
    const pid = Number(productId);
    const index = wishlist.indexOf(pid);

    if (index === -1) {
      wishlist.push(pid);
      showToast('Added to wishlist');
    } else {
      wishlist.splice(index, 1);
      showToast('Removed from wishlist');
    }

    saveWishlist(wishlist);
    checkWishlistStatus();
    return;
  }

  // For authenticated users, call API
  addToWishlistAPI(productId, buttonElement);
}

// Add to wishlist via API (for authenticated users)
async function addToWishlistAPI(productId, buttonElement) {
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const pid = Number(productId);

  try {
    // Get current wishlist to check if item is already there
    const checkResponse = await fetch(`/api/wishlist/check/${pid}`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      }
    });

    if (!checkResponse.ok) {
      console.error('Error checking wishlist status');
      return;
    }

    const checkData = await checkResponse.json();
    const isInWishlist = checkData.isInWishlist;

    let endpoint = '/api/wishlist';
    let method = 'POST';
    let body = JSON.stringify({ product_id: pid });

    // If already in wishlist, we need to remove it
    if (isInWishlist) {
      // First get the wishlist to find the item ID
      const listResponse = await fetch('/api/wishlist', {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
          'X-Requested-With': 'XMLHttpRequest'
        }
      });

      if (listResponse.ok) {
        const items = await listResponse.json();
        const wishlistItem = items.find(item => item.product_id === pid);
        if (wishlistItem) {
          endpoint = `/api/wishlist/${wishlistItem.id}`;
          method = 'DELETE';
          body = undefined;
        }
      }
    }

    const response = await fetch(endpoint, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken,
        'Accept': 'application/json'
      },
      body: body
    });

    const data = await response.json();
    if (data.success) {
      const message = isInWishlist ? 'Removed from wishlist' : 'Added to wishlist';
      showToast(message);
      checkWishlistStatus();
    } else {
      showToast('Error: ' + (data.message || 'Could not update wishlist'));
    }
  } catch (error) {
    console.error('Error updating wishlist:', error);
    showToast('Error updating wishlist');
  }
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
  setTimeout(() => {
    checkWishlistStatus();
  }, 100);

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
