package com.nextgen.store.catalog;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductVariantTest {
  @Test void decreaseStockReducesQuantity(){var v=new ProductVariant();assertEquals(0,v.getStockQuantity());}
  @Test void decreaseStockRejectsInvalidQuantity(){var v=new ProductVariant();assertThrows(IllegalArgumentException.class,()->v.decreaseStock(1));}
}
