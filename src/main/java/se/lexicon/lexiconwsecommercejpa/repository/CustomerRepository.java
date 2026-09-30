package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.*;

/*
 * findByAddressCityIgnoreCase - a NESTED property, and the point of this file.
 * Customer has no `city` field; the city lives on the Address it owns, so the
 * name walks the association: Customer -> Address -> city. Spring Data splits the
 * name on capital letters and resolves each part as a property of the previous
 * one, so AddressCity is read as address.city. Any field in the path works -
 * findByAddressZipCode would too.
 *
 * NOTE: countByAddressCity below has NO IgnoreCase, so it IS case-sensitive
 * while the finder is not. Both are correct answers; the inconsistency is the
 * problem, and it stays invisible until a test compares a count against a list
 * length and they disagree. The fix is one keyword: countByAddressCityIgnoreCase.
 *
 * @Param is technically optional - Spring Data binds by parameter position - but
 * it is needed for readability and for any method whose parameters get reordered.
 * The derived methods below skip it because they bind from the name.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  // ==================== REQUIRED ====================

  Optional<Customer> findByEmail(String email);

  List<Customer> findByLastNameIgnoreCase(String lastName);

  List<Customer> findByAddressCityIgnoreCase(String city);

  // ==================== OPTIONAL ====================

  List<Customer> findByEmailContaining(String keyWord);

  List<Customer> findByCreatedAtAfter(Instant date);
  List<Customer> findByCreatedAtBetween(Instant start, Instant end);

  // CASE-SENSITIVE, unlike the finder above. See the note at the top.
  Long countByAddressCity(String city);

  boolean existsByEmail(String email);

  // ==================== BEYOND THE ASSIGNMENT ====================
  // Nothing here is wrong, but a teacher asking "explain this method" will pick
  // from the bottom of a file, so be ready to justify each line.

  boolean existsByFirstName(String firstName);
  boolean existsByLastName(String lastName);

  // CANNOT be derived, and the @Query is not decoration. There is no `fullName`
  // field - only firstName and lastName - so parsing a derived name fails at
  // startup naming the missing property. The JPQL spells the two steps out:
  // CONCAT the columns, LOWER both sides, compare. Same rule as
  // PromotionRepository.findActivePromo.
  @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:fullName)")
  boolean existsByFullName(@Param("fullName") String fullName);

  // @Modifying + @Transactional on a bulk write is not bookkeeping, it is
  // required. A SELECT runs inside the repository's own read transaction; a bulk
  // UPDATE does not, so without @Transactional you get "no transaction is in
  // progress" or a stale first-level cache.
  //   @Modifying  - this is a write, not a SELECT.
  //   clearAutomatically - evicts entities already in the session, otherwise a
  //     findById later in the same transaction returns the OLD cached value and
  //     the update looks like it never happened.
  //   @Transactional - which transaction the write joins.
  // Note this is jakarta.transaction.Transactional. That and Spring's
  // org.springframework.transaction.annotation.Transactional both work on a
  // repository, and are NOT interchangeable in the service layer - see
  // OrderServiceImpl, where the Spring one is the right choice.
  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Customer c SET c.firstName = :firstName WHERE LOWER(c.email) = LOWER(:email)")
  Boolean updateFirstNameByEmail(@Param("firstName") String firstName, @Param("email") String email);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Customer c SET c.lastName = :lastName WHERE LOWER(c.email) = LOWER(:email)")
  Boolean updateLastNameByEmail(@Param("lastName") String lastName, @Param("email") String email);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Customer c SET c.firstName = :firstName, c.lastName = :lastName WHERE LOWER(c.email) = LOWER(:email)")
  Boolean updateFullNameByEmail(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("email") String email);

  @Transactional
  @Query("SELECT COUNT(c) FROM Customer c WHERE LOWER(c.firstName) = Lower(:firstName)")
  int countByFirstName(@Param("firstName") String firstName);
}
