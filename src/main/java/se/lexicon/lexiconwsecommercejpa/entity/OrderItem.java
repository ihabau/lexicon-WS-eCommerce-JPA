package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/*
 * ORDERITEM - Part 2 entity (Part2.md:220-230, 265-275).
 * TODO: build it.
 *  - a JPA entity mapped to the "order_items" table
 *  - identity-generated primary key (Long id)
 *  - quantity: mandatory
 *  - priceAtPurchase: mandatory, BigDecimal, precision/scale.
 *    TIP: think about WHY the price is copied here instead of always reading
 *    the live product price (Part2.md:271-274).
 *  - many-to-one to Order - the OWNER side of the relationship (FK "order_id")
 *  - many-to-one to Product (FK "product_id")
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
    // TODO: add fields + JPA annotations per the requirements above.

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
