package com.nextgen.store.payment;

import com.nextgen.store.auth.UserRepository;
import com.nextgen.store.order.Order;
import com.nextgen.store.order.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/payments/online")
public class OnlinePaymentController {
  private final PaymentRepository payments;
  private final OrderRepository orders;
  private final UserRepository users;

  public OnlinePaymentController(PaymentRepository payments, OrderRepository orders, UserRepository users) {
    this.payments = payments; this.orders = orders; this.users = users;
  }

  @PostMapping("/prepare")
  public PrepareResponse prepare(Authentication auth, @RequestBody PrepareRequest request) {
    var user = users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NoSuchElementException("User not found"));
    Order order = orders.findByIdAndUserId(request.orderId(), user.getId()).orElseThrow(() -> new NoSuchElementException("Order not found"));
    if (order.getPaymentMethod() != null && !"ONLINE".equals(order.getPaymentMethod())) throw new IllegalArgumentException("Order is not configured for online payment");
    var existing = payments.findByOrderId(order.getId());
    if (existing.isPresent()) return PrepareResponse.from(existing.get());
    Payment payment = payments.save(new Payment(order, PaymentMethod.ONLINE, PaymentStatus.PENDING, order.getTotalAmount(), order.getCurrency()));
    return PrepareResponse.from(payment);
  }

  public record PrepareRequest(Long orderId) {}
  public record PrepareResponse(Long paymentId, Long orderId, String status, String amount, String currency, String gateway) {
    static PrepareResponse from(Payment p) { return new PrepareResponse(p.getId(), p.getOrder().getId(), p.getStatus().name(), p.getAmount().toPlainString(), p.getCurrency(), p.getGateway()); }
  }
}
