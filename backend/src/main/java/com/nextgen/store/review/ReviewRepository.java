package com.nextgen.store.review;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ReviewRepository extends JpaRepository<Review,Long> {
  Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId,Pageable pageable);
  Optional<Review> findByProductIdAndUserId(Long productId,Long userId);
  @Query("select coalesce(avg(r.rating),0) from Review r where r.product.id=:productId")
  double averageRating(@Param("productId") Long productId);
  long countByProductId(Long productId);
}
