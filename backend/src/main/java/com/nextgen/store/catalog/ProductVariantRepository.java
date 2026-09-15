package com.nextgen.store.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long>{
  List<ProductVariant> findByProductIdAndActiveTrueOrderBySizeAscColorAsc(Long productId);
}
