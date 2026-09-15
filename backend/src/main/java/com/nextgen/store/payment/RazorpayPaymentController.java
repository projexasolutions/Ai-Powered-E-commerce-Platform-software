package com.nextgen.store.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.nextgen.store.auth.UserRepository;
import com.nextgen.store.order.OrderRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/payments/razorpay")
public class RazorpayPaymentController {
  private final PaymentRepository payments; private final OrderRepository orders; private final UserRepository users; private final String keyId; private final String keySecret;
  public RazorpayPaymentController(PaymentRepository payments,OrderRepository orders,UserRepository users,@Value("${razorpay.key-id:}") String keyId,@Value("${razorpay.key-secret:}") String keySecret){this.payments=payments;this.orders=orders;this.users=users;this.keyId=keyId;this.keySecret=keySecret;}
  @PostMapping("/create-order")
  public CreateResponse createOrder(Authentication auth,@RequestBody Request request)throws Exception{
    var user=users.findByEmailIgnoreCase(auth.getName()).orElseThrow(()->new NoSuchElementException("User not found"));
    var localOrder=orders.findByIdAndUserId(request.orderId(),user.getId()).orElseThrow(()->new NoSuchElementException("Order not found"));
    if(!"ONLINE".equals(localOrder.getPaymentMethod()))throw new IllegalArgumentException("Order is not configured for online payment");
    if(keyId.isBlank()||keySecret.isBlank())throw new IllegalStateException("Razorpay is not configured");
    Payment payment=payments.findByOrderId(localOrder.getId()).orElseGet(()->payments.save(new Payment(localOrder,PaymentMethod.ONLINE,PaymentStatus.PENDING,localOrder.getTotalAmount(),localOrder.getCurrency())));
    if(payment.getGatewayOrderId()!=null)return new CreateResponse(payment.getId(),localOrder.getId(),keyId,payment.getGatewayOrderId(),localOrder.getTotalAmount().toPlainString(),localOrder.getCurrency());
    RazorpayClient client=new RazorpayClient(keyId,keySecret); JSONObject options=new JSONObject(); options.put("amount",localOrder.getTotalAmount().movePointRight(2).longValueExact()); options.put("currency",localOrder.getCurrency()); options.put("receipt","GENZ-"+localOrder.getId());
    Order razorpayOrder=client.orders.create(options); payment.setGateway("RAZORPAY"); payment.setGatewayOrderId(razorpayOrder.get("id")); payments.save(payment);
    return new CreateResponse(payment.getId(),localOrder.getId(),keyId,razorpayOrder.get("id"),localOrder.getTotalAmount().toPlainString(),localOrder.getCurrency());
  }
  @PostMapping("/verify")
  public VerifyResponse verify(Authentication auth,@RequestBody VerifyRequest request)throws Exception{
    var user=users.findByEmailIgnoreCase(auth.getName()).orElseThrow(()->new NoSuchElementException("User not found")); var localOrder=orders.findByIdAndUserId(request.orderId(),user.getId()).orElseThrow(()->new NoSuchElementException("Order not found")); Payment payment=payments.findByOrderId(localOrder.getId()).orElseThrow(()->new NoSuchElementException("Payment not found"));
    if(payment.getGatewayOrderId()==null||!payment.getGatewayOrderId().equals(request.razorpayOrderId()))throw new IllegalArgumentException("Razorpay order mismatch");
    if(payment.getStatus()==PaymentStatus.CAPTURED)return new VerifyResponse(payment.getId(),localOrder.getId(),payment.getStatus().name());
    JSONObject attributes=new JSONObject(); attributes.put("razorpay_order_id",request.razorpayOrderId()); attributes.put("razorpay_payment_id",request.razorpayPaymentId()); attributes.put("razorpay_signature",request.razorpaySignature());
    if(!Utils.verifyPaymentSignature(attributes,keySecret))throw new IllegalArgumentException("Invalid Razorpay signature");
    payment.setGatewayPaymentId(request.razorpayPaymentId()); payment.transitionTo(PaymentStatus.CAPTURED); payments.save(payment); localOrder.setPaymentStatus("CAPTURED"); localOrder.setStatus(com.nextgen.store.order.OrderStatus.CONFIRMED);
    return new VerifyResponse(payment.getId(),localOrder.getId(),payment.getStatus().name());
  }
  public record Request(Long orderId){} public record CreateResponse(Long paymentId,Long orderId,String keyId,String razorpayOrderId,String amount,String currency){} public record VerifyRequest(Long orderId,String razorpayOrderId,String razorpayPaymentId,String razorpaySignature){} public record VerifyResponse(Long paymentId,Long orderId,String status){}
}
