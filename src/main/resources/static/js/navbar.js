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
    <div class="container">
      <a class="navbar-brand" href="/">
        <img src="/images/logofix.png" alt="MINARI Logo" height="40" width="auto" class="me-2">
      </a>

      <div class="d-flex align-items-center ms-auto gap-3">
        <button id="accBtn" class="btn p-0 border-0" style="background: transparent !important; cursor: pointer;">
          <img src="/images/akun.png" alt="User" width="24" height="24">
        </button>
        <a href="/products"><img src="/images/whislist.png" alt="Favorite" width="24" height="24"></a>
        <a href="/products"><img src="/images/searchnav.png" alt="Search" width="24" height="24"></a>
        <a href="/cart"><img src="/images/chart.png" alt="Cart" width="24" height="24"></a>
        <a href="/products"><img src="/images/menu.png" alt="Products" width="24" height="24"></a>
      </div>
    </div>
  </nav>

  <div id="accMini" class="accmini" style="display: none; position: fixed; z-index: 9999; background: #FFF6F0; border: 1px solid #ead9d2; border-radius: 12px; box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15); padding: 14px 12px 12px; width: 220px;">
    <div class="accmini__row" style="display: flex; align-items: center; gap: 8px; margin-bottom: 10px;">
      <img src="/images/akun.png" width="16" height="16" class="accmini__icon" alt="">
      <a class="accmini__name" href="/login" style="font-weight: 500; color: #1f1f1f; text-decoration: none;">Guest</a>
    </div>
    <a id="loginLink" class="accmini__btn" href="/login" onclick="window.location.href='/login'; return false;" style="display: inline-flex; align-items: center; justify-content: center; width: 100%; height: 36px; background-color: #ffffff; border: 1.5px solid #d9c8c1; border-radius: 10px; color: #1e1e1e; font-weight: 600; text-decoration: none; font-size: 14px; cursor: pointer !important; transition: all 0.25s ease; margin-top: 8px; position: relative; z-index: 10000; pointer-events: auto;">Log in</a>
  </div>`;
}

function tplUser() {
  return `
  <nav class="navbar navbar-expand-lg fixed-top py-2" id="mainNavbar" style="background-color: #FFF6F0; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15); transition: all 0.3s ease;">
    <div class="container">
      <a class="navbar-brand" href="/">
        <img src="/images/logofix.png" alt="MINARI Logo" height="40" width="auto" class="me-2">
      </a>

      <div class="d-flex align-items-center ms-auto gap-3">
        <button id="accBtn" class="btn p-0 border-0" style="background: transparent !important; cursor: pointer;">
          <img src="/images/akun.png" alt="User" width="24" height="24">
        </button>
        <a href="/products"><img src="/images/whislist.png" alt="Favorite" width="24" height="24"></a>
        <a href="/products"><img src="/images/searchnav.png" alt="Search" width="24" height="24"></a>
        <a href="/cart"><img src="/images/chart.png" alt="Cart" width="24" height="24"></a>
        <a href="/products"><img src="/images/menu.png" alt="Products" width="24" height="24"></a>
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
  </div>`;
}

// Scroll Effect
function initializeScrollEffect() {
  const navbar = document.getElementById('mainNavbar');
  if (navbar) {
    window.addEventListener('scroll', function () {
      if (window.scrollY > 50) {
        navbar.style.backgroundColor = 'transparent';
        navbar.style.boxShadow = 'none';
      } else {
        navbar.style.backgroundColor = '#FFF6F0';
        navbar.style.boxShadow = '0 4px 10px rgba(0, 0, 0, 0.15)';
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
        // Position dropdown below button
        currentAccMini.style.position = 'fixed';
        currentAccMini.style.top = (rect.bottom + 10) + 'px';
        currentAccMini.style.right = (window.innerWidth - rect.right) + 'px'; // Fix alignment
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
        if (confirm('Apakah Anda yakin ingin logout?')) {
          window.location.href = '/logout';
        }
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