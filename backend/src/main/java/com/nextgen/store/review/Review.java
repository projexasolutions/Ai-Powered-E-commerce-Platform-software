package com.nextgen.store.review;

import com.nextgen.store.auth.User; import com.nextgen.store.catalog.Product; import jakarta.persistence.*; import java.time.OffsetDateTime;

@Entity @Table(name="reviews",uniqueConstraints=@UniqueConstraint(name="uq_review_product_user",columnNames={"product_id","user_id"}))
public class Review {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private User user; @Column(nullable=false) private int rating; @Column(length=160) private String title; @Column(length=2000) private String body; @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private ReviewStatus status=ReviewStatus.APPROVED; @Column(name="created_at",nullable=false) private OffsetDateTime createdAt; @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
  protected Review(){} public Review(Product product,User user,int rating,String title,String body){this.product=product;this.user=user;this.rating=rating;this.title=title;this.body=body;}
  @PrePersist void onCreate(){var now=OffsetDateTime.now();createdAt=now;updatedAt=now;} @PreUpdate void onUpdate(){updatedAt=OffsetDateTime.now();}
  public Long getId(){return id;} public Product getProduct(){return product;} public User getUser(){return user;} public int getRating(){return rating;} public String getTitle(){return title;} public String getBody(){return body;} public ReviewStatus getStatus(){return status;} public OffsetDateTime getCreatedAt(){return createdAt;} public OffsetDateTime getUpdatedAt(){return updatedAt;} public void setStatus(ReviewStatus v){status=v;}
}
