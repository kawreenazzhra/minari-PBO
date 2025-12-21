package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProductId(Long productId);

    List<ProductReview> findByCustomerId(Long customerId);

    List<ProductReview> findByProductIdAndIsApprovedOrderByReviewDateDesc(Long productId, Boolean isApproved);

    List<ProductReview> findByProductIdOrderByReviewDateDesc(Long productId);

    List<ProductReview> findByReviewTextContainingIgnoreCase(String query);
}
