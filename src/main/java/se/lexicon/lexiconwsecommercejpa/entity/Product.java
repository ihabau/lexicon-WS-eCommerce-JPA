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
 *
 *    
 *    table (product_images) via the product_id foreign key
 *
 *
 *  - many-to-one to Category - the OWNER side (FK "GeneratedValuegory_id"),
 *    fetch strategy set EXPLICITLY
 *  - many-to-many to Promotion - the OWNER side, join table
 *    "products_promotions" (product_id + promotion_id), LAZY fetch,
 *    and remember: promotions outlive products -> no ALL-cascading here
 */

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  @EqualsAndHashCode

  @Entity
  @Table(name = "products") 



public class Product {
    // TODO: add fields + JPA annotations per the requirements above.

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  // From here im dont understand correctly ia helped!
  @ElementCollection
  @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "image_url", nullable = false)
  private List<String> imageUrls;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "products_promotions",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "promotion_id")
  )
  private List<Promotion> promotions = new ArrayList<>();

}
