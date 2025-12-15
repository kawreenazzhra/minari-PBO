// auth.js - untuk halaman register
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerForm');
    const errorsEl = document.getElementById('formErrors');
    const eyeBtn = document.querySelector('.toggle-pass');
    const passInput = document.getElementById('password');

    // Toggle show/hide password
    if (eyeBtn && passInput) {
        eyeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            const isPass = passInput.type === 'password';
            passInput.type = isPass ? 'text' : 'password';
            eyeBtn.textContent = isPass ? '🙈' : '👁️';
        });
    }

    // Helpers
    const isEmail = (v) => /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v);
    const isUsername = (v) => /^[a-zA-Z0-9_.]{3,20}$/.test(v);
    const isPhone = (v) => /^[+0-9][0-9\s\-]{7,16}$/.test(v);
    const getAge = (s) => {
        const b = new Date(s);
        if (isNaN(b)) return 0;
        const diff = Date.now() - b.getTime();
        return Math.abs(new Date(diff).getUTCFullYear() - 1970);
    };
    const showErrors = (list) => {
        errorsEl.innerHTML = list.length
            ? '<ul class="mb-0 ps-3">' + list.map(e => `<li>${e}</li>`).join('') + '</ul>'
            : '';
    };

    // Client-side validation sebelum submit
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        e.stopPropagation();

        const name = document.getElementById('name').value.trim();
        const username = document.getElementById('username').value.trim();
        const phone = document.getElementById('phone').value.trim();
        const email = document.getElementById('email').value.trim();
        const birth = document.getElementById('birth_date').value;
        const address = document.getElementById('address').value.trim();
        const password = passInput.value;
        const passwordConfirm = document.getElementById('password_confirmation').value;

        const errs = [];
        
        if (name.length < 2) errs.push('Name must be at least 2 characters.');
        if (!isUsername(username)) errs.push('Username must be 3–20 chars (letters, numbers, underscore, dot).');
        if (!isPhone(phone)) errs.push('Phone number format is invalid.');
        if (!isEmail(email)) errs.push('Email is invalid.');
        if (!birth) errs.push('Birthday is required.');
        else if (getAge(birth) < 13) errs.push('You must be at least 13 years old.');
        if (address.length < 5) errs.push('Address must be at least 5 characters.');
        if (password.length < 6) errs.push('Password must be at least 6 characters.');
        if (password !== passwordConfirm) errs.push('Passwords do not match.');

        showErrors(errs);
        
        // Jika ada error, stop submission
        if (errs.length > 0) {
            return;
        }
        
        // Jika validasi client-side berhasil, submit form
        form.submit();
    });

    // Validasi real-time
    const inputs = form.querySelectorAll('input, textarea');
    inputs.forEach(input => {
        input.addEventListener('blur', () => {
            const errs = [];
            const value = input.value.trim();
            
            switch(input.id) {
                case 'name':
                    if (value.length < 2) errs.push('Name must be at least 2 characters.');
                    break;
                case 'username':
                    if (!isUsername(value)) errs.push('Invalid username format.');
                    break;
                case 'phone':
                    if (!isPhone(value)) errs.push('Invalid phone number.');
                    break;
                case 'email':
                    if (!isEmail(value)) errs.push('Invalid email format.');
                    break;
                case 'password':
                    if (value.length < 6) errs.push('Password must be at least 6 characters.');
                    break;
                case 'password_confirmation':
                    const password = document.getElementById('password').value;
                    if (value !== password) errs.push('Passwords do not match.');
                    break;
            }
            
            // Tampilkan error untuk field ini
            const fieldError = document.getElementById(`${input.id}-error`);
            if (fieldError) {
                fieldError.textContent = errs.length > 0 ? errs[0] : '';
            }
        });
    });
});