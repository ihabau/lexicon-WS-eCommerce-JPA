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
public class OrderItem {
    // TODO: add fields + JPA annotations per the requirements above.
}