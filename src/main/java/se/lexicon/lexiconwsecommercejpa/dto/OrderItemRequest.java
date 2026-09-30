package se.lexicon.lexiconwsecommercejpa.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/*
 * OrderItemRequest - INBOUND data for ONE line of an order: which product, and
 * how many of it. It is its own file because a record cannot contain an anonymous
 * inner record, and OrderRequest holds a List of these.
 *
 * THE NAME: this was ItemListRequest, and nothing in it is a list - it holds a
 * single productId/quantity pair. The response twin is OrderItemResponse and the
 * entity is OrderItem, so all three are now OrderItem and the LIST lives one
 * level up in OrderRequest, where it belongs. One concept, one name.
 *
 * WHY BOTH TYPES ARE BOXED - `Long` and `Integer`, not `long` and `int`. Every id
 * in this project is `Long`; quantity follows the same idea. A primitive can never
 * be null, so a body with no "quantity" and a body with "quantity": 0 both arrive
 * as 0 and @NotNull passes both times - the annotation is not wrong, it does
 * nothing. With Integer the missing field arrives as null, the only state
 * @NotNull can see. (For contrast, OrderItemResponse keeps `int quantity`,
 * because a 0 there is a real value rather than a missing field.)
 *
 * @NotNull AND @Min(1) ASK TWO DIFFERENT QUESTIONS: "is it there?" and "is it a
 * real number of things?". A quantity of -3 is present, so it satisfies
 * @NotNull - and it is nonsense as an order, which is why the assignment asks for
 * @Min(1). @NotNull alone would let it through to a column declared
 * nullable = false, which accepts -5 perfectly happily.
 *
 * NO priceAtPurchase COMPONENT, AND THAT IS THE POINT: the client must not be
 * able to send it. The service reads the live Product price and copies it in.
 * Accepting a price here would let anyone order a 999 EUR item for 1 EUR - the
 * client would be writing the shop's own books.
 *
 * THESE CONSTRAINTS ONLY RUN IF OrderRequest ANNOTATES ITS LIST WITH @Valid. A
 * list of records is not validated by validating the list; without @Valid every
 * rule on this record is silently skipped.
 */
public record OrderItemRequest(

    @NotNull(message = "ProductId must be provided!")
    Long productId,

    @NotNull(message = "Quantity must be provided!")
    @Min(value = 1, message = "Quantity must be at least 1!")
    Integer quantity

    ) {
}
