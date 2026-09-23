package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;

import java.time.Instant;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findByCustomerId(Long customerId); // am not sure about the name. Its correct trust yourself!

  @EntityGraph(attributePaths = {"items"})
  List<Order> findByStatus(OrderStatus status);

  List<Order> findByOrderDateAfter(Instant date);
  List<Order> findByOrderDateBetween(Instant start, Instant end);

  @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product = :product")
  List<Order> findByProduct(@Param("product") Product product);

  Long countByStatus(OrderStatus status);

  @Query("SELECT o FROM Order o WHERE o.customer.id = :id AND o.status = :status")
  List<Order> findByCustomerIdAndStatus(@Param("id") Long id, @Param("status") OrderStatus status);


  // REQUIRED (Part2.md:317-318): find all orders belonging to a customer id;
  // find orders by status WITHOUT the N+1 problem - load the order items in
  // the same query (EntityGraph or a JOIN FETCH in JPQL).

  // OPTIONAL (Part2.md:321-325): orders created after a date; orders created
  // between two dates; orders containing a specific product; count orders by
  // status; combine customer id and status in one query.
  //
  // TEACHING TIP - the combined query (last item):
  // - A derived name expresses EACH condition as a property clause, and you
  //   join clauses with the keyword And: subject -> clause1 -> And -> clause2.
  //   Re-read the requirement: "orders BY customer id AND status" - the word
  //   And is the giveaway that a single query IS expected.
  // - Merge the two halves you already solved: the customer-id clause from the
  //   method above, and the status clause from findByStatus. Copy their exact
  //   property spellings - no new spelling needed.
  // - One method parameter per clause, in the same order they appear in the
  //   name. Matching types: customer id is Long, status is OrderStatus.
  // - Order matters only for readability; the parser resolves by name spelling.
  // - And = both must match (narrower). You will also meet Or (broader) - and
  //   you can chain them, though each And/Or needs the property repeated.
  // - No @EntityGraph needed here - the N+1 requirement applied only to the
  //   "orders by status" query.
}
