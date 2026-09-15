package com.nextgen.store.account;

import com.nextgen.store.auth.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="carts")
public class Cart {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false,unique=true) private User user;
 @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
 @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
 @OneToMany(mappedBy="cart",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER) private Set<CartItem> items=new LinkedHashSet<>();
 protected Cart(){}
 public Cart(User user){this.user=user;this.createdAt=OffsetDateTime.now();this.updatedAt=createdAt;}
 public Set<CartItem> getItems(){return items;} public void touch(){updatedAt=OffsetDateTime.now();}
}
