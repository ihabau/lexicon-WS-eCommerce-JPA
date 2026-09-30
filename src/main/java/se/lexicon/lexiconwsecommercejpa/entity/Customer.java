package se.lexicon.lexiconwsecommercejpa.entity;

// Imports you will need for the annotations below:
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/*
 * Customer owns BOTH @OneToOne relations, and there are three things to be ready
 * to explain about that.
 *
 * 1. address IS optional = false BUT profile IS NOT. address_id is declared
 *    MANDATORY by the assignment, so the column must reject null - that is what
 *    optional = false means, and it becomes a NOT NULL constraint in the schema.
 *    profile is declared OPTIONAL and must be left alone. Getting these the other
 *    way round is the easiest way to fail the schema check.
 *
 * 2. WHY ONLY ONE SIDE GETS @JoinColumn. The side that declares it OWNS the
 *    foreign key and is persisted. The other side writes
 *    mappedBy = "<the field name on the owner>" and must NOT repeat the column.
 *    Both FKs (address_id, profile_id) live in the `customers` table, so Customer
 *    is the owner of both, and UserProfile.customer is mappedBy = "profile".
 *    Declaring @JoinColumn on both sides asks Hibernate to manage the same column
 *    twice and it complains at startup.
 *
 * 3. WHY cascade = ALL IS SAFE HERE BUT NOT ON Product.promotions. ALL means
 *    save/delete on the Customer also saves/deletes the Address and UserProfile,
 *    which is right for a value belonging to exactly one customer with no life of
 *    its own. It is WRONG for Product -> Promotion: a promotion is shared by many
 *    products and outlives any single one, so deleting a product must not delete
 *    it.
 *
 * NOTE orphanRemoval here is why CustomerMapper.updateEntity MUTATES the existing
 * Address instead of calling setAddress(newAddress(...)). Replacing the object
 * would issue a DELETE against the old address row and an INSERT for the new one -
 * delete plus insert where a pure edit needed neither.
 *
 * There is deliberately NO List<Order> back-reference, even though the ER diagram
 * draws one. That relationship is unidirectional in this project: Order.customer
 * owns customer_id and Customer does not point back, which avoids a fourth cyclic
 * relationship in the object graph. Part 2 only ever asks you to query orders
 * from the ORDER side (findByCustomerId). If you add it later, it must be
 * @OneToMany(mappedBy = "customer", fetch = LAZY) and @ToString /
 * @EqualsAndHashCode must stay off.
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
