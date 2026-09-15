package com.nextgen.store.payment;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name="payment_webhook_events")
public class PaymentWebhookEvent {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="event_id",nullable=false,unique=true,length=160) private String eventId;
  @Column(name="event_type",nullable=false,length=100) private String eventType;
  @Column(name="processed_at",nullable=false) private OffsetDateTime processedAt;
  protected PaymentWebhookEvent(){}
  public PaymentWebhookEvent(String eventId,String eventType){this.eventId=eventId;this.eventType=eventType;this.processedAt=OffsetDateTime.now();}
}
