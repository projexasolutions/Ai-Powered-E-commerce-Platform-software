package com.nextgen.store.catalog;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="products")
public class Product {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false,length=160) private String name;
  @Column(nullable=false,unique=true,length=180) private String slug;
  @Column(nullable=false,length=2000) private String description;
  @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
  @Column(nullable=false,precision=3,scale=2) private BigDecimal rating;
  @Column(name="image_url",nullable=false,length=500) private String imageUrl;
  @Column(nullable=false) private boolean active=true;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="category_id",nullable=false) private Category category;
  protected Product() {}
  public Product(String name,String slug,String description,BigDecimal price,BigDecimal rating,String imageUrl,Category category){this.name=name;this.slug=slug;this.description=description;this.price=price;this.rating=rating;this.imageUrl=imageUrl;this.category=category;}
  public Long getId(){return id;} public String getName(){return name;} public String getSlug(){return slug;} public String getDescription(){return description;} public BigDecimal getPrice(){return price;} public BigDecimal getRating(){return rating;} public String getImageUrl(){return imageUrl;} public Category getCategory(){return category;}
}
