package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * ORDERITEMREPOSITORY - Part 2 (workshop Part2.md:334-343). OPTIONAL.
 *
 * OrderItem is normally managed through Order (cascade = ALL + orphanRemoval),
 * so a repository only makes sense for reporting-style queries like below.
 *
 * !!! BLOCKER: OrderItem.java is a scaffold, not a real @Entity yet.
 *
 * CRUD inherited for free.
 * =====================================================================
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /*
     * TODO (OPTIONAL - Part2.md:339): items belonging to one order.
     *   List<OrderItem> findByOrder_Id(Long orderId);
     */

    /*
     * TODO (OPTIONAL - Part2.md:340): items for one product.
     *   List<OrderItem> findByProduct_Id(Long productId);
     */

    /*
     * TODO (OPTIONAL - Part2.md:341): quantity above a value.
     *   List<OrderItem> findByQuantityGreaterThan(Integer quantity);
     *   TIP: "GreaterThan" === quantity > :quantity (strictly greater).
     */
}