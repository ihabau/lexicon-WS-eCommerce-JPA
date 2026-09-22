package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;

/*
 * PRODUCT - Part 2 entity (Part2.md:208-217, 251-262).
 * TODO: build it.
 *  - a JPA entity mapped to the "products" table
 *  - identity-generated primary key (Long id)
 *  - name: mandatory, max 100
 *  - price: mandatory, BigDecimal, with precision/scale (money!)
 *  - imageUrls: a collection of simple String values stored in a SEPARATE
 *    table (product_images) via the product_id foreign key
 *  - many-to-one to Category - the OWNER side (FK "category_id"),
 *    fetch strategy set EXPLICITLY
 *  - many-to-many to Promotion - the OWNER side, join table
 *    "products_promotions" (product_id + promotion_id), LAZY fetch,
 *    and remember: promotions outlive products -> no ALL-cascading here
 */
public class Product {
    // TODO: add fields + JPA annotations per the requirements above.
}