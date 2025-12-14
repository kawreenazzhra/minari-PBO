/**
 * LandingPageManager Class
 * Main orchestrator untuk landing page
 * Mengkoordinasikan ProductDisplay, PromotionBanner, CartManager, AuthManager
 * OOP Concepts: Composition, Delegation, Orchestration
 */
class LandingPageManager {
    constructor() {
        this.productDisplay = null;
        this.promotionBanner = null;
        this.cartManager = null;
        this.authManager = null;
        this.navbarManager = null;

        this.initialize();
    }

    /**
     * Inisialisasi landing page manager
     */
    initialize() {
        console.log('Initializing LandingPageManager...');

        // Initialize semua components dalam urutan yang tepat
        this.authManager = new AuthManager();
        this.cartManager = new CartManager();
        this.productDisplay = new ProductDisplay();
        this.promotionBanner = new PromotionBanner();
        this.navbarManager = new NavbarManager();

        // Setup event listeners
        this.setupEventListeners();

        console.log('LandingPageManager initialized successfully');
    }

    /**
     * Setup event listeners untuk landing page
     */
    setupEventListeners() {
        // Category card click handlers
        document.querySelectorAll('.category-card').forEach((card, index) => {
            card.addEventListener('click', (e) => {
                e.preventDefault();
                const href = card.querySelector('a').getAttribute('href');
                if (href) {
                    window.location.href = href;
                }
            });
        });

        // User icon click handler
        const userIcon = document.querySelector('.fa-user');
        if (userIcon) {
            userIcon.addEventListener('click', (e) => {
                e.preventDefault();
                if (this.authManager.isAuthenticated()) {
                    this.toggleUserMenu();
                } else {
                    this.authManager.redirectToLogin();
                }
            });
        }

        // Cart icon click handler
        const cartIcon = document.querySelector('.fa-shopping-cart');
        if (cartIcon) {
            cartIcon.addEventListener('click', (e) => {
                e.preventDefault();
                this.toggleCartPanel();
            });
        }

        // Add to cart buttons
        document.querySelectorAll('.btn-add-to-cart').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const productId = btn.dataset.productId;
                const product = this.getProductById(productId);
                if (product) {
                    this.cartManager.addToCart(product);
                }
            });
        });

        // Navbar scroll effect
        window.addEventListener('scroll', () => this.navbarManager.handleScroll());

        // Window resize
        window.addEventListener('resize', () => this.handleResize());
    }

    /**
     * Get product by ID
     * @param {number} productId - Product ID
     * @returns {Object} Product object
     */
    getProductById(productId) {
        return this.productDisplay.getAllProducts().find(p => p.id === parseInt(productId));
    }

    /**
     * Toggle user menu
     */
    toggleUserMenu() {
        const accmini = document.querySelector('.accmini');
        if (accmini) {
            accmini.classList.toggle('show');
        }
    }

    /**
     * Toggle cart panel
     */
    toggleCartPanel() {
        const cartPanel = document.querySelector('.cart-panel');
        if (cartPanel) {
            cartPanel.classList.toggle('show');
        }
    }

    /**
     * Handle window resize
     */
    handleResize() {
        // Responsive adjustments
        if (window.innerWidth <= 768) {
            // Mobile adjustments
            this.adjustForMobile();
        } else {
            // Desktop adjustments
            this.adjustForDesktop();
        }
    }

    /**
     * Adjust UI untuk mobile
     */
    adjustForMobile() {
        const navbar = document.querySelector('.navbar-custom');
        if (navbar) {
            navbar.style.height = '60px';
        }
    }

    /**
     * Adjust UI untuk desktop
     */
    adjustForDesktop() {
        const navbar = document.querySelector('.navbar-custom');
        if (navbar) {
            navbar.style.height = 'auto';
        }
    }

    /**
     * Search products
     * @param {string} query - Search query
     * @returns {Array} Search results
     */
    searchProducts(query) {
        return this.productDisplay.searchProducts(query);
    }

    /**
     * Filter products by category
     * @param {string} category - Category name
     * @returns {Array} Filtered products
     */
    filterByCategory(category) {
        return this.productDisplay.getProductsByCategory(category);
    }

    /**
     * Add product to cart
     * @param {Object} product - Product object
     * @param {number} quantity - Quantity
     */
    addToCart(product, quantity = 1) {
        this.cartManager.addToCart(product, quantity);
    }

    /**
     * Get cart items
     * @returns {Array} Cart items
     */
    getCartItems() {
        return this.cartManager.getCartItems();
    }

    /**
     * Get user info
     * @returns {Object} User object
     */
    getUserInfo() {
        return this.authManager.getUser();
    }

    /**
     * Check if user is authenticated
     * @returns {boolean}
     */
    isUserAuthenticated() {
        return this.authManager.isAuthenticated();
    }

    /**
     * Logout user
     */
    logout() {
        this.authManager.logout();
    }

    /**
     * Destroy landing page (cleanup)
     */
    destroy() {
        this.promotionBanner.destroy();
        // Cleanup other resources
    }
}

/**
 * NavbarManager Class
 * Mengelola navbar behavior dan responsiveness
 */
class NavbarManager {
    constructor() {
        this.navbar = document.querySelector('.navbar-custom');
        this.isScrolled = false;
        this.initialize();
    }

    /**
     * Inisialisasi navbar
     */
    initialize() {
        this.setupNavbarItems();
        this.setupScrollEffect();
    }

    /**
     * Setup navbar items
     */
    setupNavbarItems() {
        // Setup navbar icons
        this.setupNavbarIcons();

        // Setup mobile menu toggle
        this.setupMobileMenu();
    }

    /**
     * Setup navbar icons click handlers
     */
    setupNavbarIcons() {
        const icons = document.querySelectorAll('.navbar-icons i');
        icons.forEach(icon => {
            icon.addEventListener('click', (e) => {
                e.preventDefault();
                this.handleIconClick(icon);
            });
        });
    }

    /**
     * Handle navbar icon click
     * @param {Element} icon - Icon element
     */
    handleIconClick(icon) {
        if (icon.classList.contains('fa-search')) {
            this.handleSearch();
        } else if (icon.classList.contains('fa-envelope')) {
            this.handleMessages();
        } else if (icon.classList.contains('fa-bell')) {
            this.handleNotifications();
        }
    }

    /**
     * Handle search
     */
    handleSearch() {
        const searchBox = document.querySelector('.navbar-search');
        if (searchBox) {
            searchBox.focus();
        }
    }

    /**
     * Handle messages
     */
    handleMessages() {
        alert('No new messages');
    }

    /**
     * Handle notifications
     */
    handleNotifications() {
        alert('No new notifications');
    }

    /**
     * Setup mobile menu toggle
     */
    setupMobileMenu() {
        const hamburger = document.querySelector('.navbar-hamburger');
        if (hamburger) {
            hamburger.addEventListener('click', () => {
                this.toggleMobileMenu();
            });
        }
    }

    /**
     * Toggle mobile menu
     */
    toggleMobileMenu() {
        const menu = document.querySelector('.navbar-menu');
        if (menu) {
            menu.classList.toggle('active');
        }
    }

    /**
     * Setup scroll effect
     */
    setupScrollEffect() {
        window.addEventListener('scroll', () => {
            this.handleScroll();
        });
    }

    /**
     * Handle navbar scroll effect
     */
    handleScroll() {
        if (!this.navbar) return;

        if (window.scrollY > 50) {
            if (!this.isScrolled) {
                this.navbar.classList.add('scrolled');
                this.isScrolled = true;
            }
        } else {
            if (this.isScrolled) {
                this.navbar.classList.remove('scrolled');
                this.isScrolled = false;
            }
        }
    }

    /**
     * Update navbar based on user auth
     * @param {Object} user - User object
     */
    updateNavbarForUser(user) {
        if (user) {
            // Show user name
            const userName = document.querySelector('.navbar-username');
            if (userName) {
                userName.textContent = user.name;
            }

            // Show logout button
            const logoutBtn = document.querySelector('.navbar-logout');
            if (logoutBtn) {
                logoutBtn.style.display = 'block';
            }
        }
    }

    /**
     * Close all navbar menus
     */
    closeAllMenus() {
        document.querySelectorAll('.navbar-menu').forEach(menu => {
            menu.classList.remove('active');
        });
    }
}

/**
 * Helper function untuk initialize semuanya saat DOM ready
 */
window.landingPageManager = null;

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded - Initializing Landing Page');

    // Create landing page manager instance
    window.landingPageManager = new LandingPageManager();

    // Close navbar menu saat click di area lain
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.navbar') && !e.target.closest('.navbar-menu')) {
            if (window.landingPageManager) {
                window.landingPageManager.navbarManager.closeAllMenus();
            }
        }
    });

    // Close accmini saat click di area lain
    document.addEventListener('click', (e) => {
        const accmini = document.querySelector('.accmini');
        if (accmini && !e.target.closest('.accmini') && !e.target.closest('.fa-user')) {
            accmini.classList.remove('show');
        }
    });

    console.log('Landing Page fully initialized');
});

// Export untuk testing atau module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        LandingPageManager,
        NavbarManager,
        ProductDisplay,
        PromotionBanner,
        CartManager,
        AuthManager
    };
}
