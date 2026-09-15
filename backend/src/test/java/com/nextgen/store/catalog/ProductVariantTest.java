package com.nextgen.store.catalog;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class ProductVariantTest {
  @Test void decreaseStockReducesQuantity() throws Exception {var v=new ProductVariant();setStock(v,5);v.decreaseStock(2);assertEquals(3,v.getStockQuantity());}
  @Test void decreaseStockRejectsInvalidQuantity() throws Exception {var v=new ProductVariant();setStock(v,1);assertThrows(IllegalArgumentException.class,()->v.decreaseStock(2));assertEquals(1,v.getStockQuantity());}
  private static void setStock(ProductVariant v,int value)throws Exception{Field f=ProductVariant.class.getDeclaredField("stockQuantity");f.setAccessible(true);f.setInt(v,value);}
}
