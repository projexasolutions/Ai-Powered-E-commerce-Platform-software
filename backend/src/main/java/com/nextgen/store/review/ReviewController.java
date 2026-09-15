package com.nextgen.store.review;

import com.nextgen.store.auth.User;
import com.nextgen.store.auth.UserRepository;
import com.nextgen.store.catalog.Product;
import com.nextgen.store.catalog.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {
  private final ReviewRepository reviews;
  private final ProductRepository products;
  private final UserRepository users;

  public ReviewController(ReviewRepository reviews,ProductRepository products,UserRepository users){this.reviews=reviews;this.products=products;this.users=users;}

  @GetMapping
  public Page<ReviewResponse> list(@PathVariable Long productId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){
    if(!products.existsById(productId)) throw new NoSuchElementException("Product not found");
    return reviews.findByProductIdOrderByCreatedAtDesc(productId,PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),50))).map(ReviewResponse::from);
  }

  @GetMapping("/summary")
  public ReviewSummary summary(@PathVariable Long productId){
    if(!products.existsById(productId)) throw new NoSuchElementException("Product not found");
    return new ReviewSummary(reviews.averageRating(productId),reviews.countByProductId(productId));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<?> create(@PathVariable Long productId,Authentication authentication,@Valid @RequestBody CreateReview request){
    Product product=products.findById(productId).orElseThrow(()->new NoSuchElementException("Product not found"));
    User user=users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(()->new NoSuchElementException("User not found"));
    if(reviews.findByProductIdAndUserId(productId,user.getId()).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","You have already reviewed this product."));
    Review review=reviews.save(new Review(product,user,request.rating(),clean(request.title()),clean(request.body())));
    product.setRating(java.math.BigDecimal.valueOf(reviews.averageRating(productId)).setScale(2,java.math.RoundingMode.HALF_UP));
    products.save(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.from(review));
  }

  private String clean(String value){if(value==null)return null;String v=value.trim();return v.isEmpty()?null:v;}

  public record CreateReview(@Min(1) @Max(5) int rating,@Size(max=160) String title,@Size(max=2000) String body){}
  public record ReviewResponse(Long id,int rating,String title,String body,String author,OffsetDateTime createdAt){
    static ReviewResponse from(Review r){return new ReviewResponse(r.getId(),r.getRating(),r.getTitle(),r.getBody(),"GenZ customer",r.getCreatedAt());}
  }
  public record ReviewSummary(double averageRating,long reviewCount){}
}
