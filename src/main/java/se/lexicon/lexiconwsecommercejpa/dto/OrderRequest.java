package se.lexicon.lexiconwsecommercejpa.dto;

import java.util.List;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/*
 * OrderRequest - INBOUND data: what a client sends to place an order.
 *
 * WHY customerId IS `Long` AND NOT `long` - the project-wide rule. Every entity
 * declares `private Long id`, every repository extends JpaRepository<X, Long>,
 * and the service methods take `Long`. The reason is not fashion: a primitive can
 * never be null, so a request with no customerId and one with "customerId": 0
 * both arrive as 0 and @NotNull passes both times - the check is decoration.
 * With `Long` the missing field arrives as null, the one state @NotNull can see.
 * The cost of getting this wrong is a confusing 404: the service looks up
 * customer 0, finds nobody, and reports "not found" when the real problem is that
 * the client never sent an id.
 *
 * WHY @Valid SITS NEXT TO @NotEmpty - THEY DO DIFFERENT JOBS. @NotEmpty looks at
 * the list itself: is it null, is it empty. It has no idea the elements are
 * records with rules of their own. @Valid is the instruction to go inside and
 * check each one, and without it the rules in OrderItemRequest never run. The
 * failure that causes is QUIET rather than loud: a quantity of -5 satisfies
 * @NotNull, the order is built, and the database accepts it, because
 * nullable = false forbids a MISSING quantity but a negative one is a perfectly
 * valid integer. You end up with a real order nobody rejected.
 * (jakarta.validation.Valid - not javax, and not jakarta.annotation.Nonnull, which
 * is static-analysis only and does nothing at runtime.)
 *
 * NO price COMPONENT, AND THAT IS THE POINT: the client must not get to decide
 * what something costs. The service reads the live price off the Product and
 * stores it in OrderItem.priceAtPurchase. Accepting a price here would let anyone
 * order a 999 EUR item for 1 EUR.
 *
 * A RECORD HOLDING A LIST IS ONLY SHALLOWLY IMMUTABLE. `final` on the component
 * reference does not freeze the list: a caller can still add to it after
 * construction. List.copyOf in a compact constructor if you want real immutability
 * - optional, but it is the difference between "immutable" as a claim and as fact.
 *
 * TWO DECISIONS THE SERVICE STILL OWNS, WHICH NO VALIDATION CAN MAKE:
 *   - The same product twice in one order: there is no unique constraint on
 *     (order_id, product_id), so two rows appear and the order looks corrupt.
 *     Either merge the quantities or reject the duplicate.
 *   - Order.items has NO initialiser, and @PrePersist throws if it is null or
 *     empty. An order with no items is unpersistable by design, which is exactly
 *     why @NotEmpty on the list is worth having.
 */
public record OrderRequest(


    @NotNull(message = "CustomerId must be provided!")
    Long customerId,

    @NotEmpty(message = "Order must contain at least one item!")
    @Valid
    List<OrderItemRequest> items

    ) {
}
