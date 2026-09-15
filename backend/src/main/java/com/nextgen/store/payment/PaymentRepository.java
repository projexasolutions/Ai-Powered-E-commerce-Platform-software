package com.nextgen.store.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
  Optional<Payment> findByOrderId(Long orderId);
  Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
}
