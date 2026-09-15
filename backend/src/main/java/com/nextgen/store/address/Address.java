package com.nextgen.store.address;

import com.nextgen.store.auth.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name="addresses")
public class Address {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private User user;
  @Column(name="full_name",nullable=false,length=160) private String fullName;
  @Column(nullable=false,length=30) private String phone;
  @Column(nullable=false,length=200) private String line1;
  @Column(length=200) private String line2;
  @Column(nullable=false,length=100) private String city;
  @Column(nullable=false,length=100) private String state;
  @Column(name="postal_code",nullable=false,length=20) private String postalCode;
  @Column(nullable=false,length=100) private String country="India";
  @Column(name="is_default",nullable=false) private boolean defaultAddress;
  @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
  @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
  protected Address(){}
  public Address(User user,String fullName,String phone,String line1,String line2,String city,String state,String postalCode,String country,boolean defaultAddress){this.user=user;this.fullName=fullName;this.phone=phone;this.line1=line1;this.line2=line2;this.city=city;this.state=state;this.postalCode=postalCode;this.country=country;this.defaultAddress=defaultAddress;}
  public void update(String fullName,String phone,String line1,String line2,String city,String state,String postalCode,String country,boolean defaultAddress){this.fullName=fullName;this.phone=phone;this.line1=line1;this.line2=line2;this.city=city;this.state=state;this.postalCode=postalCode;this.country=country;this.defaultAddress=defaultAddress;}
  @PrePersist void onCreate(){var now=OffsetDateTime.now();createdAt=now;updatedAt=now;}
  @PreUpdate void onUpdate(){updatedAt=OffsetDateTime.now();}
  public Long getId(){return id;} public User getUser(){return user;} public String getFullName(){return fullName;} public String getPhone(){return phone;} public String getLine1(){return line1;} public String getLine2(){return line2;} public String getCity(){return city;} public String getState(){return state;} public String getPostalCode(){return postalCode;} public String getCountry(){return country;} public boolean isDefaultAddress(){return defaultAddress;} public void setDefaultAddress(boolean value){defaultAddress=value;}
}
