

document.addEventListener('DOMContentLoaded', function () {
    function initializeSidebar() {
        const sidebarItems = document.querySelectorAll('.sidebar-item');
        if (sidebarItems.length > 0) {
            // Server-side blade template handles active state now.
            // Client-side logic removed to prevent conflicts.

            sidebarItems.forEach(item => {
                item.addEventListener('click', function (e) {
                    if (this.getAttribute('href') === '#') {
                        e.preventDefault();
                    }
                });
            });
        }
    }

    function initializeSearch() {
        const searchBoxes = document.querySelectorAll('.search-box');
        searchBoxes.forEach(box => {
            box.addEventListener('keypress', function (e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    performSearch(this.value);
                }
            });

            box.addEventListener('input', function () {
                if (this.value.length > 0) {
                    console.log('Searching for:', this.value);
                }
            });
        });
    }

    function performSearch(query) {
        if (query.trim() !== '') {
            alert('Search functionality would search for: ' + query);
        }
    }

    function initializeFilters() {
        const filterButtons = document.querySelectorAll('.filter-btn');
        filterButtons.forEach(button => {
            button.addEventListener('click', function () {
                const container = this.closest('.filter-section') || this.closest('.d-flex');
                if (container) {
                    const allButtons = container.querySelectorAll('.filter-btn');
                    allButtons.forEach(btn => btn.classList.remove('active'));
                }

                this.classList.add('active');

                const filterValue = this.textContent || this.getAttribute('data-filter');
                console.log('Filter applied:', filterValue);
            });
        });
    }

    function initializeActionIcons() {
        const actionIcons = document.querySelectorAll('.action-icons i, .action-icons a');
        actionIcons.forEach(icon => {
            icon.addEventListener('click', function (e) {
                const action = this.getAttribute('title') ||
                    this.classList.contains('fa-edit') ? 'Edit' :
                    this.classList.contains('fa-trash') ? 'Delete' :
                        this.classList.contains('fa-eye') ? 'View' : 'Action';

                console.log(`${action} action triggered`);

                // Global delete handler removed to allow specific page logic
                /*
                if (action === 'Delete' || this.classList.contains('fa-trash')) {
                    e.preventDefault();
                    // ... (rest of the removed logic)
                }
                */
            });
        });
    }

    function initializeForms() {
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            form.addEventListener('submit', function (e) {
                if (!this.checkValidity()) {
                    e.preventDefault();
                    e.stopPropagation();

                    const invalidFields = this.querySelectorAll(':invalid');
                    if (invalidFields.length > 0) {
                        invalidFields[0].focus();
                    }
                } else {
                    const submitBtn = this.querySelector('button[type="submit"]');
                    if (submitBtn) {
                        const originalText = submitBtn.innerHTML;
                        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
                        submitBtn.disabled = true;

                        setTimeout(() => {
                            submitBtn.innerHTML = originalText;
                            submitBtn.disabled = false;
                        }, 1500);
                    }
                }
            });
        });
    }

    function initializeNotifications() {
        const messageIcons = document.querySelectorAll('.fa-envelope');
        messageIcons.forEach(icon => {
            icon.addEventListener('click', function () {
                const badge = this.querySelector('.notification-badge') ||
                    document.querySelector('.notification-count');

                if (badge) {
                    const count = parseInt(badge.textContent) || 0;
                    if (count > 0) {
                        badge.textContent = '0';
                        badge.style.display = 'none';
                        alert('All messages marked as read!');
                    } else {
                        alert('No new messages');
                    }
                } else {
                    alert('Messages would open here');
                }
            });
        });
    }

    function initializeResponsive() {
        const sidebar = document.querySelector('.sidebar');
        if (sidebar && window.innerWidth <= 768) {
            sidebar.style.display = 'none';
            const toggleBtn = document.createElement('button');
            toggleBtn.innerHTML = '<i class="fas fa-bars"></i>';
            toggleBtn.className = 'sidebar-toggle btn btn-light position-fixed';
            toggleBtn.style.cssText = 'top: 70px; left: 15px; z-index: 1001; border-radius: 50%; width: 40px; height: 40px;';

            document.body.appendChild(toggleBtn);

            toggleBtn.addEventListener('click', function () {
                if (sidebar.style.display === 'none') {
                    sidebar.style.display = 'block';
                    sidebar.style.position = 'fixed';
                    sidebar.style.top = '80px';
                    sidebar.style.left = '15px';
                    sidebar.style.zIndex = '1000';
                    sidebar.style.background = '#FFF6F0';
                    sidebar.style.padding = '15px';
                    sidebar.style.borderRadius = '15px';
                    sidebar.style.boxShadow = '0 4px 15px rgba(0,0,0,0.1)';
                } else {
                    sidebar.style.display = 'none';
                }
            });

            document.addEventListener('click', function (e) {
                if (window.innerWidth <= 768 &&
                    !sidebar.contains(e.target) &&
                    !toggleBtn.contains(e.target) &&
                    sidebar.style.display === 'block') {
                    sidebar.style.display = 'none';
                }
            });
        }
    }

    function initializePrint() {
        const printButtons = document.querySelectorAll('.btn-cancel[onclick*="print"], .btn-cancel:contains("Print")');
        printButtons.forEach(btn => {
            btn.addEventListener('click', function (e) {
                if (this.getAttribute('onclick') && this.getAttribute('onclick').includes('print')) {
                    return;
                }
                e.preventDefault();
                window.print();
            });
        });
    }

    function initializeScroll() {
        const navbar = document.querySelector('.navbar-custom');
        if (navbar) {
            window.addEventListener('scroll', function () {
                if (window.scrollY > 50) {
                    navbar.style.background = 'transparent';
                    navbar.style.boxShadow = 'none';
                } else {
                    navbar.style.background = '#FFF6F0';
                    navbar.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
                }
            });
        }
    }

    function initializeAll() {
        initializeSidebar();
        initializeSearch();
        initializeFilters();
        initializeActionIcons();
        initializeForms();
        initializeNotifications();
        initializeResponsive();
        initializePrint();
        initializeScroll();
    }

    initializeAll();

    let resizeTimer;
    window.addEventListener('resize', function () {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function () {
            initializeResponsive();
        }, 250);
    });
});

window.adminHelpers = {
    showNotification: function (message, type = 'success') {
        const existingNotifications = document.querySelectorAll('.custom-notification');
        existingNotifications.forEach(notification => notification.remove());

        const notification = document.createElement('div');
        notification.className = `custom-notification alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show`;
        notification.style.cssText = `
            position: fixed;
            top: 80px;
            right: 20px;
            z-index: 9999;
            min-width: 300px;
            border-radius: 10px;
            border: none;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        `;

        const icons = {
            success: 'fa-check-circle',
            error: 'fa-exclamation-circle',
            warning: 'fa-exclamation-triangle',
            info: 'fa-info-circle'
        };

        notification.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="fas ${icons[type] || icons.info} me-2"></i>
                <span>${message}</span>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 4000);
    },

    confirmAction: function (message, callback) {
        if (confirm(message)) {
            if (typeof callback === 'function') {
                callback();
            }
            return true;
        }
        return false;
    },

    showLoading: function (selector, text = 'Loading...') {
        const element = typeof selector === 'string' ?
            document.querySelector(selector) : selector;

        if (element) {
            element.dataset.originalHTML = element.innerHTML;
            element.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ${text}`;
            element.disabled = true;
        }
    },

    hideLoading: function (selector) {
        const element = typeof selector === 'string' ?
            document.querySelector(selector) : selector;

        if (element && element.dataset.originalHTML) {
            element.innerHTML = element.dataset.originalHTML;
            element.disabled = false;
        }
    }
};