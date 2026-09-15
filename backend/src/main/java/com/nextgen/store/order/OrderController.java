package com.nextgen.store.order;

import com.nextgen.store.address.*;
import com.nextgen.store.auth.*;
import com.nextgen.store.catalog.ProductVariant;
import com.nextgen.store.catalog.ProductVariantRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
  private final OrderRepository orders; private final UserRepository users; private final AddressRepository addresses; private final ProductVariantRepository variants;
  public OrderController(OrderRepository orders,UserRepository users,AddressRepository addresses,ProductVariantRepository variants){this.orders=orders;this.users=users;this.addresses=addresses;this.variants=variants;}
  @GetMapping public List<OrderResponse> list(Authentication auth){User u=user(auth);return orders.findByUserIdOrderByCreatedAtDesc(u.getId()).stream().map(OrderResponse::from).toList();}
  @GetMapping("/{id}") public OrderResponse get(Authentication auth,@PathVariable Long id){User u=user(auth);return orders.findByIdAndUserId(id,u.getId()).map(OrderResponse::from).orElseThrow(()->new NoSuchElementException("Order not found"));}
  @PostMapping @Transactional
  public OrderResponse create(Authentication auth,@Valid @RequestBody CreateOrder request){
    User u=user(auth); Address address=addresses.findByIdAndUserId(request.addressId(),u.getId()).orElseThrow(()->new NoSuchElementException("Address not found"));
    String method=request.paymentMethod()==null?"COD":request.paymentMethod().trim().toUpperCase();
    if(!method.equals("COD")&&!method.equals("ONLINE")) throw new IllegalArgumentException("Unsupported payment method");
    if(request.items()==null||request.items().isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");
    BigDecimal subtotal=BigDecimal.ZERO; List<Line> lines=new ArrayList<>();
    for(Item item:request.items()){
      ProductVariant v=variants.findById(item.variantId()).filter(ProductVariant::isActive).orElseThrow(()->new NoSuchElementException("Variant not found"));
      if(item.quantity()<1) throw new IllegalArgumentException("Quantity must be positive");
      if(v.getStockQuantity()<item.quantity()) throw new IllegalArgumentException("Insufficient stock for "+v.getSku());
      BigDecimal price=v.getPriceOverride()!=null?v.getPriceOverride():v.getProduct().getPrice();
      subtotal=subtotal.add(price.multiply(BigDecimal.valueOf(item.quantity())));lines.add(new Line(v,item.quantity(),price));
    }
    // Delivery is an explicit checkout charge; it is never silently included in product prices.
    BigDecimal delivery=new BigDecimal("99.00");
    BigDecimal total=subtotal.add(delivery);
    Order order=orders.save(new Order(u,address,subtotal,delivery,total,method));
    for(Line l:lines){l.variant().decreaseStock(l.quantity());order.getItems().add(new OrderItem(order,l.variant(),l.quantity(),l.price()));}
    return OrderResponse.from(orders.save(order));
  }
  private User user(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow(()->new NoSuchElementException("User not found"));}
  private record Line(ProductVariant variant,int quantity,BigDecimal price){}
  public record Item(Long variantId,@Min(1) int quantity){}
  public record CreateOrder(Long addressId,List<Item> items,String paymentMethod){}
  public record OrderItemResponse(String productName,String sku,String size,String color,int quantity,BigDecimal unitPrice,BigDecimal lineTotal){}
  public record OrderResponse(Long id,String status,BigDecimal subtotal,BigDecimal shippingAmount,BigDecimal deliveryCharge,BigDecimal totalAmount,String paymentMethod,String paymentStatus,String currency,List<OrderItemResponse> items){static OrderResponse from(Order o){return new OrderResponse(o.getId(),o.getStatus().name(),o.getSubtotal(),o.getShippingAmount(),o.getDeliveryCharge(),o.getTotalAmount(),o.getPaymentMethod(),o.getPaymentStatus(),o.getCurrency(),o.getItems().stream().map(i->new OrderItemResponse(i.getProductName(),i.getSku(),i.getSize(),i.getColor(),i.getQuantity(),i.getUnitPrice(),i.getLineTotal())).toList());}}
}
