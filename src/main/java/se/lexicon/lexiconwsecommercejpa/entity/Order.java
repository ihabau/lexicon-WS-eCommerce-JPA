package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;


/*
 * ORDER - Part 2 entity (Part2.md:220-243).
 * TODO: build it.
 *  - a JPA entity mapped to the "orders" table (NOT "order" - reserved word)
 *  - identity-generated primary key (Long id)
 *  - orderDate: set automatically at persist time, then immutable
 *  - status: an enum stored as a readable STRING in the DB (mandatory)
 *  - many-to-one to Customer (FK "customer_id", fetch strategy explicit)
 *  - one-to-many to items - the INVERSE side, but with cascade + orphan
 *    removal so the Order owns its items' lifecycle; lazy
 *  - business rule: an Order must have at least ONE item before saving
 *    (enforce it in a lifecycle callback)
 */



   // No @ToString/@EqualsAndHashCode: the bidirectional `items` relationship
   // (each OrderItem points back to its order) and `customer` would recurse
   // forever if their fields were walked. Managed entities are identified by
   // their `id`, not by comparing neighboring objects.
   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor

@Entity
@Table(name = "orders")


public class Order {
    // TODO: add fields + JPA annotations per the requirements above.


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(updatable = false, nullable = false)
  private Instant orderDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;


  @ManyToOne(fetch = FetchType.LAZY )
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<OrderItem> items;

    @PrePersist
    void onCreate() {
      if (orderDate == null) {
        orderDate = Instant.now();
      }

      if (items == null || items.isEmpty()) {
        throw new IllegalStateException("Order must have at least one item!");
      }
    }
}
