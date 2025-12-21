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

document.addEventListener('DOMContentLoaded', function () {
    const addProductForm = document.getElementById('addProductForm');
    /*
        if (addProductForm) {
            addProductForm.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const requiredFields = this.querySelectorAll('[required]');
                let isValid = true;
                
                requiredFields.forEach(field => {
                    if (!field.value.trim()) {
                        field.classList.add('is-invalid');
                        isValid = false;
                    } else {
                        field.classList.remove('is-invalid');
                    }
                });
                
                if (!isValid) {
                    adminHelpers.showNotification('Please fill in all required fields', 'error');
                    return;
                }
                
                const submitBtn = this.querySelector('button[type="submit"]');
                adminHelpers.showLoading(submitBtn, 'Adding...');
                
                setTimeout(() => {
                    adminHelpers.showNotification('Product added successfully!', 'success');
                    
                    adminHelpers.hideLoading(submitBtn);
                    
                    setTimeout(() => {
                        window.location.href = '/admin/products';
                    }, 1000);
                }, 2000);
            });
        }
    */

    const priceInputs = document.querySelectorAll('input[placeholder*="price"]');
    priceInputs.forEach(input => {
        input.addEventListener('blur', function () {
            let value = this.value.replace(/[^\d]/g, '');
            if (value) {
                const formatted = new Intl.NumberFormat('id-ID').format(value);
                this.value = formatted;
            }
        });

        input.addEventListener('focus', function () {
            this.value = this.value.replace(/[^\d]/g, '');
        });
    });

    const stockInputs = document.querySelectorAll('input[type="number"][min="0"]');
    stockInputs.forEach(input => {
        input.addEventListener('change', function () {
            if (parseInt(this.value) < 0) {
                this.value = 0;
            }
        });
    });

    /* 
    const imageUpload = document.querySelector('.image-upload');
    if (imageUpload) {
        imageUpload.addEventListener('click', function () {
            const fileInput = document.getElementById('imageInput');
            if (fileInput) {
                fileInput.click();
            }
        });
    }
    */
});