package se.lexicon.lexiconwsecommercejpa.mapper;

import java.util.*;
import se.lexicon.lexiconwsecommercejpa.entity.OrderStatus;
import org.springframework.stereotype.Component;
import se.lexicon.lexiconwsecommercejpa.dto.OrderItemResponse;
import se.lexicon.lexiconwsecommercejpa.dto.OrderItemRequest;
import se.lexicon.lexiconwsecommercejpa.dto.OrderResponse;
import se.lexicon.lexiconwsecommercejpa.dto.OrderRequest;
import se.lexicon.lexiconwsecommercejpa.entity.Order;
import se.lexicon.lexiconwsecommercejpa.entity.OrderItem;
import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import se.lexicon.lexiconwsecommercejpa.entity.Product;

/*
 * Stateless translator: entity <-> DTO.
 *
 * toEntity - the traps, in the order you hit them:
 *  1. orderDate  - do NOT set it. @PrePersist fills it.
 *  2. status     - DO set it. nullable=false, no default. CREATED.
 *  3. items      - Order.items has no initialiser -> new ArrayList<>() first.
 *  4. back-ref   - items is mappedBy="order", so every item needs
 *                  item.setOrder(order) or order_id stays null.
 *  5. price      - copy product.getPrice() into priceAtPurchase NOW. Reading the
 *                  live price later would rewrite what the customer was charged.
 *
 * items, customer and product are all LAZY, so this only works inside an open
 * session - @Transactional on placeOrder covers it.
 */
@Component
public class OrderMapper {

  // ==========================================================================
  // ENTITY -> DTO

  public OrderResponse toResponse(Order order){
    Objects.requireNonNull(order, "Order must not be null when building a orderResponse");


    return new OrderResponse(
        order.getId(),
        order.getCustomer().getId(),
        order.getOrderDate(),
        order.getStatus(),
        order.getItems().stream()
          .map(item -> new OrderItemResponse(
              item.getProduct().getId(),
              item.getProduct().getName(),
              item.getQuantity(),
              item.getPriceAtPurchase()))
          .toList()

        );


  }

    // Long id,
    // Long customerId,
    // Instant orderDate,
    // OrderStatus status,
    // List<OrderItemResponse> items


  // ==========================================================================
  // DTO -> ENTITY
  // The service has already turned the ids into real objects and passed them in
  // as `customer` and `productsById`, because a mapper cannot talk to the
  // database. It also decides duplicates and validation - not this class.

    public Order toEntity(OrderRequest request, Customer customer, Map<Long, Product> productsById) {

    Order order = new Order();
    order.setCustomer(customer);
    order.setStatus(OrderStatus.CREATED);
    order.setItems(new ArrayList<>());

      // looping through items in request
        for (OrderItemRequest ir : request.items()) {
      Product product = productsById.get(ir.productId());
      OrderItem item = new OrderItem();
      item.setOrder(order); //the back-reference. THIS is the order_id.
      item.setProduct(product);
      item.setQuantity(ir.quantity());
      item.setPriceAtPurchase(product.getPrice());
      order.getItems().add(item);
    }


    return order;
}

}
