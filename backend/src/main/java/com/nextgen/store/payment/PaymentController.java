package com.nextgen.store.payment;

import com.nextgen.store.auth.User;
import com.nextgen.store.auth.UserRepository;
import com.nextgen.store.order.Order;
import com.nextgen.store.order.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
  private final PaymentRepository payments;
  private final OrderRepository orders;
  private final UserRepository users;

  public PaymentController(PaymentRepository payments, OrderRepository orders, UserRepository users) {
    this.payments = payments; this.orders = orders; this.users = users;
  }

  @PostMapping("/cod")
  public PaymentResponse confirmCod(Authentication auth, @RequestBody CodRequest request) {
    User user = users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NoSuchElementException("User not found"));
    Order order = orders.findByIdAndUserId(request.orderId(), user.getId()).orElseThrow(() -> new NoSuchElementException("Order not found"));
    Payment payment = payments.findByOrderId(order.getId()).orElse(null);
    if (payment != null) {
      if (payment.getMethod() != PaymentMethod.COD) throw new IllegalArgumentException("Order already has an online payment");
      return PaymentResponse.from(payment);
    }
    payment = payments.save(new Payment(order, PaymentMethod.COD, PaymentStatus.PENDING, order.getTotalAmount(), order.getCurrency()));
    return PaymentResponse.from(payment);
  }

  public record CodRequest(Long orderId) {}
  public record PaymentResponse(Long id, Long orderId, String method, String status, String amount, String currency) {
    static PaymentResponse from(Payment p) { return new PaymentResponse(p.getId(), p.getOrder().getId(), p.getMethod().name(), p.getStatus().name(), p.getAmount().toPlainString(), p.getCurrency()); }
  }
}
