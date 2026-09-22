package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * ORDERREPOSITORY - Part 2 (workshop Part2.md:313-331).
 *
 * !!! BLOCKER: Order.java is a scaffold, not a real @Entity yet (no
 *     status/customer/items fields). Build it first, then add the methods.
 *
 * CRUD inherited for free.
 * =====================================================================
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /*
     * TODO (REQUIRED - Part2.md:315): all orders for one customer.
     *   List<Order> findByCustomer_Id(Long customerId);
     *   TIP: nested Order -> Customer -> id.
     */

    /*
     * TODO (REQUIRED - Part2.md:316): orders by status WITHOUT the N+1 problem.
     *   "N+1" = one query for the orders, then one EXTRA query per order to
     *   fetch its items. Avoid it with either:
     *
     *   (A) @EntityGraph(attributePaths = "items")
     *       List<Order> findByStatus(OrderStatus status);
     *
     *   (B) @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
     *       List<Order> findByStatusWithItems(OrderStatus status);
     *
     *   Both load every order's items in ONE query. Needs Order.status and
     *   Order.items to exist.
     *   TIP: DISTINCT stops duplicate orders (a row repeats once per item).
     */

    /*
     * TODO (OPTIONAL - Part2.md:320-321): by dates.
     *   List<Order> findByOrderDateAfter(Instant date);
     *   List<Order> findByOrderDateBetween(Instant from, Instant to);
     */

    /*
     * TODO (OPTIONAL - Part2.md:322): orders containing a specific product.
     *   List<Order> findByItems_Product_Id(Long productId);
     *   (Order -> items -> product -> id)
     */

    /*
     * TODO (OPTIONAL - Part2.md:323): count orders by status.
     *   long countByStatus(OrderStatus status);
     */

    /*
     * TODO (OPTIONAL - Part2.md:324): combine customer + status.
     *   List<Order> findByCustomer_IdAndStatus(Long customerId, OrderStatus status);
     */
}