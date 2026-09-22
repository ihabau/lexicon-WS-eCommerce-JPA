package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/*
 * CATEGORY - Part 2 entity (Part2.md:208-217).
 * TODO: build it.
 *  - a JPA entity mapped to the "categories" table
 *  - identity-generated primary key (Long id)
 *  - name: mandatory, max 100
 *  - OPTIONAL bidirectional one-to-many BACK to Product (the INVERSE side;
 *    the FK lives in Product)
 */


   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor
   @ToString
   @EqualsAndHashCode

   @Entity
   @Table(name = "categories")


public class Category {
    // TODO: add fields + JPA annotations per the requirements above.
    

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;


  @OneToMany(mappedBy = "category")
  private List<Product> products;
    

}
