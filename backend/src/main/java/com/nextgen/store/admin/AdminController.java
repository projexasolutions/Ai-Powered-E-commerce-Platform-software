package com.nextgen.store.admin;

import com.nextgen.store.catalog.*;
import com.nextgen.store.order.*;
import com.nextgen.store.review.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final ProductRepository products; private final ProductVariantRepository variants; private final OrderRepository orders; private final ReviewRepository reviews;
  public AdminController(ProductRepository products,ProductVariantRepository variants,OrderRepository orders,ReviewRepository reviews){this.products=products;this.variants=variants;this.orders=orders;this.reviews=reviews;}
  @GetMapping("/products") public Page<ProductSummary> products(Pageable pageable){return products.findAll(pageable).map(p->new ProductSummary(p.getId(),p.getName(),p.getSlug(),p.getPrice(),p.getRating(),p.getImageUrl(),p.getCategory().getName()));}
  @GetMapping("/products/{id}/variants") public java.util.List<VariantSummary> variants(@PathVariable Long id){return variants.findByProductIdAndActiveTrueOrderBySizeAscColorAsc(id).stream().map(v->new VariantSummary(v.getId(),v.getSku(),v.getSize(),v.getColor(),v.getStockQuantity(),v.getPriceOverride(),v.getProduct().getPrice())).toList();}
  @GetMapping("/orders") public Page<OrderSummary> orders(Pageable pageable){return orders.findAll(pageable).map(o->new OrderSummary(o.getId(),o.getStatus().name(),o.getPaymentMethod(),o.getPaymentStatus(),o.getTotalAmount(),o.getCreatedAt()));}
  @GetMapping("/reviews") public Page<ReviewSummary> reviews(Pageable pageable){return reviews.findAll(pageable).map(r->new ReviewSummary(r.getId(),r.getProduct().getId(),r.getProduct().getName(),r.getRating(),r.getTitle(),r.getBody(),r.getCreatedAt()));}
  public record ProductSummary(Long id,String name,String slug,BigDecimal price,BigDecimal rating,String imageUrl,String category){}
  public record VariantSummary(Long id,String sku,String size,String color,int stockQuantity,BigDecimal priceOverride,BigDecimal productPrice){}
  public record OrderSummary(Long id,String status,String paymentMethod,String paymentStatus,BigDecimal totalAmount,java.time.OffsetDateTime createdAt){}
  public record ReviewSummary(Long id,Long productId,String productName,int rating,String title,String body,java.time.OffsetDateTime createdAt){}
}
