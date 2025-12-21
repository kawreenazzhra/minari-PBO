document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    
    if (form) {
        form.addEventListener('submit', (e) => {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();
            
            if (username === '' || password === '') {
                e.preventDefault();
                alert('Please fill in all fields');
                return false;
            }
            
            if (username === 'admin' && password === 'admin') {
                localStorage.setItem('role', 'admin');
                localStorage.setItem('adminLoggedIn', 'true');
                return true;
            }
            
            return true;
        });
    }
    
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.style.borderColor = '#FBAF99';
        });
        
        input.addEventListener('blur', function() {
            this.parentElement.style.borderColor = '#FFE5DD';
            
            if (this.value.trim() === '' && this.hasAttribute('required')) {
                this.style.borderColor = '#dc3545';
            }
        });
    });
    
    document.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.target.matches('textarea, button')) {
            const submitBtn = document.querySelector('.btn-login');
            if (submitBtn) {
                submitBtn.click();
            }
        }
    });
    
    const submitBtn = document.querySelector('.btn-login');
    if (submitBtn) {
        const originalText = submitBtn.innerHTML;
        
        submitBtn.addEventListener('click', function() {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();
            
            if (username && password) {
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Signing in...';
                this.disabled = true;
                
                setTimeout(() => {
                    this.innerHTML = originalText;
                    this.disabled = false;
                }, 2000);
            }
        });
    }
    
    const usernameInput = document.getElementById('username');
    if (usernameInput) {
        setTimeout(() => {
            usernameInput.focus();
        }, 100);
    }
});