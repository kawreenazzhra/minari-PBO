// Landing Page Scripts
document.addEventListener('DOMContentLoaded', function () {
    console.log('Landing page scripts loaded');

    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });

    // Copy promo code logic (already inline but good to have here)
    window.copyPromoCode = function (btn) {
        const code = btn.getAttribute('data-code');
        navigator.clipboard.writeText(code).then(() => {
            const originalText = btn.innerText;
            btn.innerText = 'Copied!';
            setTimeout(() => btn.innerText = originalText, 2000);
        });
    };
});