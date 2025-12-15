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

function addToWishlist(productId, buttonElement) {
  if (isGuestRole()) {
    const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
    loginModal.show();
    return;
  }

  // Get product name for toast message
  const productCard = buttonElement?.closest('[data-product-id]');
  const productName = productCard ? productCard.querySelector('.p-name')?.textContent : 'Product';

  // Toggle active state visually
  if (buttonElement) {
    buttonElement.classList.toggle('active');
    const img = buttonElement.querySelector('img');
    if (img && buttonElement.classList.contains('active')) {
      img.src = "{{ asset('images/whislist-active.png') }}";
    } else if (img) {
      img.src = "{{ asset('images/whislist.png') }}";
    }
  }

  // Send API request
  fetch('/api/wishlist', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': window.CSRF_TOKEN || document.querySelector('meta[name="csrf-token"]')?.content
    },
    body: JSON.stringify({
      product_id: productId
    })
  })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        showToast(`"${productName}" has been added to your wishlist.`);
      } else {
        // If already in wishlist, remove it (logic from original code, assuming API behaves this way or we want toggle)
        if (data.message && data.message.includes('already')) {
          removeFromWishlist(productId, buttonElement, productName);
        } else {
          showToast(data.message || 'Failed to add to wishlist.', true);
          // Revert visual state
          if (buttonElement) {
            buttonElement.classList.toggle('active');
            const img = buttonElement.querySelector('img');
            if (img) img.src = "{{ asset('images/whislist.png') }}";
          }
        }
      }
    })
    .catch(error => {
      console.error('Error:', error);
      showToast('An error occurred. Please try again.', true);
      // Revert visual state
      if (buttonElement) {
        buttonElement.classList.toggle('active');
        const img = buttonElement.querySelector('img');
        if (img) img.src = "{{ asset('images/whislist.png') }}";
      }
    });
}

function removeFromWishlist(productId, buttonElement, productName) {
  fetch(`/api/wishlist/${productId}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': window.CSRF_TOKEN || document.querySelector('meta[name="csrf-token"]')?.content
    }
  })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        showToast(`"${productName}" removed from wishlist.`);
        if (buttonElement) {
          buttonElement.classList.remove('active');
          const img = buttonElement.querySelector('img');
          if (img) img.src = "{{ asset('images/whislist.png') }}";
        }
      }
    })
    .catch(error => {
      console.error('Error:', error);
    });
}

// ------------------------
// Toast Function
// ------------------------
function showToast(message, isError = false) {
  const toastEl = document.getElementById('miniToast');
  const toastBody = document.getElementById('toastMessage');

  if (!toastEl || !toastBody) return;

  // Update message
  toastBody.textContent = message;

  // Change color for error messages
  if (isError) {
    toastEl.querySelector('.toast-body').style.color = 'var(--danger)';
  } else {
    toastEl.querySelector('.toast-body').style.color = '';
  }

  // Show toast
  const toast = bootstrap.Toast.getOrCreateInstance(toastEl);
  toast.show();
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

  // Add hover effects for wishlist buttons
  const wishButtons = document.querySelectorAll('.p-wish');
  wishButtons.forEach(button => {
    // Check initial state (if product is already in wishlist)
    // This would require an API call to check - for simplicity, we'll assume not

    button.addEventListener('mouseenter', function () {
      if (!this.classList.contains('active')) {
        const img = this.querySelector('img');
        if (img) {
          img.src = "{{ asset('images/whislist-hover.png') }}";
        }
      }
    });

    button.addEventListener('mouseleave', function () {
      if (!this.classList.contains('active')) {
        const img = this.querySelector('img');
        if (img) {
          img.src = "{{ asset('images/whislist.png') }}";
        }
      }
    });
  });

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