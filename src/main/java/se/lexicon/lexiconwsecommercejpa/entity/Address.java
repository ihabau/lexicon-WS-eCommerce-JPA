package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * WHY NO `length` ON THESE THREE COLUMNS: the assignment only says they must be
 * mandatory, and gives no length - unlike UserProfile (max 100) and Customer
 * (100/100/150). So `nullable = false` alone is a correct reading, and adding
 * length = 100 here would be inventing a requirement. Hibernate's default is
 * VARCHAR(255). Worth knowing: a constraint you were not asked for is still a
 * decision, and a decision you did not write down is a trap for the next person.
 *
 * WHY @ToString AND @EqualsAndHashCode SURVIVE HERE AND NOWHERE ELSE: both walk
 * EVERY field, and on this class the walk terminates - four scalar fields, nothing
 * points back. Put the same two annotations on Customer and you get
 * customer -> profile -> customer -> ... forever, i.e. a StackOverflowError the
 * first time anything logs the object. Same story in Category, Product, Order and
 * OrderItem, which is why every other entity has the "No @ToString" note above
 * its Lombok block.
 *
 * @EqualsAndHashCode is the more dangerous of the pair: it is what HashSet and
 * HashMap use, so if a bidirectional relationship makes equals() asymmetric,
 * putting entities in a Set silently loses elements. Managed entities are
 * identified by their id, and the JPA default is usually the right answer.
 */



   // Address is standalone - no relationships - so @ToString and
   // @EqualsAndHashCode are safe here: nothing can walk back to an Address.
   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor
   @ToString
   @EqualsAndHashCode

   @Entity
   @Table(name = "addresses")


public class Address {

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
