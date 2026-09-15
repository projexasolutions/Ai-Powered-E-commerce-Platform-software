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
  @Column(name="total_amount",nullable=false,precision=12,scale=2) private BigDecimal totalAmount;
  @Column(nullable=false,length=3) private String currency="INR";
  @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
  @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
  @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) private List<OrderItem> items=new ArrayList<>();
  protected Order(){}
  public Order(User user,Address address,BigDecimal subtotal,BigDecimal shipping,BigDecimal total){this.user=user;this.address=address;this.status=OrderStatus.PENDING_PAYMENT;this.subtotal=subtotal;this.shippingAmount=shipping;this.totalAmount=total;}
  @PrePersist void onCreate(){var now=OffsetDateTime.now();createdAt=now;updatedAt=now;}
  @PreUpdate void onUpdate(){updatedAt=OffsetDateTime.now();}
  public Long getId(){return id;} public User getUser(){return user;} public Address getAddress(){return address;} public OrderStatus getStatus(){return status;} public BigDecimal getSubtotal(){return subtotal;} public BigDecimal getShippingAmount(){return shippingAmount;} public BigDecimal getTotalAmount(){return totalAmount;} public String getCurrency(){return currency;} public OffsetDateTime getCreatedAt(){return createdAt;} public List<OrderItem> getItems(){return items;}
}
