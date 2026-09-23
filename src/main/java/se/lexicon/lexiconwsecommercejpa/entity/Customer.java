package se.lexicon.lexiconwsecommercejpa.entity;

// Imports you will need for the annotations below:
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/*
 * =====================================================================
 * CUSTOMER - Part 1 entity, the OWNER side of both @OneToOne relations.
 * Data model only. Queries live in CustomerRepository.
 *
 * The class body is empty ON PURPOSE - rebuild it (doc = Part1.md:134-152).
 * =====================================================================
 */
   // No @ToString/@EqualsAndHashCode: `profile` is bidirectional (UserProfile
   // has a back-reference to its customer), which would recurse forever in
   // generated toString/equals/hashCode. Equality is the `id`.
   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor

   @Entity
   @Table(name = "customers")

public class Customer {



   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   // firstName & lastName (doc: mandatory, max 100)
    @Column(nullable = false, length = 100)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column( updatable = false, nullable = false)
    private Instant createdAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;


    @PrePersist
    void onCreate() {
      if (createdAt == null) {
        createdAt = Instant.now();
      }
    }
}
