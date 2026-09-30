package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/*
 * startDate/endDate are LocalDate, and that is a real modelling decision: a
 * promotion is valid from a DAY to a DAY. One running 2026-01-01 to 2026-01-31 is
 * valid for the whole of the 31st, and "whole of" is a concept LocalDate has and
 * Instant does not - an Instant would force somebody to decide whether the end is
 * inclusive, and then in which timezone. Everywhere else in this project uses
 * Instant (Order.orderDate, Customer.createdAt) because those are MOMENTS.
 *
 * Consequence: a repository method must take a LocalDate. findByStartDateAfter
 * (LocalDate) resolves; the same name with an Instant parameter will not, because
 * there is no Instant-typed startDate to compare to.
 *
 * mappedBy = "promotions" has the "s" and that is NOT a typo: it names the FIELD
 * on the other entity, and Product's field is plural. Every mappedBy in this
 * project names a real field - "profile" (Customer.profile), "category"
 * (Product.category), "promotions" (Product.promotions, PLURAL), "order"
 * (OrderItem.order). Get one wrong and the app fails at startup, loudly.
 *
 * NO @JoinTable HERE, deliberately. The join table is declared once, by the owner,
 * and Product is the owner - so the table name and both column names exist only
 * in Product. This side contributes one thing: which field on the other side to
 * look at. The rule is not arbitrary: one relationship has one table, and exactly
 * one class owns it.
 *
 * NOTE the unused java.time.Instant import above. It compiles - Java does not
 * error on unused imports - but it is exactly the leftover that makes the next
 * person wonder whether startDate was meant to be an Instant.
 *
 * unique = true on code is not decoration: findByCode is the required lookup, and
 * a lookup key that is not unique returns an Optional that is really a List. The
 * constraint is also what makes the code safe to put in a URL. Customer.email has
 * the same treatment.
 *
 * UNRESOLVED BY DESIGN: endDate being nullable means "open-ended". What price
 * that becomes, and what happens when a promotion has expired, is the optional
 * PromotionService.calculateDiscount - not modelled here, and not required.
 */

  // No @ToString/@EqualsAndHashCode: the `products` collection is cyclic
  // (Product has a back-reference via promotions), which would recurse forever
  // in generated toString/equals/hashCode. Equality is the `id`.
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor

  @Entity
  @Table(name = "promotions")


public class Promotion {
    // TODO: add fields + JPA annotations per the requirements above.


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = true)
  private LocalDate endDate;

  @ManyToMany(mappedBy = "promotions" )
  private List<Product> products = new ArrayList<>();

}
