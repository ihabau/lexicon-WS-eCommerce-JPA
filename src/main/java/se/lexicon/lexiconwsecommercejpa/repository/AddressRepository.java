package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;

import java.util.*;



import org.springframework.data.jpa.repository.JpaRepository;

/*
 * countCustomersByZipCode is a @Query, and reading the requirement again is the
 * whole reason: "count how many CUSTOMERS live in a given zip code". The column
 * lives on ADDRESSES, but the row being counted is a customer. A derived
 * countByZipCode would run `SELECT count(*) FROM addresses WHERE zip_code = ?` -
 * right column, wrong table - and silently disagree with
 * CustomerRepository.countByAddressCity, which counts customers by walking into
 * address. Two methods, same question, two different answers, is the bug this
 * avoids. When a method name cannot say what you mean, say it in JPQL.
 *
 * The two OPTIONAL queries this file is still missing, both pure derived:
 *   List<Address> findByStreet(String street);
 *       The property is `street`, NOT `streetName` - every capital-letter word
 *       after findBy must be a field that really exists. Address has only
 *       street / city / zipCode. For case-insensitive, the modifier goes at the
 *       END of the property: findByStreetIgnoreCase.
 *   List<Address> findByZipCodeStartingWith(String prefix);
 *       StartingWith is its own keyword (LIKE 'prefix%'), and is a different
 *       thing from findByZipCodeContaining (LIKE '%prefix%') and from
 *       findByZipCode (= exact).
 *
 * NOTE: findByCity / findByZipCode here are case-SENSITIVE, while
 * CustomerRepository has findByAddressCityIgnoreCase and the @Query above uses
 * LOWER(...) on both sides. Being right is not the same as being consistent.
 *
 * README.md still calls this repository "fully implemented" - it is not.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {


  List<Address> findByCity(String city);
  List<Address> findByZipCode(String zipCode);

  @Query("SELECT COUNT(c) FROM Customer c WHERE LOWER(c.address.zipCode) = LOWER(:zipCode)")
  int countCustomersByZipCode(@Param("zipCode") String zipCode);
}
