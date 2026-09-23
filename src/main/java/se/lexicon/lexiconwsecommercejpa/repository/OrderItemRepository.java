package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.OrderItem;
import se.lexicon.lexiconwsecommercejpa.entity.Product;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  List<OrderItem> findByOrderId( Long orderId);

  List<OrderItem> findByProduct(Product product);

  List<OrderItem> findByQuantityGreaterThan(int quantity);

  // OPTIONAL (Part2.md:339-341): find all order items belonging to an order;
  // find all order items for a product; find order items where the quantity
  // is greater than a given value.
}
