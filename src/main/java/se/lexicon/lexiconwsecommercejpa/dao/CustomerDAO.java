package se.lexicon.lexiconwsecommercejpa.dao;

import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import java.util.*;
import java.time.Instant;

/*
 * =====================================================================
 * JpaRepository EXAMPLE (how the SAME Customer queries would look with
 * Spring Data instead of the manual DAO pattern used below):
 * =====================================================================
 *
 * public interface CustomerRepository extends JpaRepository<Customer, Long> {
 *
 *     // ----- derived queries: Spring Data parses the method NAME into JPQL -----
 *     Optional<Customer> findByEmail(String email);
 *     List<Customer> findByLastNameIgnoreCase(String lastName);
 *     List<Customer> findByAddressCityIgnoreCase(String city);       // nested field via "_" or "."
 *     List<Customer> findByEmailContaining(String keyword);          // LIKE '%keyword%' generated
 *     List<Customer> findByCreatedAtAfter(Instant date);             // date > one value
 *     List<Customer> findByCreatedAtBetween(Instant from, Instant to);
 *     Long countByAddressCity(String city);                          // returns Long, COUNT generated
 *     boolean existsByEmail(String email);
 *
 *     // ----- custom JPQL for things a method name cannot express -----
 *     @Modifying                        // required for UPDATE/DELETE
 *     @Query("UPDATE Customer c SET c.firstName = :firstName WHERE c.email = :email")
 *     int updateFirstNameByEmail(@Param("firstName") String firstName,
 *                                @Param("email") String email);
 * }
 *
 * What JpaRepository gives you FOR FREE (zero code in the interface):
 *     findById / findAll / count / existsById / deleteById / delete /
 *     save (does merge) / saveAll / flush ...
 *
 * What you give up versus the manual DAO below:
 *     - explicit control: EntityManager merge vs persist, deleting only
 *       a MANAGED instance, returning Boolean from delete
 *     - the *DAOImpl classes disappear - interface only, Spring provides it
 * =====================================================================
 */

public interface CustomerDAO {

    // ----- per-method JpaRepository equivalent -----
    // In JpaRepository: findById(Long) is FREE and returns Optional<Customer>, not Customer.
    Customer findById(long id);
    // JpaRepository: List<Customer> findByFirstNameAndLastName(...) - derived.
    List<Customer> findByFullName(String name);
    // JpaRepository: List<Customer> findByFirstName(...) - derived.
    List<Customer> findByFirstName(String firstName);
    // JpaRepository: List<Customer> findByLastNameIgnoreCase(...) - derived.
    List<Customer> findByLastName(String lastName);
    // JpaRepository: List<Customer> findByAddressCityIgnoreCase(...) - derived.
    List<Customer> findByCity(String city);
    // JpaRepository: Optional<Customer> findByEmail(...) - derived.
    List<Customer> findByEmail(String email);

    // JpaRepository: save() (= persist OR merge) - FREE.
    Customer save(Customer customer);
    // JpaRepository: findAll() - FREE.
    List<Customer> findAll();
    // JpaRepository: no update() method - save() already merges.
    Customer update(Customer customer);
    // JpaRepository: delete() returns void (no boolean). Call existsById() first if you want true/false.
    Boolean delete(Customer customer);
    // JpaRepository: deleteById() - FREE, returns void. Call existsById() first for true/false.
    Boolean deleteById(long id);
    // JpaRepository: long deleteByFirstNameAndLastName(...) - derived DELETE, returns the row count.
    Boolean deleteByFullName(String name);
    // JpaRepository: boolean existsBy(...) - derived (all four below).
    Boolean existByFullName(String name);
    Boolean existByFirstName(String firstName);
    Boolean existByLastName(String lastName);
    Boolean existByEmail(String email);
    // JpaRepository: @Modifying + @Query("UPDATE ...") - a method NAME cannot express these three.
    int updateFirstNameByEmail(String firstName, String email);
    int updateLastNameByEmail(String lastName, String email);
    int updateFullNameByEmail(String firstName, String lastName, String email);
    // The four below are exactly what JpaRepository names become as derived queries:
    //   findByEmailContaining  ->  LIKE '%keyword%'        (works as-is)
    //   findByCreatedAfter     ->  findByCreatedAtAfter(...)
    //   findByCreatedBetween   ->  findByCreatedAtBetween(from, to)
    //   countByCity            ->  countByAddressCity(...) (nested "address.city")
    List<Customer> findByEmailContaining(String keyword);                  // email CONTAINS keyword
    List<Customer> findByCreatedAfter(Instant date);                       // createdAt AFTER one date
    List<Customer> findByCreatedBetween(Instant start, Instant end);       // createdAt BETWEEN two dates
    Long countByCity(String city);                                         // counts customers in a city (Long, NOT List)

    // existByEmail(...) above IS implemented in CostumerDAOImpl.
}
