package com.nextgen.store.order;

import com.nextgen.store.address.Address;
import com.nextgen.store.auth.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name="orders")
public class Order {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private User user;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="address_id") private Address address;
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private OrderStatus status;
  @Column(nullable=false,precision=12,scale=2) private BigDecimal subtotal;
  @Column(name="shipping_amount",nullable=false,precision=12,scale=2) private BigDecimal shippingAmount;
  @Column(name="delivery_charge",nullable=false,precision=12,scale=2) private BigDecimal deliveryCharge;
  @Column(name="total_amount",nullable=false,precision=12,scale=2) private BigDecimal totalAmount;
  @Column(name="payment_method",nullable=false,length=30) private String paymentMethod="COD";
  @Column(name="payment_status",nullable=false,length=30) private String paymentStatus="PENDING";
  @Column(nullable=false,length=3) private String currency="INR";
  @Column(name="idempotency_key",length=160,unique=true) private String idempotencyKey;
  @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
  @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
  @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) private List<OrderItem> items=new ArrayList<>();
  protected Order(){}
  public Order(User user,Address address,BigDecimal subtotal,BigDecimal deliveryCharge,BigDecimal total,String paymentMethod,String idempotencyKey){this.user=user;this.address=address;this.status="COD".equals(paymentMethod)?OrderStatus.CONFIRMED:OrderStatus.PENDING_PAYMENT;this.subtotal=subtotal;this.shippingAmount=deliveryCharge;this.deliveryCharge=deliveryCharge;this.totalAmount=total;this.paymentMethod=paymentMethod;this.idempotencyKey=idempotencyKey;}
  @PrePersist void onCreate(){var now=OffsetDateTime.now();createdAt=now;updatedAt=now;}
  @PreUpdate void onUpdate(){updatedAt=OffsetDateTime.now();}
  public Long getId(){return id;} public User getUser(){return user;} public Address getAddress(){return address;} public OrderStatus getStatus(){return status;} public BigDecimal getSubtotal(){return subtotal;} public BigDecimal getShippingAmount(){return shippingAmount;} public BigDecimal getDeliveryCharge(){return deliveryCharge;} public BigDecimal getTotalAmount(){return totalAmount;} public String getPaymentMethod(){return paymentMethod;} public String getPaymentStatus(){return paymentStatus;} public String getCurrency(){return currency;} public OffsetDateTime getCreatedAt(){return createdAt;} public List<OrderItem> getItems(){return items;} public String getIdempotencyKey(){return idempotencyKey;}
  public void setStatus(OrderStatus status){this.status=status;} public void setPaymentStatus(String paymentStatus){this.paymentStatus=paymentStatus;}
}
