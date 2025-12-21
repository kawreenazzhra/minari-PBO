// ====== Role utils ======
const Role = {
  GUEST: 'guest',
  USER: 'user',
  ADMIN: 'admin'
};

// ====== Templates ======
function tplGuest() {
  return `
  <nav class="navbar navbar-expand-lg fixed-top py-2" id="mainNavbar" style="background-color: #FFF6F0; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15); transition: all 0.3s ease;">
    <div class="container-fluid px-5">
      <a class="navbar-brand" href="/">
        <img src="/images/logofix.png" alt="MINARI Logo" height="40" width="auto" class="me-2">
      </a>

      <div class="d-flex align-items-center ms-auto gap-3">
        <button id="accBtn" class="btn p-0 border-0" style="background: transparent !important; cursor: pointer;">
          <img src="/images/akun.png" alt="User" width="24" height="24">
        </button>
        <a href="/wishlist"><img src="/images/whislist.png" alt="Favorite" width="24" height="24"></a>
        <a href="/search"><img src="/images/searchnav.png" alt="Search" width="24" height="24"></a>
        <a href="/cart"><img src="/images/chart.png" alt="Cart" width="24" height="24"></a>
        <a href="/menu"><img src="/images/menu.png" alt="Menu" width="24" height="24"></a>
      </div>
    </div>
  </nav>

  <div id="accMini" class="accmini" style="display: none; position: fixed; z-index: 9999; background: #FFF6F0; border: 1px solid #ead9d2; border-radius: 12px; box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15); padding: 14px 12px 12px; width: 220px;">
    <div class="accmini__row" style="display: flex; align-items: center; gap: 8px; margin-bottom: 10px;">
      <img src="/images/akun.png" width="16" height="16" class="accmini__icon" alt="">
      <a class="accmini__name" href="/login" style="font-weight: 500; color: #1f1f1f; text-decoration: none;">Guest</a>
    </div>
    <a id="loginLink" class="accmini__btn" href="/login" onclick="window.location.href='/login'; return false;" style="display: inline-flex; align-items: center; justify-content: center; width: 100%; height: 36px; background-color: #ffffff; border: 1.5px solid #d9c8c1; border-radius: 10px; color: #1e1e1e; font-weight: 600; text-decoration: none; font-size: 14px; cursor: pointer !important; transition: all 0.25s ease; margin-top: 8px; position: relative; z-index: 10000; pointer-events: auto;">Log in</a>
  </div>

  <div id="searchOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: #FFF6F0; z-index: 20000; overflow-y: auto;">
    <div class="container pt-4">
        <div class="d-flex align-items-center mb-4">
            <button id="closeSearchBtn" style="background: none; border: none; font-size: 24px; cursor: pointer; color: #5a4b45;" class="me-3">
                <i class="bi bi-arrow-left"></i>
            </button>
            <form action="/search" method="GET" style="flex-grow: 1;">
                <input type="text" name="q" placeholder="keywords" style="width: 100%; border: none; border-bottom: 1px solid #ccc; padding: 10px; font-size: 18px; background: transparent; outline: none;">
            </form>
        </div>
        
        <div class="mt-4">
            <h6 class="fw-bold mb-3" style="color: #5a4b45;">Trending</h6>
            <div class="d-flex flex-wrap gap-2">
                <a href="/search?q=Celana Barrel" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Celana Barrel</a>
                <a href="/search?q=Koleksi Jeans" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Koleksi Jeans</a>
                <a href="/search?q=Koleksi Flannel" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Koleksi Flannel</a>
                <a href="/search?q=Winter Travel" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Winter Travel</a>
                <a href="/search?q=Kaos Kaki HEATTECH" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Kaos Kaki HEATTECH</a>
                <a href="/search?q=Limited Offer" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Limited Offer</a>
                <a href="/search?q=Sale" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Sale</a>
            </div>
        </div>

        <div class="mt-5">
            <h6 class="fw-bold mb-3" style="color: #5a4b45;">Riwayat pencarian</h6>
            <p class="text-muted small">Anda dapat melihat riwayat pencarian Anda di sini.</p>
        </div>
    </div>
  </div>`;
}

function tplUser() {
  return `
  <nav class="navbar navbar-expand-lg fixed-top py-2" id="mainNavbar" style="background-color: #FFF6F0; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15); transition: all 0.3s ease;">
    <div class="container-fluid px-5">
      <a class="navbar-brand" href="/">
        <img src="/images/logofix.png" alt="MINARI Logo" height="40" width="auto" class="me-2">
      </a>

      <div class="d-flex align-items-center ms-auto gap-3">
        <button id="accBtn" class="btn p-0 border-0" style="background: transparent !important; cursor: pointer;">
          <img src="/images/akun.png" alt="User" width="24" height="24">
        </button>
        <a href="/wishlist"><img src="/images/whislist.png" alt="Favorite" width="24" height="24"></a>
        <a href="/search"><img src="/images/searchnav.png" alt="Search" width="24" height="24"></a>
        <a href="/cart"><img src="/images/chart.png" alt="Cart" width="24" height="24"></a>
        <a href="/menu"><img src="/images/menu.png" alt="Menu" width="24" height="24"></a>
      </div>
    </div>
  </nav>

  <div id="accMini" class="accmini" style="display: none; position: fixed; z-index: 9999; background: #FFF6F0; border: 1px solid #ead9d2; border-radius: 12px; box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15); padding: 14px 12px 12px; width: 220px;">
    <div class="accmini__row" style="display: flex; align-items: center; gap: 8px; margin-bottom: 10px;">
      <img src="/images/akun.png" width="16" height="16" class="accmini__icon" alt="">
      <a class="accmini__link" href="/account" style="font-weight: 500; color: #1f1f1f; text-decoration: none;">Account</a>
    </div>
    <div class="accmini__row" style="display: flex; align-items: center; gap: 8px; margin-bottom: 10px;">
      <img src="/images/order history.png" width="16" height="16" class="accmini__icon" alt="">
      <a class="accmini__link" href="/order-history" style="font-weight: 500; color: #1f1f1f; text-decoration: none;">Order history</a>
    </div>
    <button id="logoutBtn" class="accmini__btn" style="display: inline-flex; align-items: center; justify-content: center; width: 100%; height: 36px; background-color: #ffffff; border: 1.5px solid #d9c8c1; border-radius: 10px; color: #1e1e1e; font-weight: 600; text-decoration: none; font-size: 14px; cursor: pointer; transition: all 0.25s ease; margin-top: 8px;">Log out</button>
  </div>

  <div id="searchOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: #FFF6F0; z-index: 20000; overflow-y: auto;">
    <div class="container pt-4">
        <div class="d-flex align-items-center mb-4">
            <button id="closeSearchBtn" style="background: none; border: none; font-size: 24px; cursor: pointer; color: #5a4b45;" class="me-3">
                <i class="bi bi-arrow-left"></i>
            </button>
            <form action="/search" method="GET" style="flex-grow: 1;">
                <input type="text" name="q" placeholder="keywords" style="width: 100%; border: none; border-bottom: 1px solid #ccc; padding: 10px; font-size: 18px; background: transparent; outline: none;">
            </form>
        </div>
        
        <div class="mt-4">
            <h6 class="fw-bold mb-3" style="color: #5a4b45;">Trending</h6>
            <div class="d-flex flex-wrap gap-2">
                <a href="/search?q=Celana Barrel" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Celana Barrel</a>
                <a href="/search?q=Koleksi Jeans" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Koleksi Jeans</a>
                <a href="/search?q=Koleksi Flannel" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Koleksi Flannel</a>
                <a href="/search?q=Winter Travel" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Winter Travel</a>
                <a href="/search?q=Kaos Kaki HEATTECH" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Kaos Kaki HEATTECH</a>
                <a href="/search?q=Limited Offer" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Limited Offer</a>
                <a href="/search?q=Sale" class="btn btn-sm btn-outline-secondary rounded-pill" style="border-color: #ddd;">Sale</a>
            </div>
        </div>

        <div class="mt-5">
            <h6 class="fw-bold mb-3" style="color: #5a4b45;">Riwayat pencarian</h6>
            <p class="text-muted small">Anda dapat melihat riwayat pencarian Anda di sini.</p>
        </div>
    </div>
  </div>`;
}

// Scroll Effect - Background hilang saat scroll (tanpa blur)
function initializeScrollEffect() {
  const navbar = document.getElementById('mainNavbar');
  if (navbar) {
    window.addEventListener('scroll', function () {
      if (window.scrollY > 50) {
        // Scrolled Down: Background hilang total (transparent, no blur)
        navbar.style.setProperty('background-color', 'transparent', 'important');
        navbar.style.setProperty('box-shadow', 'none', 'important');
      } else {
        // Top of page: Solid background
        navbar.style.setProperty('background-color', '#FFF6F0', 'important');
        navbar.style.setProperty('box-shadow', '0 4px 10px rgba(0, 0, 0, 0.15)', 'important');
      }
    });
  }
}



// ====== Main Render Function ======
function renderNavbar() {
  const mount = document.getElementById('navMount');
  if (!mount) {
    console.error('Element #navMount not found');
    return;
  }

  // Get role from global variable set by Laravel
  const role = window.APP_ROLE || Role.GUEST;

  console.log('Rendering navbar with role:', role);

  // Clear and render template based on role
  mount.innerHTML = '';
  if (role === Role.USER) {
    mount.innerHTML = tplUser();
  } else {
    mount.innerHTML = tplGuest();
  }

  // Initialize scroll effect
  initializeScrollEffect();

  // Setup event listeners
  setTimeout(attachEventListeners, 100);
}

function attachEventListeners() {
  console.log('Attaching event listeners...');

  const accBtn = document.getElementById('accBtn');
  const accMini = document.getElementById('accMini');

  console.log('Found elements:', { accBtn, accMini });

  if (accBtn && accMini) {
    // Remove any existing event listeners
    const newAccBtn = accBtn.cloneNode(true);
    accBtn.parentNode.replaceChild(newAccBtn, accBtn);

    // Get new reference
    const currentAccBtn = document.getElementById('accBtn');
    const currentAccMini = accMini;

    // Add click event to account button
    currentAccBtn.addEventListener('click', function (e) {
      e.preventDefault();
      e.stopPropagation();
      console.log('Account button clicked!');

      // Get position of button
      const rect = currentAccBtn.getBoundingClientRect();
      console.log('Button position:', rect);

      // Toggle dropdown
      if (currentAccMini.style.display === 'block' || currentAccMini.style.display === '') {
        currentAccMini.style.display = 'none';
        currentAccMini.style.pointerEvents = 'none';
      } else {
        // Position dropdown below button (Centered)
        currentAccMini.style.position = 'fixed';
        currentAccMini.style.top = (rect.bottom + 10) + 'px';

        // Center alignment
        const popupWidth = 220;
        const buttonCenter = rect.left + (rect.width / 2);
        const leftPos = buttonCenter - (popupWidth / 2);

        currentAccMini.style.left = leftPos + 'px';
        currentAccMini.style.right = 'auto';
        currentAccMini.style.display = 'block';
        currentAccMini.style.opacity = '1';
        currentAccMini.style.pointerEvents = 'auto'; // Enable clicks
      }
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', function (e) {
      if (currentAccMini &&
        !currentAccMini.contains(e.target) &&
        !currentAccBtn.contains(e.target)) {
        currentAccMini.style.display = 'none';
        currentAccMini.style.pointerEvents = 'none';
      }
    });



    // Handle logout button
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', function (e) {
        e.preventDefault();
        window.location.href = '/logout';
      });
    }

    // Handle login link (fix for unclickable issue)
    const loginLink = document.getElementById('loginLink');
    if (loginLink) {
      loginLink.addEventListener('click', function (e) {
        e.preventDefault();
        e.stopPropagation(); // Stop it from closing the dropdown immediately
        console.log('Login link clicked - Forcing navigation');
        window.location.href = '/login';
      });
    }

    // Handle standard navbar links to ensure navigation works
    const navLinks = document.querySelectorAll('a[href="/menu"], a[href="/cart"], a[href="/wishlist"], a[href="/admin/dashboard"]');
    navLinks.forEach(link => {
      link.addEventListener('click', function (e) {
        // e.preventDefault(); // Don't prevent default unless necessary, but...
        // If there's some global handler blocking it, we force it.
        // For now, let's just log and ensure propagation.
        e.stopPropagation();
        console.log('Nav link clicked:', this.href);
        // Force navigation if it doesn't happen naturally
        // window.location.href = this.href; 
      });
      // Safety net: if for some reason expected behavior fails, force it on click
      link.onclick = function () {
        window.location.href = this.href;
        return true;
      };
    });

    console.log('Event listeners attached successfully');
  }
}

// Initialize
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', renderNavbar);
} else {
  renderNavbar();
}

// Re-render on window load for safety
window.addEventListener('load', renderNavbar);