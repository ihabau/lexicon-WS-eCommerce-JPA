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
}
