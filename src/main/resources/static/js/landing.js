// Hidden Admin Access - Triple Click to Login
document.addEventListener("DOMContentLoaded", function() {
    const logoElement = document.querySelector('.logo img');
    if (logoElement) {
        let clickCount = 0;
        let clickTimer;

        logoElement.addEventListener('click', function(e) {
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

document.addEventListener("DOMContentLoaded", function() {
    const suggestionForm = document.querySelector('footer form');
    if (suggestionForm) {
        suggestionForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const message = this.querySelector('input[name="message"]').value;
            
            if (message.trim() === '') {
                alert('Please enter your suggestion');
                return;
            }
            
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Sending...';
            submitBtn.disabled = true;
            
            setTimeout(() => {
                alert('Thank you for your suggestion!');
                this.reset();
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;
            }, 1500);
        });
    }
    
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
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
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px)';
            this.style.boxShadow = '0 15px 30px rgba(0,0,0,0.15)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 8px 25px rgba(0,0,0,0.08)';
        });
    });
    
    const testimonials = document.querySelectorAll('.testimonial-card');
    if (testimonials.length > 0) {
        let currentTestimonial = 0;
        
        function showTestimonial(index) {
            testimonials.forEach((testimonial, i) => {
                testimonial.style.display = i === index ? 'block' : 'none';
            });
        }
        
        setInterval(() => {
            currentTestimonial = (currentTestimonial + 1) % testimonials.length;
            showTestimonial(currentTestimonial);
        }, 5000);
    }
    
    if (window.location.pathname.includes('/admin/')) {
        checkLoginStatus();
    }
});