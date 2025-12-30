document.addEventListener('DOMContentLoaded', function () {
  // Cek apakah user sudah login
  if (!window.IS_AUTHENTICATED || window.IS_AUTHENTICATED === 'false') {
    // Jika guest, redirect ke login
    window.location.href = '/login';
    return;
  }

  const fieldName = document.getElementById('fieldName');
  const fieldPhone = document.getElementById('fieldPhone');
  const fieldEmail = document.getElementById('fieldEmail');
  const fieldAddress = document.getElementById('fieldAddress');
  const addAddressLink = document.getElementById('addAddressLink');
  const primaryAction = document.getElementById('primaryAction');
  const roleBadge = document.getElementById('roleBadge');
  const userEmoji = document.getElementById('userEmoji');
  const logoutForm = document.getElementById('logoutForm');

  // Fetch user data dari server
  function fetchUserData() {
    fetch('/api/user/profile')
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          fillData(data.user);
        }
      })
      .catch(error => {
        console.error('Error fetching user data:', error);
      });
  }

  function fillData(data = {}) {
    if (fieldName) fieldName.textContent = data.name || '-';
    if (fieldPhone) fieldPhone.textContent = data.phone || '-';
    if (fieldEmail) fieldEmail.textContent = data.email || '-';
    if (fieldAddress) fieldAddress.textContent = data.address || '-';

    // Populate loyalty points
    const fieldLoyaltyPoints = document.getElementById('fieldLoyaltyPoints');
    if (fieldLoyaltyPoints) {
      const pointsValue = data.loyaltyPoints || 0;
      fieldLoyaltyPoints.querySelector('span:last-child').textContent = pointsValue;
    }

    if (userEmoji) {
      userEmoji.textContent = data.role === 'admin' ? '🛠️' : '👩‍🦰';
    }
    if (roleBadge) {
      roleBadge.textContent = data.role === 'admin' ? 'Admin' : 'User';
    }

    // Populate birth date
    const fieldBirthDate = document.getElementById('fieldBirthDate');
    if (fieldBirthDate) {
      fieldBirthDate.textContent = data.birth_date || '-';
    }
  }



  // Fetch data saat halaman load
  fetchUserData();
});