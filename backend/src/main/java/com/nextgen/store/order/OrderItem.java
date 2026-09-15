package com.nextgen.store.order;

import com.nextgen.store.catalog.ProductVariant;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="order_items")
public class OrderItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id",nullable=false) private Order order;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="variant_id",nullable=false) private ProductVariant variant;
  @Column(name="product_name",nullable=false,length=160) private String productName;
  @Column(nullable=false,length=100) private String sku;
  @Column(nullable=false,length=40) private String size;
  @Column(nullable=false,length=60) private String color;
  @Column(nullable=false) private int quantity;
  @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
  @Column(name="line_total",nullable=false,precision=12,scale=2) private BigDecimal lineTotal;
  protected OrderItem(){}
  public OrderItem(Order order,ProductVariant variant,int quantity,BigDecimal unitPrice){this.order=order;this.variant=variant;this.productName=variant.getProduct().getName();this.sku=variant.getSku();this.size=variant.getSize();this.color=variant.getColor();this.quantity=quantity;this.unitPrice=unitPrice;this.lineTotal=unitPrice.multiply(BigDecimal.valueOf(quantity));}
  public Long getId(){return id;} public String getProductName(){return productName;} public String getSku(){return sku;} public String getSize(){return size;} public String getColor(){return color;} public int getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getLineTotal(){return lineTotal;}
}
