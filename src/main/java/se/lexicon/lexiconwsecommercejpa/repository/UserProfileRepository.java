package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

/*
 * "Find profiles created after a specific date (if applicable)" is missing, and
 * the spec's own "(if applicable)" is the reason: UserProfile has four columns
 * (id, nickName, phoneNumber, bio) and NOT ONE is a date, so the question cannot
 * be asked of this entity. A derived query is a SPELLING of a field that exists;
 * a query you cannot write is a signal that the MODEL is missing something. The
 * fix would be a createdAt column plus a @PrePersist hook, as Customer has.
 *
 * The four below, and why each name is what it is:
 *   findByNickName(nickName)            - exact, case-sensitive. Insensitive
 *                                         sibling: findByNickNameIgnoreCase.
 *   findByPhoneNumberContaining(part)   - "partial" means the MIDDLE of the
 *                                         value, so Containing (LIKE '%v%'),
 *                                         not StartingWith.
 *   findByBioIsNotNull()                - the field comes FIRST, then the
 *                                         condition: findByBioIsNotNull, never
 *                                         findByIsNotNullBio. Spring Data parses
 *                                         right-to-left, stripping keywords off
 *                                         the end until it hits a real property.
 *   findByNickNameStartingWith(prefix)  - LIKE 'prefix%'. A different query from
 *                                         both Containing and an exact match.
 *   countByPhoneNumberStartingWith(...) - same query, different return type:
 *                                         `countBy` makes Spring Data return a
 *                                         single number and add a COUNT to the SQL.
 *
 * Naming a parameter phoneNumberPart is a free readability win - the signature
 * in an IDE then says what it wants. Spring Data ignores parameter names, so it
 * cannot break the query.
 *
 * THE RULE THIS WHOLE FILE IS AN EXAMPLE OF: if a method name can say it, do NOT
 * write a @Query. A derived query is validated at startup, so a typo fails
 * immediately with a message naming the method, while hand-written JPQL is not
 * checked until the query is first called.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

  // REQUIRED: find a profile by nickname.
  Optional<UserProfile> findByNickName(String nickName);

  // REQUIRED: search for profiles by a partial phone number.
  List<UserProfile> findByPhoneNumberContaining(String phoneNumberPart);

  // OPTIONAL: bio is not null / nickname starts with a prefix.
  List<UserProfile> findByBioIsNotNull();
  List<UserProfile> findByNickNameStartingWith(String prefix);

  // OPTIONAL: count profiles with a phone number prefix.
  Long countByPhoneNumberStartingWith(String prefix);
}
