package se.lexicon.lexiconwsecommercejpa.dao;

import se.lexicon.lexiconwsecommercejpa.entity.Address;
import java.util.*;

public interface AddressDAO {

    // Required & optional queries per SpringBoot-DataJPA-Workshop-Part1.md (AddressRepository)

    Address findById(long id);

    List<Address> findByZipCode(String zipCode);                  // required: addresses in a specific zip area

    List<Address> findByCity(String city);                        // optional

    List<Address> findByStreet(String street);                    // optional

    Long countByZipCode(String zipCode);                          // optional: count CUSTOMERS living in a zip

    List<Address> findByZipCodeStartingWith(String prefix);       // optional: zip code starts with a prefix

    Address save(Address address);
    List<Address> findAll();
    Address update(Address address);
    Boolean delete(Address address);
    Boolean deleteById(long id);
    Boolean existByZipCode(String zipCode);




}
