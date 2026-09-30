package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;

import java.time.Instant;
import java.util.List;

/*
 * THE N+1 REQUIREMENT - the one thing to be ready to explain here.
 *
 * Order.items is LAZY, so this innocent-looking loop:
 *     for (Order o : orderRepository.findByStatus(PAID)) print(o.getItems().size());
 * runs ONE query for the orders, then ONE MORE PER ORDER. Five paid orders is
 * six queries. Nothing is wrong, nothing throws, and it gets worse linearly.
 *
 * @EntityGraph(attributePaths = {"items"}) tells Hibernate to build the same
 * result but fetch the association in the same round trip - a LEFT JOIN on
 * order_items instead of a second statement per row. Same answer, one query.
 * (A JOIN FETCH in JPQL is the alternative the assignment also accepts;
 * @EntityGraph keeps the derived name and Hibernate de-duplicates rows for you.)
 *
 * WHY ONLY THIS ONE METHOD HAS IT. An @EntityGraph is only needed when something
 * will WALK a lazy association on each returned row. findByCustomerId and
 * findByOrderDateAfter just return orders, and if the caller reads only the
 * orders nothing is lazy-loaded. A blanket @EntityGraph everywhere would be cargo
 * cult: it turns a cheap query into a fat one and solves a problem that was
 * never there. The requirement is one query; the principle is to add it where
 * the walk happens.
 *
 * findByProduct NEEDS a @Query, and not because it is "hard": a collection join
 * produces one row per matching item, so an order containing the product three
 * times comes back three times. A derived name cannot express SELECT DISTINCT at
 * all. A @Query appears when the NAME cannot say what you mean - DISTINCT, a
 * group of conditions with parentheses, a renamed column.
 *
 * findByCustomerIdAndStatus COULD be derived, and both are fine; the @Query here
 * is a readability choice. The naming mechanics it illustrates come up again in
 * the DTO layer:
 *   - And means BOTH clauses match (narrower), Or means either (broader);
 *   - one parameter per clause, in the order the clauses appear in the name;
 *   - each clause is a property path reachable from the entity;
 *   - the parameter type must match the property type. OrderStatus is an enum
 *     and Spring Data binds it directly - no .name(), no string conversion.
 *
 * countByStatus changes the return type to a single number and adds a COUNT to
 * the SQL. It is also a perfectly good derived query, so this file's mix of
 * derived names and @Query is a convention, not an inconsistency.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

  // `customer` is a field on Order and `id` is a field on Customer, so
  // `CustomerId` is read as customer.id - no underscore and no @Query needed.
  List<Order> findByCustomerId(Long customerId);

  // REQUIRED: orders by status, loading the items in the same query.
  @EntityGraph(attributePaths = {"items"})
  List<Order> findByStatus(OrderStatus status);

  // OPTIONAL: orders created after a date / between two dates.
  List<Order> findByOrderDateAfter(Instant date);
  List<Order> findByOrderDateBetween(Instant start, Instant end);

  // OPTIONAL: orders containing a specific product. Takes the Product ENTITY, not
  // a Long, so the caller has already loaded it and JPQL can compare objects.
  @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product = :product")
  List<Order> findByProduct(@Param("product") Product product);

  // OPTIONAL: count orders by status.
  Long countByStatus(OrderStatus status);

  // OPTIONAL: customer id AND status in one query. No @EntityGraph needed - the
  // N+1 requirement applied only to findByStatus.
  @Query("SELECT o FROM Order o WHERE o.customer.id = :id AND o.status = :status")
  List<Order> findByCustomerIdAndStatus(@Param("id") Long id, @Param("status") OrderStatus status);
}
