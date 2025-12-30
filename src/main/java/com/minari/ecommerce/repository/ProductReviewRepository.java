package com.minari.ecommerce.repository;

import com.minari.ecommerce.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"customer"})
    List<ProductReview> findByProductId(Long productId);

    List<ProductReview> findByCustomerId(Long customerId);

    List<ProductReview> findByReviewTextContainingIgnoreCase(String query);

    List<ProductReview> findTop3ByOrderByReviewDateDesc();
}
