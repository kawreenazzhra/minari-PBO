// Admin Notification System
// Polls for new order notifications and updates the UI

let notificationCount = 0;

// Update notification badge
function updateNotificationBadge(count) {
    const badge = document.getElementById('notificationBadge');
    if (badge) {
        if (count > 0) {
            badge.textContent = count;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    }
}

// Update notification dropdown content
function updateNotificationDropdown(notifications) {
    const dropdownMenu = document.querySelector('#notificationDropdown + .dropdown-menu');
    if (!dropdownMenu) return;

    // Clear existing content except header
    const header = dropdownMenu.querySelector('.dropdown-header')?.parentElement;
    const divider = dropdownMenu.querySelector('.dropdown-divider')?.parentElement;

    dropdownMenu.innerHTML = '';

    // Re-add header
    if (header) {
        dropdownMenu.appendChild(header);
    }
    if (divider) {
        dropdownMenu.appendChild(divider);
    }

    if (notifications.length === 0) {
        // No notifications
        const emptyItem = document.createElement('li');
        emptyItem.className = 'text-center py-4';
        emptyItem.innerHTML = `
            <i class="far fa-bell-slash text-muted mb-2 fa-lg"></i>
            <p class="text-muted small mb-0">No new notifications</p>
        `;
        dropdownMenu.appendChild(emptyItem);
    } else {
        // Add notifications
        notifications.forEach(notification => {
            const item = document.createElement('li');
            const link = document.createElement('a');
            link.className = 'dropdown-item py-3 border-bottom';
            link.href = `/admin/orders?orderNumber=${notification.orderNumber}`;
            link.innerHTML = `
                <div class="d-flex align-items-start">
                    <div class="flex-shrink-0">
                        <i class="fas fa-shopping-bag text-primary fa-lg"></i>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <h6 class="mb-1 fw-bold" style="font-size: 13px;">New Order #${notification.orderNumber}</h6>
                        <p class="mb-1 text-muted small">${notification.customerName}</p>
                        <p class="mb-0 text-muted small">Rp ${new Intl.NumberFormat('id-ID').format(notification.totalAmount)}</p>
                    </div>
                </div>
            `;
            item.appendChild(link);
            dropdownMenu.appendChild(item);
        });
    }
}

// Fetch notifications from server
async function fetchNotifications() {
    try {
        const response = await fetch('/api/admin/notifications/orders', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            notificationCount = data.count || 0;
            updateNotificationBadge(notificationCount);
            updateNotificationDropdown(data.notifications || []);
        }
    } catch (error) {
        console.error('Error fetching notifications:', error);
    }
}

// Initialize notification system
function initNotifications() {
    // Add notification badge to the bell icon if it doesn't exist
    const notificationIcon = document.getElementById('notificationDropdown');
    if (notificationIcon && !document.getElementById('notificationBadge')) {
        const badge = document.createElement('span');
        badge.id = 'notificationBadge';
        badge.className = 'position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger';
        badge.style.cssText = 'font-size: 10px; padding: 3px 6px; display: none;';
        badge.textContent = '0';
        notificationIcon.appendChild(badge);
    }

    // Fetch initial notifications
    fetchNotifications();

    // Poll for new notifications every 30 seconds
    setInterval(fetchNotifications, 30000);
}

// Initialize on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initNotifications);
} else {
    initNotifications();
}
