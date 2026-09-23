package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/*
 * PROMOTION - Part 2 entity (Part2.md:251-262, 345-355).
 * TODO: build it.
 *  - a JPA entity mapped to the "promotions" table
 *  - identity-generated primary key (Long id)
 *  - code: mandatory, UNIQUE, max 100
 *  - startDate: mandatory
 *  - endDate: optional (null = open-ended)
 *  - OPTIONAL bidirectional many-to-many BACK to Product (the INVERSE side;
 *    Product owns the join table via its field named "promotions")
 */

  // No @ToString/@EqualsAndHashCode: the `products` collection is cyclic
  // (Product has a back-reference via promotions), which would recurse forever
  // in generated toString/equals/hashCode. Equality is the `id`.
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor

  @Entity
  @Table(name = "promotions")


public class Promotion {
    // TODO: add fields + JPA annotations per the requirements above.


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = true)
  private LocalDate endDate;

  @ManyToMany(mappedBy = "promotions" )
  private List<Product> products = new ArrayList<>();

}
