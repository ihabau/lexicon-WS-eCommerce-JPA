package se.lexicon.lexiconwsecommercejpa.dao;

import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import java.util.*;
import java.time.Instant;

public interface CustomerDAO {

    Customer findById(long id);
    List<Customer> findByFullName(String name);
    List<Customer> findByFirstName(String firstName);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByCity(String city);
    List<Customer> findByEmail(String email);

    Customer save(Customer customer);
    List<Customer> findAll();
    Customer update(Customer customer);
    Boolean delete(Customer customer);
    Boolean deleteById(long id);
    Boolean deleteByFullName(String name);
    Boolean existByFullName(String name);
    Boolean existByFirstName(String firstName);
    Boolean existByLastName(String lastName);
    Boolean existByEmail(String email);
    int updateFirstNameByEmail(String firstName, String email);
    int updateLastNameByEmail(String lastName, String email);
    int updateFullNameByEmail(String firstName, String lastName, String email);
    // Spring Data derived queries still to implement (optional - Part1 workshop).
    // Declared as empty TODO stubs in CostumerDAOImpl.
    List<Customer> findByEmailContaining(String keyword);                  // email CONTAINS keyword
    List<Customer> findByCreatedAfter(Instant date);                       // createdAt AFTER one date
    List<Customer> findByCreatedBetween(Instant start, Instant end);       // createdAt BETWEEN two dates
    Long countByCity(String city);                                         // counts customers in a city (Long, NOT List)

    // existByEmail(...) above IS implemented in CostumerDAOImpl.
}
