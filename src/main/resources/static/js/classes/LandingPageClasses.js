/**
 * ProductDisplay Class
 * Mengelola tampilan dan interaksi product di landing page
 * OOP Concepts: Encapsulation, Single Responsibility
 */
class ProductDisplay {
    constructor() {
        this.products = [];
        this.categories = [];
        this.filterActive = null;
        this.initialize();
    }

    /**
     * Inisialisasi product display
     */
    initialize() {
        this.cacheElements();
        this.setupScrollBehavior();
        this.loadProducts();
        console.log('ProductDisplay initialized');
    }

    /**
     * Cache DOM elements yang diperlukan
     */
    cacheElements() {
        this.elements = {
            styleScroll: document.querySelector('.style-scroll'),
            categoryScroll: document.querySelector('.category-scroll'),
            categoryCards: document.querySelectorAll('.category-card'),
            styleItems: document.querySelectorAll('.style-item')
        };
    }

    /**
     * Load products dari database atau API (simulasi)
     */
    loadProducts() {
        // TODO: Replace dengan API call ke backend
        this.products = [
            { id: 1, name: 'Shirt & Blouse', category: 'shirtblouse', image: 'asset/photo_2025-11-09_17-45-25-removebg-preview.png' },
            { id: 2, name: 'Sweaters, Cardigan, and Fleece', category: 'sweater', image: 'asset/image_2025-11-09_17-47-14.png' },
            { id: 3, name: 'T-shirt and Polo', category: 'tshirt', image: 'asset/image_2025-11-09_17-48-42 (2).png' },
            { id: 4, name: 'Pants', category: 'pants', image: 'asset/image_2025-11-09_17-56-09.png' },
            { id: 5, name: 'Skirt and Dress', category: 'skirt', image: 'asset/image_2025-11-09_17-48-42.png' },
            { id: 6, name: 'Accessories', category: 'accessories', image: 'asset/image_2025-11-09_17-48-42 (3).png' }
        ];
    }

    /**
     * Setup scroll behavior untuk kategori dan style
     */
    setupScrollBehavior() {
        if (!this.elements.styleScroll && !this.elements.categoryScroll) return;

        // Auto scroll dengan mouse movement
        document.querySelectorAll('.style-scroll, .category-scroll').forEach(scrollArea => {
            let scrollInterval;

            scrollArea.addEventListener('mousemove', (e) => {
                const rect = scrollArea.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const center = rect.width / 2;

                clearInterval(scrollInterval);
                scrollInterval = setInterval(() => {
                    const speed = 15;
                    if (x > center + 50) {
                        scrollArea.scrollLeft += speed;
                    } else if (x < center - 50) {
                        scrollArea.scrollLeft -= speed;
                    }
                }, 20);
            });

            scrollArea.addEventListener('mouseleave', () => {
                clearInterval(scrollInterval);
            });
        });
    }

    /**
     * Get product by category
     * @param {string} category - Category name
     * @returns {Array} Filtered products
     */
    getProductsByCategory(category) {
        return this.products.filter(p => p.category === category);
    }

    /**
     * Navigate ke kategori detail
     * @param {string} category - Category identifier
     */
    navigateToCategory(category) {
        window.location.href = `category.html?cat=${category}`;
    }

    /**
     * Get all products
     * @returns {Array} All products
     */
    getAllProducts() {
        return this.products;
    }

    /**
     * Search products
     * @param {string} query - Search query
     * @returns {Array} Search results
     */
    searchProducts(query) {
        const lowerQuery = query.toLowerCase();
        return this.products.filter(p => 
            p.name.toLowerCase().includes(lowerQuery) ||
            p.category.toLowerCase().includes(lowerQuery)
        );
    }

    /**
     * Get categories
     * @returns {Array} Categories list
     */
    getCategories() {
        return [...new Set(this.products.map(p => p.category))];
    }
}

/**
 * PromotionBanner Class
 * Mengelola tampilan dan logika promotion/sale di landing page
 * OOP Concepts: Encapsulation, Single Responsibility
 */
class PromotionBanner {
    constructor() {
        this.promotions = [];
        this.currentPromotion = 0;
        this.autoPlayInterval = null;
        this.initialize();
    }

    /**
     * Inisialisasi promotion banner
     */
    initialize() {
        this.loadPromotions();
        this.setupBannerAutoplay();
        console.log('PromotionBanner initialized');
    }

    /**
     * Load promotions dari database atau API (simulasi)
     */
    loadPromotions() {
        // TODO: Replace dengan API call ke backend
        this.promotions = [
            {
                id: 1,
                title: 'Summer Sale',
                discount: '50%',
                description: 'Get up to 50% off on summer collection',
                image: 'asset/promotion-summer.jpg',
                endDate: '2025-12-31',
                active: true
            },
            {
                id: 2,
                title: 'Flash Sale',
                discount: '30%',
                description: 'Limited time flash sale on selected items',
                image: 'asset/promotion-flash.jpg',
                endDate: '2025-12-25',
                active: true
            }
        ];
    }

    /**
     * Setup auto play untuk banner
     */
    setupBannerAutoplay() {
        if (this.promotions.length === 0) return;

        // Auto rotate setiap 5 detik
        this.autoPlayInterval = setInterval(() => {
            this.nextPromotion();
        }, 5000);
    }

    /**
     * Move ke promosi berikutnya
     */
    nextPromotion() {
        this.currentPromotion = (this.currentPromotion + 1) % this.promotions.length;
        this.updateBannerDisplay();
    }

    /**
     * Move ke promosi sebelumnya
     */
    prevPromotion() {
        this.currentPromotion = (this.currentPromotion - 1 + this.promotions.length) % this.promotions.length;
        this.updateBannerDisplay();
    }

    /**
     * Update banner display
     */
    updateBannerDisplay() {
        const banner = document.querySelector('.promotion-banner');
        if (!banner) return;

        const promo = this.promotions[this.currentPromotion];
        banner.style.backgroundImage = `url('${promo.image}')`;
        // Update other banner elements
    }

    /**
     * Get current promotion
     * @returns {Object} Current promotion
     */
    getCurrentPromotion() {
        return this.promotions[this.currentPromotion];
    }

    /**
     * Get all active promotions
     * @returns {Array} Active promotions
     */
    getActivePromotions() {
        return this.promotions.filter(p => p.active);
    }

    /**
     * Get promotion by ID
     * @param {number} id - Promotion ID
     * @returns {Object} Promotion object
     */
    getPromotionById(id) {
        return this.promotions.find(p => p.id === id);
    }

    /**
     * Stop auto play
     */
    stopAutoPlay() {
        if (this.autoPlayInterval) {
            clearInterval(this.autoPlayInterval);
            this.autoPlayInterval = null;
        }
    }

    /**
     * Destroy promotion banner
     */
    destroy() {
        this.stopAutoPlay();
    }
}

/**
 * CartManager Class
 * Mengelola shopping cart logic
 * OOP Concepts: Encapsulation, State Management
 */
class CartManager {
    constructor() {
        this.cart = [];
        this.cartTotal = 0;
        this.loadCartFromStorage();
        this.setupCartUI();
        console.log('CartManager initialized');
    }

    /**
     * Load cart dari localStorage
     */
    loadCartFromStorage() {
        const savedCart = localStorage.getItem('minari_cart');
        if (savedCart) {
            try {
                this.cart = JSON.parse(savedCart);
                this.calculateTotal();
            } catch (e) {
                console.error('Error loading cart:', e);
                this.cart = [];
            }
        }
    }

    /**
     * Save cart ke localStorage
     */
    saveCartToStorage() {
        localStorage.setItem('minari_cart', JSON.stringify(this.cart));
    }

    /**
     * Add item ke cart
     * @param {Object} product - Product object
     * @param {number} quantity - Quantity
     */
    addToCart(product, quantity = 1) {
        const existingItem = this.cart.find(item => item.id === product.id);

        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            this.cart.push({
                ...product,
                quantity: quantity,
                addedAt: new Date().toISOString()
            });
        }

        this.calculateTotal();
        this.saveCartToStorage();
        this.updateCartUI();
        this.showAddToCartNotification(product.name);
    }

    /**
     * Remove item dari cart
     * @param {number} productId - Product ID
     */
    removeFromCart(productId) {
        this.cart = this.cart.filter(item => item.id !== productId);
        this.calculateTotal();
        this.saveCartToStorage();
        this.updateCartUI();
    }

    /**
     * Update quantity
     * @param {number} productId - Product ID
     * @param {number} quantity - New quantity
     */
    updateQuantity(productId, quantity) {
        const item = this.cart.find(item => item.id === productId);
        if (item) {
            item.quantity = Math.max(1, quantity);
            this.calculateTotal();
            this.saveCartToStorage();
            this.updateCartUI();
        }
    }

    /**
     * Calculate total cart value
     */
    calculateTotal() {
        this.cartTotal = this.cart.reduce((total, item) => {
            return total + (item.price * item.quantity);
        }, 0);
    }

    /**
     * Get cart items
     * @returns {Array} Cart items
     */
    getCartItems() {
        return this.cart;
    }

    /**
     * Get cart total
     * @returns {number} Cart total
     */
    getCartTotal() {
        return this.cartTotal;
    }

    /**
     * Get cart item count
     * @returns {number} Item count
     */
    getCartItemCount() {
        return this.cart.reduce((count, item) => count + item.quantity, 0);
    }

    /**
     * Clear cart
     */
    clearCart() {
        this.cart = [];
        this.cartTotal = 0;
        this.saveCartToStorage();
        this.updateCartUI();
    }

    /**
     * Setup cart UI
     */
    setupCartUI() {
        this.updateCartUI();
    }

    /**
     * Update cart UI display
     */
    updateCartUI() {
        const cartCount = document.querySelector('.cart-count');
        const cartTotal = document.querySelector('.cart-total');

        if (cartCount) {
            cartCount.textContent = this.getCartItemCount();
        }

        if (cartTotal) {
            cartTotal.textContent = `Rp ${this.cartTotal.toLocaleString('id-ID')}`;
        }
    }

    /**
     * Show notification saat add to cart
     * @param {string} productName - Product name
     */
    showAddToCartNotification(productName) {
        const notification = document.createElement('div');
        notification.className = 'cart-notification';
        notification.innerHTML = `
            <i class="fas fa-check-circle"></i>
            <span>${productName} added to cart</span>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.classList.add('show');
        }, 10);

        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 3000);
    }

    /**
     * Proceed to checkout
     */
    proceedToCheckout() {
        if (this.cart.length === 0) {
            alert('Your cart is empty');
            return;
        }
        window.location.href = 'checkout.html';
    }
}

/**
 * AuthManager Class
 * Mengelola authentication dan user navigation
 * OOP Concepts: Encapsulation, State Management
 */
class AuthManager {
    constructor() {
        this.isLoggedIn = false;
        this.userRole = null;
        this.user = null;
        this.initialize();
    }

    /**
     * Inisialisasi auth manager
     */
    initialize() {
        this.checkLoginStatus();
        this.setupAuthUI();
        console.log('AuthManager initialized');
    }

    /**
     * Check login status dari localStorage
     */
    checkLoginStatus() {
        const savedUser = localStorage.getItem('minari_user');
        const adminLoggedIn = localStorage.getItem('adminLoggedIn');

        if (savedUser) {
            try {
                this.user = JSON.parse(savedUser);
                this.isLoggedIn = true;
                this.userRole = this.user.role || 'customer';
            } catch (e) {
                console.error('Error loading user:', e);
                this.logout();
            }
        } else if (adminLoggedIn) {
            this.isLoggedIn = true;
            this.userRole = 'admin';
            this.user = { role: 'admin', name: 'Admin' };
        }
    }

    /**
     * Handle login
     * @param {Object} credentials - Username/email dan password
     * @returns {Promise} Login result
     */
    async login(credentials) {
        try {
            // TODO: Replace dengan actual API call
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(credentials)
            });

            if (response.ok) {
                const data = await response.json();
                this.setUser(data.user);
                return { success: true };
            } else {
                return { success: false, error: 'Invalid credentials' };
            }
        } catch (error) {
            console.error('Login error:', error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Set user setelah login
     * @param {Object} user - User object
     */
    setUser(user) {
        this.user = user;
        this.isLoggedIn = true;
        this.userRole = user.role || 'customer';
        localStorage.setItem('minari_user', JSON.stringify(user));
        this.setupAuthUI();
    }

    /**
     * Handle logout
     */
    logout() {
        this.user = null;
        this.isLoggedIn = false;
        this.userRole = null;
        localStorage.removeItem('minari_user');
        localStorage.removeItem('adminLoggedIn');
        this.setupAuthUI();
    }

    /**
     * Get current user
     * @returns {Object} User object
     */
    getUser() {
        return this.user;
    }

    /**
     * Get user role
     * @returns {string} User role (admin, customer, guest)
     */
    getUserRole() {
        return this.userRole || 'guest';
    }

    /**
     * Setup auth UI
     */
    setupAuthUI() {
        this.updateUserMenu();
    }

    /**
     * Update user menu di navbar
     */
    updateUserMenu() {
        const accmini = document.querySelector('.accmini');
        if (!accmini) return;

        if (this.isLoggedIn) {
            // Show user info
            const userName = accmini.querySelector('.accmini__name');
            if (userName) {
                userName.textContent = this.user.name || 'User';
            }

            // Show logout button
            const btn = accmini.querySelector('.accmini__btn');
            if (btn) {
                btn.textContent = 'Logout';
                btn.onclick = () => this.handleLogoutClick();
            }

            // Hide dashboard link untuk customer
            if (this.userRole === 'customer') {
                const dashboardLink = accmini.querySelector('[href="dashboard.html"]');
                if (dashboardLink) {
                    dashboardLink.style.display = 'none';
                }
            }
        } else {
            // Show login button
            const btn = accmini.querySelector('.accmini__btn');
            if (btn) {
                btn.textContent = 'Login';
                btn.onclick = () => this.redirectToLogin();
            }
        }
    }

    /**
     * Handle logout click
     */
    handleLogoutClick() {
        if (confirm('Are you sure you want to logout?')) {
            this.logout();
            window.location.href = 'index.html';
        }
    }

    /**
     * Redirect ke login
     */
    redirectToLogin() {
        window.location.href = 'login.html';
    }

    /**
     * Redirect ke dashboard (admin only)
     */
    redirectToDashboard() {
        if (this.userRole === 'admin') {
            window.location.href = 'dashboard.html';
        } else {
            alert('Only admins can access dashboard');
        }
    }

    /**
     * Check if user is admin
     * @returns {boolean}
     */
    isAdmin() {
        return this.userRole === 'admin';
    }

    /**
     * Check if user is logged in
     * @returns {boolean}
     */
    isAuthenticated() {
        return this.isLoggedIn;
    }
}
