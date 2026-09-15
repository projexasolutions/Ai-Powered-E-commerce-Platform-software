package com.nextgen.store.account;

import com.nextgen.store.catalog.ProductVariant;
import jakarta.persistence.*;

@Entity
@Table(name="cart_items")
public class CartItem {
 @EmbeddedId private Id id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @MapsId("cartId") @JoinColumn(name="cart_id") private Cart cart;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) @MapsId("variantId") @JoinColumn(name="variant_id") private ProductVariant variant;
 @Column(nullable=false) private int quantity;
 protected CartItem(){}
 public CartItem(Cart cart,ProductVariant variant,int quantity){this.cart=cart;this.variant=variant;this.quantity=quantity;this.id=new Id(null,variant.getId());}
 public Id getId(){return id;} public ProductVariant getVariant(){return variant;} public int getQuantity(){return quantity;}
 public void increase(int amount){quantity+=amount;}
 public void setQuantity(int quantity){this.quantity=quantity;}
 @Embeddable public record Id(Long cartId,Long variantId) implements java.io.Serializable{}
}
