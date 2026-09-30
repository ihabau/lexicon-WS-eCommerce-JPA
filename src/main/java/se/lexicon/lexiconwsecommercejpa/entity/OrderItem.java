package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/*
 * WHY COPY THE PRICE? Product.price is a MOVING value and priceAtPurchase is a
 * FROZEN one, and a receipt has to be the second kind. Drop the column and read
 * the live price instead: the shop runs a sale from 99 to 49 and every order ever
 * placed - last month's receipts, the customer's history, a refund calculated from
 * the order - silently now says 49. The order was correct when written; only the
 * interpretation changed, which is the worst kind of bug because nothing throws
 * and no test fails.
 *
 * That is also why the column is not called "price": the name carries the moment.
 * And why the table does not store the product NAME either - rename a product and
 * last month's history would show the new name. Storing it would be a second
 * snapshot, a weaker one; that is a design choice, not a requirement.
 *
 * precision/scale is identical to Product.price on purpose: this is a copy of
 * that number, and a column that cannot hold the value it preserves would truncate
 * silently. Money in a double loses cents to binary rounding.
 *
 * `int quantity` is correct HERE and the OPPOSITE of OrderItemRequest, which uses
 * `Integer` on purpose. Which side of the boundary decides:
 *   ON THE WAY IN (a request) use the BOXED type - "not supplied" and "0" are
 *     different situations, and only a boxed type can represent the first, so
 *     @NotNull has something to detect. A primitive turns a missing field into 0.
 *   ON THE WAY OUT (a saved row) a primitive is fine - the column is
 *     nullable = false, so null is not a state that can occur.
 * So: boxed for data a client sends you, primitive for data you already have.
 *
 * Note there is NO quantity >= 1 check on this entity. nullable = false forbids a
 * MISSING quantity but happily accepts -5, because -5 is a valid integer. That is
 * why OrderItemRequest carries @Min(1) - and it is the difference between the two
 * validation layers: DTO annotations give the client a 400 with a message, column
 * constraints are the last line of defence against a bug in OUR code.
 *
 * TWO @ManyToOne, both LAZY, both owner of their own relationship. So nothing
 * here loads for free: item.getProduct().getName() fires a SELECT, and doing that
 * while printing a list of items is an N+1 by hand - the same problem
 * OrderRepository.findByStatus solves with @EntityGraph("items").
 *
 * There is deliberately no back-reference from Product or Order to "the items
 * that mention me". Two entities pointing at the same join entity is a classic
 * source of accidental cascades. Consequence to know: deleting a Product will
 * FAIL while order_items rows still point at it, which is correct - you should
 * not be able to delete something in someone's purchase history - but it
 * surprises the first time.
 */

// No @ToString/@EqualsAndHashCode: `order` is bidirectional (Order holds this
// item in its items collection + cascade), so walking fields would recurse
// forever in generated toString/equals/hashCode. Equality is the `id`.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "order_items")




public class OrderItem {

 @Id
@GeneratedValue( strategy = GenerationType.IDENTITY )
private Long id;

@Column(nullable = false)
private int quantity;

@Column(nullable = false, precision = 10, scale = 2)
private BigDecimal priceAtPurchase;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "order_id", nullable = false)
private Order order;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "product_id", nullable = false)
private Product product;







}
