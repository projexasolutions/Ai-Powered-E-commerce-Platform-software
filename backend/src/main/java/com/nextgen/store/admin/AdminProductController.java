package com.nextgen.store.admin;

import com.nextgen.store.catalog.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@RestController @RequestMapping("/api/v1/admin/products") @PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
  private final ProductRepository products; private final ProductVariantRepository variants;
  public AdminProductController(ProductRepository products,ProductVariantRepository variants){this.products=products;this.variants=variants;}
  @GetMapping("/{productId}/variants") public java.util.List<AdminController.VariantSummary> list(@PathVariable Long productId){return variants.findByProductIdOrderBySizeAscColorAsc(productId).stream().map(v->new AdminController.VariantSummary(v.getId(),v.getSku(),v.getSize(),v.getColor(),v.getStockQuantity(),v.getPriceOverride(),v.getProduct().getPrice())).toList();}
  @PostMapping("/{productId}/variants") public AdminController.VariantSummary create(@PathVariable Long productId,@RequestBody VariantRequest r){throw new UnsupportedOperationException("Variant creation requires a catalog service");}
  public record VariantRequest(String sku,String size,String color,int stockQuantity,BigDecimal priceOverride){}
}
