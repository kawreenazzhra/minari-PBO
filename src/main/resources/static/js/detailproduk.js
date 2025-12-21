document.addEventListener('DOMContentLoaded', () => {
  const minus = document.getElementById('minus');
  const plus = document.getElementById('plus');
  const count = document.getElementById('count');
  const addBtn = document.querySelector('.add-to-cart');

  let quantity = 0;

  if (!minus || !plus || !count || !addBtn) return;

  minus.addEventListener('click', () => {
    if (quantity > 0) {
      quantity--;
      count.textContent = quantity;
    }
  });

  plus.addEventListener('click', () => {
    quantity++;
    count.textContent = quantity;
  });

  function showToast(message, subtext = '') {
    let box = document.getElementById('miniToast');
    if (!box) {
      box = document.createElement('div');
      box.id = 'miniToast';
      box.className = 'mini-toast';
      box.innerHTML = '<span id="mtMsg"></span><small id="mtSub"></small>';
      document.body.appendChild(box);
    }
    box.querySelector('#mtMsg').textContent = message;
    box.querySelector('#mtSub').textContent = subtext || '';
    box.classList.add('show');
    clearTimeout(box._hideT);
    box._hideT = setTimeout(() => box.classList.remove('show'), 1800);
  }

  addBtn.addEventListener('click', () => {
    const name = document.querySelector('.details h2')?.textContent?.trim() || 'Item';
    const priceText = document.querySelector('.details .price')?.textContent || '';
    const price = Number(priceText.replace(/[^\d]/g, '')) || 0;
    const image = document.querySelector('.product img')?.getAttribute('src') || '';

    const cart = JSON.parse(localStorage.getItem('cartItems') || '[]');
    cart.push({
      id: Date.now().toString(),
      name,
      price,
      image,
      quantity: Math.max(1, quantity)
    });
    localStorage.setItem('cartItems', JSON.stringify(cart));

    showToast(`“${name}” has been added to your cart.`, `Quantity: ${Math.max(1, quantity)}`);
  });

  // start at least 1 item
  quantity = Math.max(1, quantity);
  count.textContent = quantity;
});
