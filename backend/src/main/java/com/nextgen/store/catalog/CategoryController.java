package com.nextgen.store.catalog;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
  private final CategoryRepository repository;
  public CategoryController(CategoryRepository repository){this.repository=repository;}
  @GetMapping public List<CategoryResponse> list(){return repository.findAll().stream().map(c->new CategoryResponse(c.getId(),c.getName(),c.getSlug())).toList();}
  public record CategoryResponse(Long id,String name,String slug){}
}
