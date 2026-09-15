package com.nextgen.store.order;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order,Long> {
  @EntityGraph(attributePaths="items")
  List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
  @EntityGraph(attributePaths="items")
  Optional<Order> findByIdAndUserId(Long id,Long userId);
  Optional<Order> findByUserIdAndIdempotencyKey(Long userId,String idempotencyKey);
}
