package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =====================================================================
 * ADDRESSREPOSITORY - Part 1 (workshop Part1.md:201-212).
 *
 * A Spring Data repository is an INTERFACE that extends JpaRepository<T, ID>.
 * Spring Data generates the implementation at RUNTIME - you never write a
 * CustomerDAOImpl-style class again.
 *
 * Inherited for free (do NOT redeclare): findById, findAll, save, saveAll,
 * deleteById, delete, count, existsById.
 *
 * Declared methods are derived from their NAME - add the ones under TODO.
 * =====================================================================
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

    /*
     * TODO (REQUIRED - Part1.md:205): find all addresses in a zip area.
     *   List<Address> findByZipCode(String zipCode);
     *
     *   Derived: property "zipCode" + exact match. No IgnoreCase needed -
     *   zip codes are numeric strings.
     */

    /*
     * TODO (OPTIONAL - Part1.md:209): all addresses in a city.
     *   List<Address> findByCity(String city);
     */

    /*
     * TODO (OPTIONAL - Part1.md:210): addresses by street name.
     *   List<Address> findByStreet(String street);
     */

    /*
     * TODO (OPTIONAL - Part1.md:211): COUNT how many CUSTOMERS live in a zip.
     *   A derived countByZipCode on THIS repository would count addresses, not
     *   customers - the question crosses entities, so uncomment/annotate:
     *   @Query("SELECT COUNT(c) FROM Customer c WHERE c.address.zipCode = :zipCode")
     *   Long countCustomersInZipCode(String zipCode);
     */

    /*
     * TODO (OPTIONAL - Part1.md:212): zip codes starting with a prefix.
     *   List<Address> findByZipCodeStartingWith(String prefix);
     *   "StartingWith" === LIKE 'prefix%'.
     */

    /*
     * =====================================================================
     * EXTRA - carried over from the old AddressDAO, BEYOND the assignment.
     * Add these AFTER finishing the workshop.
     * =====================================================================
     *   // derived existence check (note "existsBy", not "existBy"):
     *   boolean existsByZipCode(String zipCode);
     */
}