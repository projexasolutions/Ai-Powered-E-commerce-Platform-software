package com.nextgen.store.account;

import com.nextgen.store.auth.User;
import com.nextgen.store.catalog.Product;
import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="wishlists")
public class Wishlist {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false,unique=true) private User user;
  @ManyToMany
  @JoinTable(name="wishlist_items",joinColumns=@JoinColumn(name="wishlist_id"),inverseJoinColumns=@JoinColumn(name="product_id"))
  private Set<Product> products=new LinkedHashSet<>();
  protected Wishlist(){}
  public Wishlist(User user){this.user=user;}
  public Set<Product> getProducts(){return products;}
}
