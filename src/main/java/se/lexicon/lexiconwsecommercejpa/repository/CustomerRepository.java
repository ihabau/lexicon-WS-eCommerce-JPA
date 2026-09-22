package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * CUSTOMERREPOSITORY - Part 1 (workshop Part1.md:167-181).
 *
 * The old CustomerDAO + CostumerDAOImpl (backed up in /backup/dao) had every
 * query written by hand. A Spring Data repository replaces BOTH files with
 * one interface - method names become queries at STARTUP (a wrong name fails
 * fast instead of at runtime).
 *
 * CRUD inherited for free: findById, findAll, save, saveAll, deleteById,
 * delete, count, existsById. The old "update" was just em.merge() = save().
 * =====================================================================
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /*
     * TODO (REQUIRED - Part1.md:171): find the customer by email.
     *   Optional<Customer> findByEmail(String email);
     *   TIP: email is UNIQUE in the DB -> Optional (the DAO returned List,
     *   which did not express that "at most one" guarantee).
     */

    /*
     * TODO (REQUIRED - Part1.md:172): by last name, case-insensitive.
     *   List<Customer> findByLastNameIgnoreCase(String lastName);
     *   TIP: "IgnoreCase" replaces the old LOWER(...) = LOWER(:x) JPQL.
     */

    /*
     * TODO (REQUIRED - Part1.md:173): customers living in a city.
     *   List<Customer> findByAddressCityIgnoreCase(String city);
     *   TIP: nested property path (Customer -> Address -> city), ONLY possible
     *   after Customer.address and Address.city exist (they were just scaffolded).
     */

    /*
     * TODO (OPTIONAL - Part1.md:177): email contains a keyword.
     *   List<Customer> findByEmailContaining(String keyword);
     *   "Containing" === LIKE '%keyword%'.
     */

    /*
     * TODO (OPTIONAL - Part1.md:178-179): created after / between dates.
     *   List<Customer> findByCreatedAtAfter(Instant date);
     *   List<Customer> findByCreatedAtBetween(Instant start, Instant end);
     *   TIP: Spring Data parses the FIELD name - it is "createdAt", so the
     *   method says "CreatedAt" (the old DAO named it findByCreatedAfter).
     */

    /*
     * TODO (OPTIONAL - Part1.md:180): count customers in a city.
     *   Long countByAddressCity(String city);
     *   TIP: returns Long, NOT a list.
     */

    /*
     * TODO (OPTIONAL - Part1.md:181): does a customer with this email exist?
     *   boolean existsByEmail(String email);
     *   (also existsByFirstName / existsByLastName are easy additions.)
     */

    /*
     * TODO (extra, from the old DAO): find by first name.
     *   List<Customer> findByFirstNameIgnoreCase(String firstName);
     */

    /*
     * NOT EXPRESSIBLE BY NAME - needs @Query (recreate only if you need them):
     *
     * - findByFullName: there is NO fullName column, it is firstName + lastName.
     *   Needs CONCAT, e.g.:
     *   @Query("SELECT c FROM Customer c WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:name)")
     *   List<Customer> findByFullName(@Param("name") String name);
     *
     * - Bulk updates (updateFirstNameByEmail etc.) need @Modifying + @Query.
     *   TIP: bulk UPDATE/DELETE bypass the persistence context - stale cached
     *   entities may come back until you clear the EntityManager.
     */

    /*
     * =====================================================================
     * EXTRA - carried over from the old CustomerDAO, BEYOND the assignment.
     * Add these AFTER finishing the workshop (Part2.md:383 checklist done).
     * =====================================================================
     *   boolean existsByFirstName(String firstName);     // derived
     *   boolean existsByLastName(String lastName);       // derived
     *
     *   // exists by full name -> NOT derivable (CONCAT), needs @Query:
     *   // @Query("SELECT COUNT(c) FROM Customer c " +
     *   //        "WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:name)")
     *   // boolean existsByFullName(@Param("name") String name);
     *
     *   // delete by full name -> bulk delete, needs @Modifying + @Query:
     *   // @Modifying
     *   // @Query("DELETE FROM Customer c " +
     *   //        "WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:name)")
     *   // int deleteByFullName(@Param("name") String name);
     *
     *   // bulk renames by email - each needs @Modifying + @Query:
     *   // int updateFirstNameByEmail(String firstName, String email);
     *   // int updateLastNameByEmail(String lastName, String email);
     *   // int updateFullNameByEmail(String firstName, String lastName, String email);
     */
}