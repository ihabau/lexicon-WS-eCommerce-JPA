package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
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

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  @EqualsAndHashCode

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
  private Instant startDate;

  @Column(nullable = true)
  private Instant endDate;

  @ManyToMany(mappedBy = "promotions" )
  private List<Product> products = new ArrayList<>();

}
