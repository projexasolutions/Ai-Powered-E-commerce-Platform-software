package com.nextgen.store.catalog;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
  private final ProductRepository repository;
  private final ProductVariantRepository variants;

  public ProductController(ProductRepository repository, ProductVariantRepository variants){
    this.repository=repository;
    this.variants=variants;
  }

  @GetMapping
  public Page<ProductResponse> list(@RequestParam(required=false) String q,@RequestParam(required=false) String category,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size,@RequestParam(defaultValue="name") String sort,@RequestParam(defaultValue="asc") String direction){
    String safeSort=Set.of("name","price","rating").contains(sort)?sort:"name";
    Sort.Direction d="desc".equalsIgnoreCase(direction)?Sort.Direction.DESC:Sort.Direction.ASC;
    return repository.search(blank(q),blank(category),PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100),Sort.by(d,safeSort))).map(ProductResponse::from);
  }

  @GetMapping("/{id}")
  public ProductResponse get(@PathVariable Long id){
    return repository.findById(id).filter(p->p.getCategory()!=null).map(ProductResponse::from).orElseThrow(()->new NoSuchElementException("Product not found"));
  }

  @GetMapping("/{id}/detail")
  public ProductDetailResponse detail(@PathVariable Long id){
    Product product=repository.findById(id).filter(p->p.getCategory()!=null).orElseThrow(()->new NoSuchElementException("Product not found"));
    List<VariantResponse> items=variants.findByProductIdAndActiveTrueOrderBySizeAscColorAsc(product.getId()).stream().map(VariantResponse::from).toList();
    return ProductDetailResponse.from(product,items);
  }

  private String blank(String value){return value==null||value.isBlank()?null:value.trim();}

  public record ProductResponse(Long id,String name,String slug,String description,java.math.BigDecimal price,java.math.BigDecimal rating,String category,String categorySlug,String imageUrl){
    static ProductResponse from(Product p){return new ProductResponse(p.getId(),p.getName(),p.getSlug(),p.getDescription(),p.getPrice(),p.getRating(),p.getCategory().getName(),p.getCategory().getSlug(),p.getImageUrl());}
  }

  public record VariantResponse(Long id,String sku,String size,String color,int stockQuantity,java.math.BigDecimal price){
    static VariantResponse from(ProductVariant v){
      java.math.BigDecimal price=v.getPriceOverride()!=null?v.getPriceOverride():v.getProduct().getPrice();
      return new VariantResponse(v.getId(),v.getSku(),v.getSize(),v.getColor(),v.getStockQuantity(),price);
    }
  }

  public record ProductDetailResponse(Long id,String name,String slug,String description,java.math.BigDecimal price,java.math.BigDecimal rating,String category,String categorySlug,String imageUrl,List<VariantResponse> variants){
    static ProductDetailResponse from(Product p,List<VariantResponse> variants){
      return new ProductDetailResponse(p.getId(),p.getName(),p.getSlug(),p.getDescription(),p.getPrice(),p.getRating(),p.getCategory().getName(),p.getCategory().getSlug(),p.getImageUrl(),variants);
    }
  }
}
