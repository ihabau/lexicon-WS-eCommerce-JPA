package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.OrderItem;
import se.lexicon.lexiconwsecommercejpa.entity.Product;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * The assignment calls this repository optional: OrderItem is normally managed
 * through Order via cascade and orphan removal.
 *
 * The spec says "for a specific product ID" but this takes the Product ENTITY.
 * Both are valid - the entity means zero extra queries when the caller already
 * loaded the product, while findByProductId(Long) needs only the number. To make
 * the two lines here match, change it to findByProductId; no @Query needed.
 *
 * `int quantity` (primitive) is the right choice HERE, unlike on the DTO side.
 * A filter built from existing data can never be null, so a primitive is fine.
 * On the way IN - a request - you want the BOXED type, so @NotNull can tell
 * "not supplied" apart from "0".
 *
 * Keywords to avoid: "BiggerThan" and "MoreThan" read like English but are not
 * keywords, so Spring Data would treat "Bigger" as a property name and fail at
 * startup.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  // OPTIONAL: items belonging to an order.
  List<OrderItem> findByOrderId( Long orderId);

  // OPTIONAL: items for a product.
  List<OrderItem> findByProduct(Product product);

  // OPTIONAL: items where the quantity is greater than a value.
  List<OrderItem> findByQuantityGreaterThan(int quantity);
}
