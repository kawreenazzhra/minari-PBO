// login.js

document.addEventListener('DOMContentLoaded', () => {
  const form   = document.getElementById('loginForm');
  const userEl = document.getElementById('username');
  const passEl = document.getElementById('password');
  const errEl  = document.getElementById('loginError'); // <small> opsional untuk error

  // Kalau sudah login, opsional: langsung lempar ke landing
  try {
    const roleNow = NavbarRole.getRole?.();
    if (roleNow && roleNow !== NavbarRole.Role.GUEST) {
      // Sudah logged in
      // location.href = 'landing.html';  // aktifkan jika mau auto-redirect
    }
  } catch (e) {}

  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const u = userEl.value.trim();
    const p = passEl.value.trim();

    // Kredensial contoh (hard-coded)
    let role = null;
    if (u === 'admin' && p === 'admin') {
      role = NavbarRole.Role.ADMIN;
    } else if (u === 'user' && p === 'user') {
      role = NavbarRole.Role.USER;
    }

    if (!role) {
      if (errEl) {
        errEl.textContent = 'Username atau password salah. Coba: user/user atau admin/admin';
        errEl.style.display = 'block';
      } else {
        alert('Username atau password salah. Coba: user/user atau admin/admin');
      }
      return;
    }

    // Set role → navbar akan mengikuti
    NavbarRole.setRole(role);

    // Bersihkan flag lama yang mungkin masih ada dari script lama
    localStorage.removeItem('adminLoggedIn');

    // Opsional simpan nama user
    localStorage.setItem('displayName', role === NavbarRole.Role.ADMIN ? 'Admin' : 'User');

    // Arahkan ke landing (navbar akan auto-render sesuai role)
    if (role === NavbarRole.Role.ADMIN) {
      location.href = 'dashboardadmin.html'; // ganti dengan nama file dashboard admin kamu
    } else {
      location.href = 'landing.html'; // user biasa tetap ke landing
    }

    if (!localStorage.getItem('userData')) {
      localStorage.setItem('userData', JSON.stringify({
        name: 'Aliyah Rahma',
        phone: '08123456789',
        email: 'aliyah@gmail.com',
        birth: '2004-06-15',
        address: 'Jl. Mawar No. 10, Bandung'
      }));
    }

  });
});

