package com.nextgen.store.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/payments/webhooks")
public class PaymentWebhookController {
  private final PaymentRepository payments;
  private final PaymentWebhookEventRepository events;
  private final String secret;

  public PaymentWebhookController(PaymentRepository payments, PaymentWebhookEventRepository events, @Value("${payment.razorpay.webhook-secret:}") String secret) {
    this.payments=payments; this.events=events; this.secret=secret;
  }

  @PostMapping("/razorpay")
  @Transactional
  public ResponseEntity<Void> razorpay(@RequestHeader(value="X-Razorpay-Signature",required=false) String signature,
                                       @RequestHeader(value="x-razorpay-event-id",required=false) String eventId,
                                       @RequestBody String rawBody) {
    if(secret.isBlank() || signature==null || !validSignature(rawBody,signature)) return ResponseEntity.badRequest().build();
    if(eventId==null || eventId.isBlank()) return ResponseEntity.badRequest().build();
    if(events.existsByEventId(eventId)) return ResponseEntity.ok().build();
    String type=extract(rawBody,"event");
    String paymentId=extractNestedPaymentId(rawBody);
    String razorpayOrderId=extractNestedOrderId(rawBody);
    if(paymentId!=null){
      payments.findByGatewayPaymentId(paymentId).ifPresent(p->{p.setStatus(statusFor(type));});
    }
    if(razorpayOrderId!=null){
      payments.findAll().stream().filter(p->razorpayOrderId.equals(p.getGatewayOrderId())).forEach(p->{p.setStatus(statusFor(type));});
    }
    events.save(new PaymentWebhookEvent(eventId,type==null?"unknown":type));
    return ResponseEntity.ok().build();
  }

  private PaymentStatus statusFor(String event){
    if(event==null)return PaymentStatus.PENDING;
    return switch(event){case "payment.captured","order.paid"->PaymentStatus.CAPTURED;case "payment.authorized"->PaymentStatus.AUTHORIZED;case "payment.failed"->PaymentStatus.FAILED;default->PaymentStatus.PENDING;};
  }
  private boolean validSignature(String body,String received){
    try{Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));byte[] digest=mac.doFinal(body.getBytes(StandardCharsets.UTF_8));String expected=Base64.getEncoder().encodeToString(digest);return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),received.getBytes(StandardCharsets.UTF_8));}catch(Exception e){return false;}
  }
  private String extract(String json,String key){String marker="\""+key+"\"";int i=json.indexOf(marker);if(i<0)return null;int c=json.indexOf(':',i+marker.length());if(c<0)return null;int q=json.indexOf('"',c+1);if(q<0)return null;int e=json.indexOf('"',q+1);return e>q?json.substring(q+1,e):null;}
  private String extractNestedPaymentId(String json){return extract(json,"id");}
  private String extractNestedOrderId(String json){int p=json.indexOf("\"order_id\"");if(p<0)return null;return extract(json.substring(p),"order_id");}
}
