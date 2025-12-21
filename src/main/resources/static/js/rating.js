document.addEventListener('DOMContentLoaded', () => {

    // Star Click Handler (Delegation)
    document.body.addEventListener('click', (e) => {
        if (e.target.classList.contains('star')) {
            const star = e.target;
            const container = star.closest('.stars');
            const productId = container.dataset.productId;
            const value = parseInt(star.dataset.value);

            // Update UI
            updateStars(container, value);

            // Update Hidden Input for this product
            const input = document.getElementById(`rating-value-${productId}`);
            if (input) input.value = value;
        }
    });

    function updateStars(container, value) {
        const stars = container.querySelectorAll('.star');
        stars.forEach(star => {
            const starValue = parseInt(star.dataset.value);
            if (starValue <= value) {
                star.classList.add('active');
                star.style.color = ''; // clear inline style if any
            } else {
                star.classList.remove('active');
                star.style.color = ''; // clear inline style if any
            }
        });
    }

    // Making this global so the onclick in Blade works
    window.submitProductReview = async function (orderId, productId) {
        const ratingInput = document.getElementById(`rating-value-${productId}`);
        const commentInput = document.getElementById(`comment-${productId}`);
        const hideInput = document.getElementById(`hide-user-${productId}`);

        const rating = parseInt(ratingInput.value);
        const comment = commentInput.value.trim();
        const isAnonymous = hideInput.checked;

        if (rating === 0) {
            showToast('Please select a star rating first.', 'error');
            return;
        }

        if (comment === '') {
            showToast('Please write a comment.', 'error');
            return;
        }

        const submitBtn = document.querySelector(`#review-box-${productId} .btn-submit`);
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = "Sending...";

        try {
            const response = await fetch('/api/submit-review', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').content,
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    order_id: orderId,
                    product_id: productId,
                    rating: rating,
                    comment: comment,
                    is_anonymous: isAnonymous
                })
            });

            const result = await response.json();

            if (result.success) {
                showToast('Review submitted successfully!', 'success');
                // Reload or update UI to show "Reviewed"
                setTimeout(() => location.reload(), 1000); // Delay refresh to see toast
            } else {
                showToast(result.message || 'Failed to submit review.', 'error');
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        } catch (error) {
            console.error('Error:', error);
            showToast('An error occurred. Please try again.', 'error');
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    };

});
