package com.nextgen.store.payment;

import com.nextgen.store.order.Order;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
  private Payment payment(){return new Payment(new Order(null,null,BigDecimal.TEN,BigDecimal.ZERO,BigDecimal.TEN,"ONLINE","test-key"),PaymentMethod.ONLINE,PaymentStatus.PENDING,BigDecimal.TEN,"INR");}
  @Test void allowsPendingToCaptured(){var p=payment();p.transitionTo(PaymentStatus.CAPTURED);assertEquals(PaymentStatus.CAPTURED,p.getStatus());}
  @Test void allowsAuthorizedToCaptured(){var p=payment();p.transitionTo(PaymentStatus.AUTHORIZED);p.transitionTo(PaymentStatus.CAPTURED);assertEquals(PaymentStatus.CAPTURED,p.getStatus());}
  @Test void allowsCapturedToRefunded(){var p=payment();p.transitionTo(PaymentStatus.CAPTURED);p.transitionTo(PaymentStatus.REFUNDED);assertEquals(PaymentStatus.REFUNDED,p.getStatus());}
  @Test void rejectsTerminalRegression(){var p=payment();p.transitionTo(PaymentStatus.CAPTURED);assertThrows(IllegalStateException.class,()->p.transitionTo(PaymentStatus.PENDING));}
  @Test void rejectsFailedToCaptured(){var p=payment();p.transitionTo(PaymentStatus.FAILED);assertThrows(IllegalStateException.class,()->p.transitionTo(PaymentStatus.CAPTURED));}
}
