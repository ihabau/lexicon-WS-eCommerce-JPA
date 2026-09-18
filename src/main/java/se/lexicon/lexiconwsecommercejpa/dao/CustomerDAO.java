package se.lexicon.lexiconwsecommercejpa.dao;

import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import java.util.*;

public interface CustomerDAO {

    Customer findById(long id);
    List<Customer> findByName(String name);
    List<Customer> findByFirstName(String firstName);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByCity(String city);
    List<Customer> findByEmail(String email);

    Customer save(Customer customer);
    List<Customer> findAll();
    Customer update(Customer customer);
    void delete(Customer customer);
    Boolean deleteById(long id);
    Boolean deleteByName(String name);
    Boolean existByName(String name);
    Boolean existByEmail(String email);
    int updateNameByEmail(String name, String email);
    // some of these where completed by autocomplete. Spring??

    // Missing optional queries (workshop "CustomerRepository optional", SpringBoot-DataJPA-Workshop-Part1.md):
    // - findByEmailContaining(String keyword)                -> email contains keyword
    // - findByCreatedAfter(Instant date)                     -> created after ONE date   (needs import java.time.Instant)
    // - findByCreatedBetween(Instant start, Instant end)     -> created between TWO dates (needs import java.time.Instant)
    // - countByCity(String city)                             -> returns long/Long, NOT List<Customer>
    // - existByEmail(...) is declared above but has no implementation in CostumerDAOImpl yet
}
