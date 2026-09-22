package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * ADDRESS - Part 1 entity (Part1.md:114-121).
 * TODO: build it.
 *  - a JPA entity mapped to the "addresses" table
 *  - identity-generated primary key (Long id)
 *  - street, city, zipCode: all mandatory columns
 *  - standalone: does NOT reference Customer
 */



   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor
   @ToString
   @EqualsAndHashCode

   @Entity
   @Table(name = "addresses")


public class Address {
    // TODO: add fields + JPA annotations per the requirements above.
    //

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String street;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String zipCode;



}
