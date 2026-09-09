package com.nextgen.store.catalog;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
  private final ProductRepository repository;
  public ProductController(ProductRepository repository){this.repository=repository;}

  @GetMapping
  public Page<ProductResponse> list(@RequestParam(required=false) String q,@RequestParam(required=false) String category,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size,@RequestParam(defaultValue="name") String sort,@RequestParam(defaultValue="asc") String direction){
    String safeSort=Set.of("name","price","rating").contains(sort)?sort:"name";
    Sort.Direction d="desc".equalsIgnoreCase(direction)?Sort.Direction.DESC:Sort.Direction.ASC;
    return repository.search(blank(q),blank(category),PageRequest.of(Math.max(page,0),Math.min(Math.max(size,1),100),Sort.by(d,safeSort))).map(ProductResponse::from);
  }
  @GetMapping("/{id}")
  public ProductResponse get(@PathVariable Long id){return repository.findById(id).filter(p->p.getCategory()!=null).map(ProductResponse::from).orElseThrow(()->new NoSuchElementException("Product not found"));}
  private String blank(String value){return value==null||value.isBlank()?null:value.trim();}

  public record ProductResponse(Long id,String name,String slug,String description,java.math.BigDecimal price,java.math.BigDecimal rating,String category,String categorySlug,String imageUrl){
    static ProductResponse from(Product p){return new ProductResponse(p.getId(),p.getName(),p.getSlug(),p.getDescription(),p.getPrice(),p.getRating(),p.getCategory().getName(),p.getCategory().getSlug(),p.getImageUrl());}
  }
}
