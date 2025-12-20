// ====== Role utils ======
const Role = {
  GUEST: 'guest',
  USER: 'user',
  ADMIN: 'admin'
};

// ====== Templates ======

// 1. Template for the Hamburger Button (triggers full menu)
function tplHamburgerBtn() {
  return `
      <button id="hamburgerBtn" class="btn p-0 border-0 bg-transparent ms-2" style="cursor: pointer;">
          <img src="/images/menu.png" alt="Menu" width="24" height="24">
      </button>
  `;
}

// 2. Template for the Full Screen Overlay
function tplFullMenuOverlay() {
  const categories = window.APP_CATEGORIES || [];

  let gridItems = '';
  if (categories.length > 0) {
    categories.forEach(cat => {
      // Use default image if none provided
      // Assuming imageUrl is stored relative to static or as an absolute path
      // If cat.imageUrl starts with 'uploads/', prepend '/'
      let imgPath = cat.imageUrl ? cat.imageUrl : '/images/placeholder.png';
      if (imgPath && !imgPath.startsWith('/') && !imgPath.startsWith('http')) {
        imgPath = '/' + imgPath;
      }

      gridItems += `
        <a href="/products?category=${cat.id}" class="full-menu-item">
            <div class="full-menu-img-container">
                <img src="${imgPath}" alt="${cat.name}">
            </div>
            <h3>${cat.name}</h3>
        </a>
        `;
    });
  } else {
    gridItems = '<p class="text-center w-100">No categories found.</p>';
  }

  return `
    <div id="fullMenuOverlay" class="full-menu-overlay">
        <!-- Header -->
        <div class="full-menu-header">
            <div class="logo">
                <span style="font-family: 'Playfair Display', serif; font-size: 24px; color: #4A3B34; letter-spacing: 2px;">MINARI</span>
            </div>
            <button id="closeMenuBtn" class="btn-close-menu">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <!-- Search Bar -->
        <div class="full-menu-search">
             <i class="fas fa-search"></i>
             <input type="text" id="menuSearchInput" placeholder="keywords">
        </div>

        <!-- Categories Grid -->
        <div class="full-menu-grid">
            ${gridItems}
        </div>
    </div>
  `;
}

// Template: Mobile/Guest Navbar
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
        <a href="#" id="navSearchBtn"><img src="/images/searchnav.png" alt="Search" width="24" height="24"></a>
        <a href="/cart"><img src="/images/chart.png" alt="Cart" width="24" height="24"></a>
        ${tplHamburgerBtn()}
      </div>
    </div>
  </nav>

  <div id="accMini" class="accmini" style="display: none;">
    <div class="accmini__row">
      <img src="/images/akun.png" class="accmini__icon" alt="">
      <a class="accmini__name" href="/login">Guest</a>
    </div>
    <a id="loginLink" class="accmini__btn" href="/login">Log in</a>
  </div>`;
}

// Template: User (Logged In) Navbar
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
        <a href="#" id="navSearchBtn"><img src="/images/searchnav.png" alt="Search" width="24" height="24"></a>
        <a href="/cart"><img src="/images/chart.png" alt="Cart" width="24" height="24"></a>
        ${tplHamburgerBtn()}
      </div>
    </div>
  </nav>

  <div id="accMini" class="accmini" style="display: none;">
    <div class="accmini__row">
      <img src="/images/akun.png" class="accmini__icon" alt="">
      <a class="accmini__link" href="/profile">Account</a>
    </div>
    <div class="accmini__row">
      <img src="/images/order history.png" class="accmini__icon" alt="">
      <a class="accmini__link" href="/order-history">Order history</a>
    </div>
    <button id="logoutBtn" class="accmini__btn">Log out</button>
  </div>`;
}

// Scroll Effect
function initializeScrollEffect() {
  const navbar = document.getElementById('mainNavbar');
  if (navbar) {
    window.addEventListener('scroll', function () {
      if (window.scrollY > 50) {
        navbar.classList.add('scrolled');
      } else {
        navbar.classList.remove('scrolled');
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

  // Get role global
  const role = window.APP_ROLE || Role.GUEST;
  console.log('Rendering navbar with role:', role);

  // Clear and render template
  mount.innerHTML = '';

  // 1. Render Navbar
  if (role === Role.USER) {
    mount.innerHTML = tplUser();
  } else {
    mount.innerHTML = tplGuest();
  }

  // 2. Append Full Menu Overlay to mount (or body, but mount is fine if outside container)
  const overlayHTML = tplFullMenuOverlay();
  mount.insertAdjacentHTML('beforeend', overlayHTML);

  // Initialize scroll effect
  initializeScrollEffect();

  // Setup event listeners
  setTimeout(attachEventListeners, 100);
}

function attachEventListeners() {
  // === Account Dropdown Logic ===
  const accBtn = document.getElementById('accBtn');
  const accMini = document.getElementById('accMini');

  if (accBtn && accMini) {
    const newAccBtn = accBtn.cloneNode(true);
    accBtn.parentNode.replaceChild(newAccBtn, accBtn);
    const currentAccBtn = document.getElementById('accBtn');

    currentAccBtn.addEventListener('click', function (e) {
      e.preventDefault();
      e.stopPropagation();
      const rect = currentAccBtn.getBoundingClientRect();

      if (accMini.classList.contains('show')) {
        accMini.classList.remove('show');
      } else {
        accMini.style.top = (rect.bottom + 10) + 'px';
        accMini.style.right = (window.innerWidth - rect.right) + 'px';
        accMini.classList.add('show');
      }
    });

    document.addEventListener('click', function (e) {
      if (accMini.classList.contains('show') &&
        !accMini.contains(e.target) &&
        !currentAccBtn.contains(e.target)) {
        accMini.classList.remove('show');
      }
    });
  }

  // === Full Menu Overlay Logic ===
  const hamburgerBtn = document.getElementById('hamburgerBtn');
  const fullMenuOverlay = document.getElementById('fullMenuOverlay');
  const closeMenuBtn = document.getElementById('closeMenuBtn');
  const menuSearchInput = document.getElementById('menuSearchInput');

  if (hamburgerBtn && fullMenuOverlay && closeMenuBtn) {
    hamburgerBtn.addEventListener('click', function (e) {
      e.preventDefault();
      fullMenuOverlay.classList.add('active');
      fullMenuOverlay.classList.remove('search-mode'); // Ensure menu mode
      fullMenuOverlay.classList.add('menu-mode');

      // Show categories, ensure search is hidden or secondary if desired
      // Based on request: "Button garis 3 saja yang menampilkan kategori"
      // "Button search ... ga perlu nampilin kategori"

      const grid = fullMenuOverlay.querySelector('.full-menu-grid');
      if (grid) grid.style.display = 'grid';

      const searchBox = fullMenuOverlay.querySelector('.full-menu-search');
      if (searchBox) searchBox.style.display = 'none'; // Menu only shows categories per request context implication

      document.body.style.overflow = 'hidden';
    });

    closeMenuBtn.addEventListener('click', function (e) {
      e.preventDefault();
      fullMenuOverlay.classList.remove('active');
      fullMenuOverlay.classList.remove('search-mode');
      fullMenuOverlay.classList.remove('menu-mode');
      document.body.style.overflow = '';
    });

    // Handle Enter on search input
    if (menuSearchInput) {
      menuSearchInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
          const query = this.value;
          window.location.href = '/products?search=' + encodeURIComponent(query);
        }
      });
    }

    // === Search Button (Nav) Logic ===
    const navSearchBtn = document.getElementById('navSearchBtn');
    if (navSearchBtn) {
      navSearchBtn.addEventListener('click', function (e) {
        e.preventDefault();
        fullMenuOverlay.classList.add('active');
        fullMenuOverlay.classList.add('search-mode');
        fullMenuOverlay.classList.remove('menu-mode');

        // Hide categories for search mode
        const grid = fullMenuOverlay.querySelector('.full-menu-grid');
        if (grid) grid.style.display = 'none';

        // Show search box
        const searchBox = fullMenuOverlay.querySelector('.full-menu-search');
        if (searchBox) searchBox.style.display = 'block';

        document.body.style.overflow = 'hidden';
        setTimeout(() => {
          if (menuSearchInput) menuSearchInput.focus();
        }, 100);
      });
    }
  }

  // === Other Buttons ===
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', function (e) {
      e.preventDefault();
      if (confirm('Apakah Anda yakin ingin logout?')) {
        window.location.href = '/logout';
      }
    });
  }

  const loginLink = document.getElementById('loginLink');
  if (loginLink) {
    loginLink.addEventListener('click', function (e) {
      window.location.href = '/login';
    });
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