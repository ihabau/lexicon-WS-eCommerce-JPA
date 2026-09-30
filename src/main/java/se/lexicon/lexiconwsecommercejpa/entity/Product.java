package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;

/*
 * @ElementCollection - the one annotation that creates a table WITHOUT another
 * entity. imageUrls is a List<String> and there is no Image entity in this
 * project, so this tells JPA the collection deserves a table of its own whose
 * only columns are the owner's FK and the element:
 *     product_images (product_id BIGINT FK -> products.id, image_url VARCHAR)
 *
 * Why an element collection rather than a @ManyToMany: a @ManyToMany needs two
 * entities with their own ids and columns. An image url is a VALUE, not a thing
 * - it has no identity of its own. The test to apply: does the thing on the other
 * end need its own row and columns? Promotion does; an image url does not.
 *
 * TRAP: this field has NO initialiser, so a new Product holds null here, and
 * Hibernate refuses to save a null @ElementCollection. It must be an empty list
 * meaning "no photos", never null. Compare promotions below, which does say
 * `= new ArrayList<>()`; that asymmetry is the trap.
 *
 * @ManyToOne(fetch = LAZY) - the @ManyToOne default is EAGER, so writing LAZY
 * is real work, not a no-op. Lazy is right here: listing products should not
 * also issue one query per product to fetch its category. The cost is that a
 * lazy association can only be read while the session is open - see
 * ProductRepository's @EntityGraph, which is how this gets mapped afterwards.
 *
 * @ManyToMany with NO CascadeType.ALL, deliberately. A promotion is SHARED: it
 * applies to many products and has its own dates, so it outlives any single
 * product. With CascadeType.ALL, deleting one product would delete the promotion
 * and with it every other product's discount - data loss on the first delete.
 *
 * Product is the OWNER, which is why @JoinTable lives HERE and not on Promotion:
 *   joinColumns        = FK back to THIS side -> product_id
 *   inverseJoinColumns = FK to the other side -> promotion_id
 * Get those two the wrong way round and the schema generates happily with
 * nonsense data. Promotion.products then only needs mappedBy = "promotions".
 *
 * The List collection type is allowed but worth a question: a @ManyToMany is
 * semantically a Set, and a List implies an ORDER this relationship does not
 * have. Two reads of the same product can hand you the promotions in different
 * orders, which is a real source of flaky tests. Fix is @OrderColumn, or
 * Set/LinkedHashSet.
 *
 * price: precision 10 / scale 2 is DECIMAL(10,2), up to 99 999 999.99. Money is
 * never a double - 0.1 + 0.2 is not 0.3 in binary floating point. This also
 * lines up with @Digits(integer = 8, fraction = 2) on ProductRequest: the same
 * rule written twice, once in the DTO so the client gets a clean 400 and once on
 * the column so the database is the last line of defence. 10 - 2 = 8.
 */

  // No @ToString/@EqualsAndHashCode: category (Category has a back-reference
  // via products) and promotions (Promotion has a back-reference via products)
  // are cyclic, so generated toString/equals/hashCode would recurse forever.
  // Equality for a managed entity is its `id`.
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor

  @Entity
  @Table(name = "products")



public class Product {
    // TODO: add fields + JPA annotations per the requirements above.

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  // From here i dont understand correctly ia helped!
  @ElementCollection
  @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "image_url", nullable = false)
  private List<String> imageUrls;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "products_promotions",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "promotion_id")
  )
  private List<Promotion> promotions = new ArrayList<>();

}
