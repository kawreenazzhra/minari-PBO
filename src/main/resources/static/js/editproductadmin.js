function previewImage(event) {
    const input = event.target;
    const preview = document.getElementById('imagePreview');

    if (input.files && input.files[0]) {
        const reader = new FileReader();

        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        }

        reader.readAsDataURL(input.files[0]);
    }
}

function confirmDelete() {
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    deleteModal.show();
}

function deleteProduct() {
    const deleteBtn = document.querySelector('#deleteModal .btn-danger');
    adminHelpers.showLoading(deleteBtn, 'Deleting...');

    setTimeout(() => {
        adminHelpers.showNotification('Product deleted successfully!', 'success');
        setTimeout(() => {
            window.location.href = '/admin/products';
        }, 1000);
    }, 1500);
}

document.addEventListener('DOMContentLoaded', function () {
    /*
        const editProductForm = document.getElementById('editProductForm');
        if (editProductForm) {
            // Form now uses standard submission
        }
    */

    const priceInput = document.querySelector('input[type="text"][value*="."]');
    if (priceInput) {
        priceInput.addEventListener('blur', function () {
            let value = this.value.replace(/[^\d]/g, '');
            if (value) {
                const formatted = new Intl.NumberFormat('id-ID').format(value);
                this.value = formatted;
            }
        });

        priceInput.addEventListener('focus', function () {
            this.value = this.value.replace(/[^\d]/g, '');
        });
    }
});