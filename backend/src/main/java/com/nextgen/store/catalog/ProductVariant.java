package com.nextgen.store.catalog;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name="product_variants",uniqueConstraints=@UniqueConstraint(name="uq_product_variant",columnNames={"product_id","size","color"}))
public class ProductVariant {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Version @Column(nullable=false) private long version;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
  @Column(nullable=false,unique=true,length=100) private String sku;
  @Column(nullable=false,length=40) private String size;
  @Column(nullable=false,length=60) private String color;
  @Column(name="stock_quantity",nullable=false) private int stockQuantity;
  @Column(name="price_override",precision=12,scale=2) private BigDecimal priceOverride;
  @Column(nullable=false) private boolean active=true;
  @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
  protected ProductVariant(){}
  public Long getId(){return id;} public long getVersion(){return version;} public Product getProduct(){return product;} public String getSku(){return sku;} public String getSize(){return size;} public String getColor(){return color;} public int getStockQuantity(){return stockQuantity;} public BigDecimal getPriceOverride(){return priceOverride;} public boolean isActive(){return active;}
  public void decreaseStock(int quantity){if(quantity<1||stockQuantity<quantity)throw new IllegalArgumentException("Insufficient stock for "+sku);stockQuantity-=quantity;}
}
