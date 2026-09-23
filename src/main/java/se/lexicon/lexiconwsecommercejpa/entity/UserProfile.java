package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * USERPROFILE - Part 1 entity (Part1.md:123-132).
 * TODO: build it.
 *  - a JPA entity mapped to the "user_profiles" table
 *  - identity-generated primary key (match the id style of the other entities)
 *  - nickName: mandatory, max 100
 *  - phoneNumber: mandatory, max 100
 *  - bio: optional, max 500
 *  - optional bidirectional one-to-one BACK to Customer (the INVERSE side;
 *    the owning field on Customer is named "profile")
 */


   // No @ToString/@EqualsAndHashCode: `customer` is bidirectional (Customer
   // owns a back-reference via profile), which would recurse forever in
   // generated toString/equals/hashCode. Equality is the `id`.
   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor

@Entity
@Table(name = "user_profiles")


public class UserProfile {
    // TODO: add fields + JPA annotations per the requirements above.
    
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nickName;

  @Column(nullable = false, length = 100)
  private String phoneNumber;

  // nullable is redundent but just in case
  @Column(nullable = true, length = 500)
  private String bio;
    
  @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
  private Customer customer;

    
}
