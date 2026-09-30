package se.lexicon.lexiconwsecommercejpa.service;

import se.lexicon.lexiconwsecommercejpa.dto.OrderResponse;
import se.lexicon.lexiconwsecommercejpa.dto.OrderRequest;

public interface OrderService {

  // OrderResponse and not Order: the entity must not leave the service layer.
  // Not void either: the caller needs the order id, the status, and the prices
  // actually charged.
  OrderResponse placeOrder(OrderRequest request);
}

/*
 * The interface/impl split is not ceremony. A controller (Part 4) depends on
 * this interface, so it can be written and reasoned about without knowing which
 * implementation is running. The interface is also the list of what this service
 * can DO - if a method is not here, the controller cannot call it, which is how
 * a service keeps a small deliberate public surface instead of thirty methods.
 *
 * @Transactional and @Service belong on the IMPL, not here: an interface cannot
 * carry a transactional behaviour, because there is no implementation to wrap.
 *
 * Do NOT add create/update/delete for Order later. An order exists as the RESULT
 * of placeOrder, and a second door into that state machine defeats the point.
 */
