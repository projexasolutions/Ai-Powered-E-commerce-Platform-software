package com.nextgen.store.order;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order,Long> {
  List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
  Optional<Order> findByIdAndUserId(Long id,Long userId);
  Optional<Order> findByUserIdAndIdempotencyKey(Long userId,String idempotencyKey);
}
