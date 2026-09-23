package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.*;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  // ==================== REQUIRED (Part1.md:171-173) ====================

  Optional<Customer> findByEmail(String email);

  List<Customer> findByLastNameIgnoreCase(String lastName);

  List<Customer> findByAddressCityIgnoreCase(String city);

  // ==================== OPTIONAL (Part1.md:177-181) ====================

  List<Customer> findByEmailContaining(String keyWord);

  List<Customer> findByCreatedAtAfter(Instant date);
  List<Customer> findByCreatedAtBetween(Instant start, Instant end);

  Long countByAddressCity(String city);

  boolean existsByEmail(String email);

  // ==================== BEYOND THE ASSIGNMENT (add last) ====================

  boolean existsByFirstName(String firstName);
  boolean existsByLastName(String lastName);

  @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:fullName)")
  boolean existsByFullName(@Param("fullName") String fullName);

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
