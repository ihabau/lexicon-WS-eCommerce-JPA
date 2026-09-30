package se.lexicon.lexiconwsecommercejpa.dto;


import java.time.Instant;
import java.util.List;

import se.lexicon.lexiconwsecommercejpa.entity.OrderStatus;

/*
 * OrderResponse - OUTBOUND data: id, customerId, orderDate, status, items.
 *
 * THE FIELD ORDER AND NAME ARE PART OF THE CONTRACT, for three reasons that all
 * bite later rather than now:
 *   1. A record has exactly ONE constructor, in declaration order, and no
 *      overload to fall back on - so OrderMapper must pass them in this order.
 *   2. Jackson builds the JSON from these component names, in this order. The
 *      wire format is decided HERE, not in the controller.
 *   3. Two of these are of similar types, so a swap would still COMPILE and
 *      silently produce wrong data. Nothing catches it except you.
 *
 * `status`, not `orderStatus`: inside a type already called OrderResponse the
 * prefix says nothing - it would read "Order's order status". Since Jackson
 * publishes the component name, the prefix would also make the JSON field
 * disagree with the database column for no gain. A DTO name is not required to
 * mirror the entity field; it is required to describe the value to whoever reads
 * the JSON.
 *
 * WHY THE OrderStatus ENUM IS FINE IN A DTO: it looks like a persistence leak
 * and is not. OrderStatus is a plain Java enum - no @Entity, no @Enumerated, no
 * jakarta.persistence import. A JPA *type* (an entity) would leak the persistence
 * design into the API and could change under you; a plain enum can only bring its
 * own set of constant names, and Jackson writes it as "CREATED"/"PAID"/...
 * Exposing a String via .name() is defensible and has one real advantage - a
 * client can never send a status back as a valid value - but responses cannot be
 * sent back anyway. Keep the enum; if you ever expose status on a REQUEST
 * record, that is the moment to switch to String.
 *
 * THE ITEMS ARE FLATTENED, NEVER THE OrderItem ENTITY. Order -> items -> order
 * -> items recurses forever - the same trap that made @ToString get stripped
 * from the entities in Part 2. Inside a record you would not get a stack
 * overflow; Jackson would follow the graph and never stop, so the output would
 * quietly grow forever. Every response record here holds records or primitives.
 *
 * `items` must be a LIST - it was a single OrderItemResponse, which cannot
 * represent an order with two lines. One item serialises as a one-element array,
 * which is honest: nothing is lost or invented.
 *
 * THIS RECORD CANNOT BE BUILT LAZILY. Order.items and Order.customer are both
 * FetchType.LAZY, so OrderMapper.toResponse() must run while the Hibernate session
 * is open or the getters throw LazyInitializationException. Covered by
 * @Transactional on placeOrder, which it needs anyway. Exposing only the customer
 * ID rather than a name is a second, cheaper win: one lazy hop instead of two.
 *
 * TWO RULES THIS FILE FOLLOWS:
 *   - The component copies the REAL type of the entity field. Order.orderDate is
 *     an Instant; the tempting java.sql.Date has day granularity and would
 *     silently discard the time part of every order. A DTO never imports a
 *     java.sql or javax.persistence type.
 *   - No validation constraints. They are only evaluated for a validated
 *     @RequestBody, so here they would promise a check that never runs. What a
 *     response MAY carry is serialization config: @JsonProperty,
 *     @JsonInclude(NON_NULL), @JsonFormat.
 */
public record OrderResponse(

    Long id,
    Long customerId,
    Instant orderDate,
    OrderStatus status,
    List<OrderItemResponse> items

    ) {
}
