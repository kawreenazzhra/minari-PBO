document.addEventListener('DOMContentLoaded', function () {
    const houseAddress = document.getElementById('houseAddress');
    const officeAddress = document.getElementById('officeAddress');
    const addNewAddress = document.getElementById('addNewAddress');

    loadSelectedAddress();

    houseAddress.addEventListener('click', function () {
        selectAddress('house');
    });

    officeAddress.addEventListener('click', function () {
        selectAddress('office');
    });

    addNewAddress.addEventListener('click', function () {
        alert('Fitur tambah alamat baru akan segera tersedia!');
    });

    function loadSelectedAddress() {
        const selected = localStorage.getItem('selectedAddress') || 'house';

        document.querySelectorAll('.address-item').forEach(item =>
            item.classList.remove('selected')
        );

        if (selected === 'house') houseAddress.classList.add('selected');
        if (selected === 'office') officeAddress.classList.add('selected');
    }

    function selectAddress(type) {
        localStorage.setItem('selectedAddress', type);
        loadSelectedAddress();
    }
});
