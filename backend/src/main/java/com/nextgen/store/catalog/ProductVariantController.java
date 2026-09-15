package com.nextgen.store.catalog;

import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductVariantController {
  private final ProductVariantRepository variants;
  public ProductVariantController(ProductVariantRepository variants){this.variants=variants;}

  @GetMapping("/{productId}/variants")
  public List<VariantResponse> list(@PathVariable Long productId){
    return variants.findByProductIdAndActiveTrueOrderBySizeAscColorAsc(productId).stream().map(VariantResponse::from).toList();
  }
  public record VariantResponse(Long id,String sku,String size,String color,int stockQuantity,BigDecimal priceOverride,boolean inStock){
    static VariantResponse from(ProductVariant v){return new VariantResponse(v.getId(),v.getSku(),v.getSize(),v.getColor(),v.getStockQuantity(),v.getPriceOverride(),v.getStockQuantity()>0);}
  }
}
