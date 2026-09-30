package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/*
 * THE MOST IMPORTANT THING IN THIS FILE: `products` HAS NO INITIALISER.
 *     private List<Product> products;
 * Compare Product.promotions one file over, which says `= new ArrayList<>()`, and
 * Promotion.products, which also initialises. This class is the odd one out, and
 * that is a bug waiting to happen:
 *   new Category().getProducts()        -> NullPointerException
 *   new Category().getProducts().add(p) -> NullPointerException
 * Either a collection is null or it is empty, and null is never a state you want
 * to be able to observe. Three ways out: initialise the field (simplest, and fine
 * for a mappedBy side); guard at every use site (what ProductMapper does for
 * imageUrls); or copy in the getter (not worth it here).
 * Nothing breaks TODAY because nothing reads this field: DataSeeder only sets the
 * name and no service returns a Category entity. It will break the first time you
 * write a CategoryService.create() that appends, or a test asserting a new
 * category has no products. Fix it when you write that code.
 *
 * mappedBy, not @JoinColumn: the FK category_id lives in the PRODUCTS table, and
 * the side holding the FK is the owner. So Product is the owner, Category is the
 * inverse, and the inverse says mappedBy = "category" - the exact field name on
 * Product. Writing @JoinColumn here too asks Hibernate to manage the same column
 * twice and it refuses to start.
 *
 * No explicit fetch: the assignment asks for an explicit strategy on
 * Product -> Category and Order -> items, not on this side. And @OneToMany already
 * defaults to LAZY, which is the strongest possible default for a collection. Add
 * fetch = FetchType.LAZY anyway if you prefer not to rely on remembering it.
 *
 * NO cascade here, and that is the point. Deleting a Category must not silently
 * delete every Product filed under it - and one with order_items rows cannot be
 * deleted anyway. A product does not BELONG to a category, it is merely filed
 * under one. Compare Customer.address, which does carry CascadeType.ALL because
 * an address exists only as part of a customer.
 */


   // No @ToString/@EqualsAndHashCode: the `products` collection is cyclic
   // (Product has a back-reference to its category), which would recurse
   // forever in generated toString/equals/hashCode. Equality is the `id`.
   @Getter
   @Setter
   @NoArgsConstructor
   @AllArgsConstructor

   @Entity
   @Table(name = "categories")


public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;


  @OneToMany(mappedBy = "category")
  private List<Product> products;
    

}
