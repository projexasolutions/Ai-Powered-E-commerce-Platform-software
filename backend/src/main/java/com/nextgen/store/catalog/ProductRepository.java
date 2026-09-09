package com.nextgen.store.catalog;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Long> {
  @Query("select p from Product p join fetch p.category c where p.active=true and (:q is null or lower(p.name) like lower(concat('%',:q,'%')) or lower(p.description) like lower(concat('%',:q,'%'))) and (:category is null or c.slug=:category)")
  Page<Product> search(@Param("q") String q,@Param("category") String category,Pageable pageable);
}
