// Hidden Admin Access - Triple Click to Login
document.addEventListener("DOMContentLoaded", function () {
    const logoElement = document.querySelector('.logo img');
    if (logoElement) {
        let clickCount = 0;
        let clickTimer;

        logoElement.addEventListener('click', function (e) {
            clickCount++;

            if (clickCount === 1) {
                clickTimer = setTimeout(() => {
                    clickCount = 0;
                }, 1000); // Reset count after 1 second
            }

            if (clickCount >= 3) {
                clearTimeout(clickTimer);
                clickCount = 0;
                // Redirect to hidden admin login
                window.location.href = '/__admin';
            }
        });

        // Change cursor to indicate something might be there (optional, maybe keep it default to be truly hidden)
        // logoElement.style.cursor = 'pointer'; 
    }
});

// Removed: redirectToLogin, redirectToDashboard, handleLogin, handleLogout, checkLoginStatus
// These were insecure client-side implementations. We now use server-side Auth logic.

document.addEventListener("DOMContentLoaded", function () {
    const suggestionForm = document.querySelector('footer form');
    if (suggestionForm) {
        suggestionForm.addEventListener('submit', function (e) {
            const message = this.querySelector('input[name="message"]').value;

            if (message.trim() === '') {
                e.preventDefault();
                alert('Please enter your suggestion');
                return;
            }

            // Don't prevent default - let the form submit to the server!
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Sending...';
            submitBtn.disabled = true;

            // Form will now submit normally to the server
        });
    }

    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (href !== '#' && href !== '') {
                e.preventDefault();
                const targetElement = document.querySelector(href);
                if (targetElement) {
                    targetElement.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });

    document.querySelectorAll('.category-card').forEach(card => {
        card.addEventListener('mouseenter', function () {
            this.style.transform = 'translateY(-10px)';
            this.style.boxShadow = '0 15px 30px rgba(0,0,0,0.15)';
        });

        card.addEventListener('mouseleave', function () {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 8px 25px rgba(0,0,0,0.08)';
        });
    });

    // Testimonial rotation removed by user request
    const testimonials = document.querySelectorAll('.testimonial-card');
    if (testimonials.length > 0) {
        testimonials.forEach(t => t.style.display = 'flex');
    }

    if (window.location.pathname.includes('/admin/')) {
        checkLoginStatus();
    }

    // Category Manual Scroll Buttons
    const scrollContainer = document.getElementById('categoryScroll');
    const scrollLeftBtn = document.getElementById('scrollLeftBtn');
    const scrollRightBtn = document.getElementById('scrollRightBtn');

    if (scrollContainer && scrollLeftBtn && scrollRightBtn) {
        // Function to check and update button visibility
        const updateScrollButtons = () => {
            // Threshold to account for float precision
            const threshold = 5;

            // Hide Left Button if at start
            if (scrollContainer.scrollLeft <= threshold) {
                scrollLeftBtn.style.opacity = '0';
                scrollLeftBtn.style.pointerEvents = 'none';
            } else {
                scrollLeftBtn.style.opacity = '1';
                scrollLeftBtn.style.pointerEvents = 'auto';
            }

            // Hide Right Button if at end
            // scrollWidth - scrollLeft should equal clientWidth at the end
            if (scrollContainer.scrollWidth - scrollContainer.scrollLeft - scrollContainer.clientWidth <= threshold) {
                scrollRightBtn.style.opacity = '0';
                scrollRightBtn.style.pointerEvents = 'none';
            } else {
                scrollRightBtn.style.opacity = '1';
                scrollRightBtn.style.pointerEvents = 'auto';
            }
        };

        // Initial check
        updateScrollButtons();

        // Update on scroll (programmatic scroll triggers this)
        scrollContainer.addEventListener('scroll', updateScrollButtons);

        // Also update after window resize
        window.addEventListener('resize', updateScrollButtons);

        scrollLeftBtn.addEventListener('click', () => {
            scrollContainer.scrollBy({
                left: -400,
                behavior: 'smooth'
            });
            // Fallback check after delay to ensure animation finished
            setTimeout(updateScrollButtons, 500);
        });

        scrollRightBtn.addEventListener('click', () => {
            scrollContainer.scrollBy({
                left: 400,
                behavior: 'smooth'
            });
            setTimeout(updateScrollButtons, 500);
        });
    }
});