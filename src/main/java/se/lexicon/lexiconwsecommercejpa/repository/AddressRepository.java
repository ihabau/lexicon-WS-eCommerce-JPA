package se.lexicon.lexiconwsecommercejpa.repository;

import se.lexicon.lexiconwsecommercejpa.entity.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;

import java.util.*;



import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {


  List<Address> findByCity(String city);
  List<Address> findByZipCode(String zipCode);

  @Query("SELECT COUNT(c) FROM Customer c WHERE LOWER(c.address.zipCode) = LOWER(:zipCode)")
  int countCustomersByZipCode(@Param("zipCode") String zipCode);
}
