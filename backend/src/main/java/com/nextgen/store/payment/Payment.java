package com.nextgen.store.payment;

import com.nextgen.store.order.Order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name="payments")
public class Payment {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id",nullable=false) private Order order;
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private PaymentMethod method;
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private PaymentStatus status;
  @Column(length=40) private String gateway;
  @Column(name="gateway_order_id",length=160) private String gatewayOrderId;
  @Column(name="gateway_payment_id",length=160,unique=true) private String gatewayPaymentId;
  @Column(nullable=false,precision=12,scale=2) private BigDecimal amount;
  @Column(nullable=false,length=3) private String currency="INR";
  @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
  @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
  protected Payment(){}
  public Payment(Order order,PaymentMethod method,PaymentStatus status,BigDecimal amount,String currency){this.order=order;this.method=method;this.status=status;this.amount=amount;this.currency=currency;}
  @PrePersist @PreUpdate void touch(){var now=OffsetDateTime.now();if(createdAt==null)createdAt=now;updatedAt=now;}
  public Long getId(){return id;} public Order getOrder(){return order;} public PaymentMethod getMethod(){return method;} public PaymentStatus getStatus(){return status;} public BigDecimal getAmount(){return amount;} public String getCurrency(){return currency;} public String getGateway(){return gateway;} public String getGatewayOrderId(){return gatewayOrderId;} public String getGatewayPaymentId(){return gatewayPaymentId;}
  public void setGateway(String value){gateway=value;} public void setGatewayOrderId(String value){gatewayOrderId=value;} public void setGatewayPaymentId(String value){gatewayPaymentId=value;} public void setStatus(PaymentStatus value){status=value;}
}
