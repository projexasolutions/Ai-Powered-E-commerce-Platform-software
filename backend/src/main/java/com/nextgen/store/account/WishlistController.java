package com.nextgen.store.account;

import com.nextgen.store.auth.User;
import com.nextgen.store.auth.UserRepository;
import com.nextgen.store.catalog.Product;
import com.nextgen.store.catalog.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {
  private final WishlistRepository wishlists; private final UserRepository users; private final ProductRepository products;
  public WishlistController(WishlistRepository wishlists,UserRepository users,ProductRepository products){this.wishlists=wishlists;this.users=users;this.products=products;}
  @GetMapping public List<Product> get(Authentication auth){return getOrCreate(user(auth)).getProducts().stream().toList();}
  @PostMapping("/{productId}") public List<Product> add(Authentication auth,@PathVariable Long productId){Wishlist w=getOrCreate(user(auth));w.getProducts().add(products.findById(productId).orElseThrow(()->new NoSuchElementException("Product not found")));wishlists.save(w);return w.getProducts().stream().toList();}
  @DeleteMapping("/{productId}") public List<Product> remove(Authentication auth,@PathVariable Long productId){Wishlist w=getOrCreate(user(auth));w.getProducts().removeIf(p->p.getId().equals(productId));wishlists.save(w);return w.getProducts().stream().toList();}
  private User user(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow(()->new NoSuchElementException("User not found"));}
  private Wishlist getOrCreate(User u){return wishlists.findByUserId(u.getId()).orElseGet(()->wishlists.save(new Wishlist(u)));}
}
