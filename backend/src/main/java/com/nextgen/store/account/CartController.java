package com.nextgen.store.account;

import com.nextgen.store.auth.User;
import com.nextgen.store.auth.UserRepository;
import com.nextgen.store.catalog.ProductVariant;
import com.nextgen.store.catalog.ProductVariantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
 private final CartRepository carts; private final UserRepository users; private final ProductVariantRepository variants;
 public CartController(CartRepository carts,UserRepository users,ProductVariantRepository variants){this.carts=carts;this.users=users;this.variants=variants;}
 @GetMapping public CartResponse get(Authentication auth){return response(getOrCreate(user(auth)));}
 @PostMapping("/items") public CartResponse add(Authentication auth,@RequestBody AddItem request){Cart c=getOrCreate(user(auth));ProductVariant v=variants.findById(request.variantId()).filter(ProductVariant::isActive).orElseThrow(()->new NoSuchElementException("Variant not found"));if(v.getStockQuantity()<request.quantity())throw new IllegalArgumentException("Insufficient stock");var found=c.getItems().stream().filter(i->i.getVariant().getId().equals(v.getId())).findFirst();if(found.isPresent())found.get().increase(request.quantity());else c.getItems().add(new CartItem(c,v,request.quantity()));c.touch();return response(carts.save(c));}
 @DeleteMapping("/items/{variantId}") public CartResponse remove(Authentication auth,@PathVariable Long variantId){Cart c=getOrCreate(user(auth));c.getItems().removeIf(i->i.getVariant().getId().equals(variantId));c.touch();return response(carts.save(c));}
 private User user(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow(()->new NoSuchElementException("User not found"));}
 private Cart getOrCreate(User u){return carts.findByUserId(u.getId()).orElseGet(()->carts.save(new Cart(u)));}
 private CartResponse response(Cart c){var rows=c.getItems().stream().map(i->new Item(i.getVariant().getId(),i.getVariant().getSku(),i.getVariant().getProduct().getId(),i.getVariant().getProduct().getName(),i.getVariant().getSize(),i.getVariant().getColor(),i.getQuantity(),i.getVariant().getPriceOverride()!=null?i.getVariant().getPriceOverride():i.getVariant().getProduct().getPrice())).toList();BigDecimal total=rows.stream().map(x->x.price().multiply(BigDecimal.valueOf(x.quantity()))).reduce(BigDecimal.ZERO,BigDecimal::add);return new CartResponse(rows,total);}
 public record AddItem(Long variantId,int quantity){}
 public record Item(Long variantId,String sku,Long productId,String productName,String size,String color,int quantity,BigDecimal price){}
 public record CartResponse(List<Item> items,BigDecimal subtotal){}
}
