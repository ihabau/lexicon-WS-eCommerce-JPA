package se.lexicon.lexiconwsecommercejpa.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * ONE NAMING DECISION TO BE READY TO EXPLAIN: `nickName` vs `nickname`.
 * The assignment spells this field `nickname`; this project uses `nickName`.
 * Nothing is broken by either - the column is `nick_name` either way, because
 * Hibernate converts camelCase to snake_case automatically - but it has one real
 * consequence: A DERIVED QUERY MUST SPELL THE FIELD THE WAY THE JAVA FIELD IS
 * SPELLED. So `findByNickName` resolves and `findByNickname` would not (it stops
 * the app at startup). UserProfileRepository is written for `nickName`, so the
 * two agree. If you ever want to match the spec exactly, rename the field AND
 * every method that spells it - never one without the other.
 *
 * No createdAt here, deliberately: Part 1's optional "profiles created after a
 * date (if applicable)" has nothing to filter on, and the assignment never asks
 * for the column. Adding one would mean an Instant field plus a @PrePersist hook,
 * exactly as Customer.createdAt does.
 *
 * For contrast, Order.status uses @Enumerated(EnumType.STRING), so a status is
 * stored as "CREATED" rather than 0 and can be read straight out of the database.
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
