package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;


/*
 * The class is named in the assignment's diagram; the table is your choice, and
 * @Table(name = "orders") is not a flourish: ORDER is a SQL reserved word, so a
 * generated table called "order" is a syntax error the moment anyone writes a
 * JOIN in raw SQL. Remove the annotation to "simplify" and Hibernate derives
 * "order" from the class and breaks.
 *
 * @OneToMany(mappedBy = "order") + cascade = ALL - both are correct and both are
 * required, and they are genuinely INDEPENDENT of each other:
 *   cascade  = "when I save/delete the Order, do the same to the items"
 *   mappedBy = "the column is over there, in the other table"
 *   orphanRemoval = "if an item is REMOVED from the list, delete its row"
 * They are about LIFETIME, not column ownership. Without the cascade, saving an
 * order with three new items would insert the order and silently lose the items.
 * Without orphanRemoval, removing an item from the list would leave an orphan
 * row with a dangling order_id.
 *
 * The child must point back. Hibernate fixes both sides for order.getItems()
 * .add(item), but a collection built by hand - the way OrderMapper builds it -
 * needs item.setOrder(order) explicitly, or the insert fails on a null
 * order_id (nullable = false) or, worse, attaches the item to the WRONG order.
 *
 * No cascade on customer: a customer outlives its orders, and deleting a customer
 * should not delete what they bought. Same lifetime question as
 * Product.promotions.
 *
 * @PrePersist carries TWO rules:
 *   orderDate - set on the way into the database, then @Column(updatable = false)
 *     stops it ever changing. The `if (orderDate == null)` guard lets a test or
 *     a seeder set a chosen date instead. Same pattern as Customer.createdAt.
 *   the one-item rule - throwing from @PrePersist is the right place, because it
 *     is the last moment before the INSERT, so the order is not half written.
 *     IllegalStateException is unchecked, so no `throws` is needed.
 *
 * TRAP: items has NO initialiser, so a freshly built Order holds null - which is
 * why the check tests `items == null ||` and not just isEmpty(). Same null-vs-
 * empty trap as Category.products and Product.imageUrls.
 *
 * @Enumerated(STRING) because the default ORDINAL stores 0,1,2,3. Beyond being
 * unreadable, reordering the enum constants silently REWRITES EXISTING DATA:
 * insert CANCELLED between PAID and SHIPPED and every shipped order in the table
 * becomes cancelled. That is why OrderStatus is never renumbered.
 *
 * There is deliberately no total/order-amount column. Nothing asks for one and
 * it is derivable (sum of priceAtPurchase * quantity); storing it would mean
 * recalculating on every item change and creates a second source of truth.
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
