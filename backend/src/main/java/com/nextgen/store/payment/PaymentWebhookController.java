package com.nextgen.store.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextgen.store.order.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/v1/payments/webhooks")
public class PaymentWebhookController {
  private final PaymentRepository payments;
  private final PaymentWebhookEventRepository events;
  private final ObjectMapper mapper;
  private final String secret;

  public PaymentWebhookController(PaymentRepository payments, PaymentWebhookEventRepository events, ObjectMapper mapper,
      @Value("${payment.razorpay.webhook-secret:}") String secret) {
    this.payments=payments; this.events=events; this.mapper=mapper; this.secret=secret;
  }

  @PostMapping("/razorpay")
  @Transactional
  public ResponseEntity<Void> razorpay(@RequestHeader(value="X-Razorpay-Signature",required=false) String signature,
      @RequestHeader(value="x-razorpay-event-id",required=false) String eventId, @RequestBody String rawBody) {
    if(secret.isBlank() || signature==null || !validSignature(rawBody,signature)) return ResponseEntity.badRequest().build();
    if(eventId==null || eventId.isBlank()) return ResponseEntity.badRequest().build();
    if(events.existsByEventId(eventId)) return ResponseEntity.ok().build();
    try {
      JsonNode root=mapper.readTree(rawBody);
      String type=text(root,"event");
      String paymentId=text(root.at("/payload/payment/entity/id"));
      String razorpayOrderId=text(root.at("/payload/payment/entity/order_id"));
      if(razorpayOrderId==null) razorpayOrderId=text(root.at("/payload/order/entity/id"));
      PaymentStatus status=statusFor(type);
      java.util.Optional<Payment> match=paymentId==null?java.util.Optional.empty():payments.findByGatewayPaymentId(paymentId);
      if(match.isEmpty() && razorpayOrderId!=null) match=payments.findByGatewayOrderId(razorpayOrderId);
      if(match.isPresent()) {
        Payment p=match.get(); p.setStatus(status); updateOrder(p,status);
      }
      events.save(new PaymentWebhookEvent(eventId,type==null?"unknown":type));
      return ResponseEntity.ok().build();
    } catch(Exception e) { return ResponseEntity.badRequest().build(); }
  }

  private void updateOrder(Payment payment,PaymentStatus status){
    var order=payment.getOrder();
    switch(status){
      case AUTHORIZED -> order.setPaymentStatus("AUTHORIZED");
      case CAPTURED -> { order.setPaymentStatus("CAPTURED"); order.setStatus(OrderStatus.CONFIRMED); }
      case FAILED -> { order.setPaymentStatus("FAILED"); order.setStatus(OrderStatus.PENDING_PAYMENT); }
      case REFUNDED -> order.setPaymentStatus("REFUNDED");
      default -> { }
    }
  }

  private PaymentStatus statusFor(String event){
    if(event==null)return PaymentStatus.PENDING;
    return switch(event){
      case "payment.captured","order.paid" -> PaymentStatus.CAPTURED;
      case "payment.authorized" -> PaymentStatus.AUTHORIZED;
      case "payment.failed" -> PaymentStatus.FAILED;
      case "refund.processed" -> PaymentStatus.REFUNDED;
      default -> PaymentStatus.PENDING;
    };
  }

  private boolean validSignature(String body,String received){
    try{
      Mac mac=Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
      byte[] digest=mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex=new StringBuilder(digest.length*2);
      for(byte b:digest) hex.append(String.format("%02x",b));
      return MessageDigest.isEqual(hex.toString().getBytes(StandardCharsets.UTF_8),received.getBytes(StandardCharsets.UTF_8));
    }catch(Exception e){return false;}
  }

  private static String text(JsonNode node){return node==null||node.isMissingNode()||node.isNull()?null:node.asText(null);}
  private static String text(JsonNode root,String field){return text(root.path(field));}
}
