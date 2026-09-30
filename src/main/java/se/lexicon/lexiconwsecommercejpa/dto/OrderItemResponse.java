package se.lexicon.lexiconwsecommercejpa.dto;


import java.math.BigDecimal;

/*
 * OrderItemResponse - OUTBOUND data for ONE line of an order: which product (id
 * AND name), how many, and what it cost.
 *
 * A SEPARATE type from OrderItemRequest on purpose, and that is the single most
 * useful thing to understand about having two:
 *   - OrderItemRequest is what a client is ALLOWED TO SAY. Validated, and
 *     deliberately almost empty, because everything in it is a claim the server
 *     has not checked yet.
 *   - OrderItemResponse is what the server DECIDED. Never validated, and it can
 *     safely include things the client never sent - the resolved product name, the
 *     price actually charged.
 * "What do you want?" versus "what happened?". Merging them would force the
 * response to carry the request's validation annotations (which never run) and
 * make every new server-side field a decision about what clients may send.
 *
 * THE NAME: this was ItemListResponse, and nothing in it is a list. The LIST
 * lives in OrderResponse as List<OrderItemResponse>.
 *
 * priceAtPurchase IS A SNAPSHOT, AND THAT IS THE WHOLE POINT: map it from
 * OrderItem.getPriceAtPurchase(), NEVER from product.getPrice(). The entity
 * copies the value at order time precisely so a later price change cannot
 * silently rewrite what the customer was charged. If you ever reach for the live
 * product price to fill this field, the snapshot is broken.
 *
 * productName is a deliberate second hop: the item row stores a foreign key, not
 * a name, so the name is walked out of the product. OrderItem.product is LAZY, so
 * touching it needs an open session - @Transactional on placeOrder covers it.
 *
 * NO CONSTRAINTS HERE, AND THAT IS THE RULE FOR RESPONSES: they are only evaluated
 * for a validated @RequestBody, so on outgoing data they promise a check that
 * never runs. What a response MAY carry is serialization config: @JsonProperty,
 * @JsonInclude(NON_NULL), @JsonFormat.
 *
 * `Long productId` BUT `int quantity` - NOT AN INCONSISTENCY. The id is boxed
 * because 0 is a plausible-looking WRONG id: a mapper that forgets to pass it
 * produces a silent 0, and Long turns that into a visible null. quantity is a
 * plain int because 0 is a legitimate value, not a mistake, and a primitive means
 * the mapper never null-checks it - the value came out of a saved order, so it is
 * always there. Boxed types earn their keep on the way IN, which is why
 * OrderItemRequest uses `Integer quantity`.
 */
public record OrderItemResponse(

    Long productId,
    String productName,
    int quantity,
    BigDecimal priceAtPurchase

    ) {
}
