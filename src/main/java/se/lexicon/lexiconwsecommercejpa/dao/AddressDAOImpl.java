package se.lexicon.lexiconwsecommercejpa.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import se.lexicon.lexiconwsecommercejpa.entity.Address;
import java.util.*;

@Repository
public class AddressDAOImpl implements AddressDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Address findById(long id) {
        // same pattern as CustomerDAOImpl.findById
        return em.find(Address.class, id);
    }

    @Override
    @Transactional
    public Address save(Address address) {
        // same pattern as CustomerDAOImpl.save - merge upserts new and existing rows
        return em.merge(address);
    }

    @Override
    public List<Address> findAll() {
        // same pattern as CustomerDAOImpl.findAll
        return em.createQuery("SELECT a from Address a", Address.class)
            .getResultList();
    }

    @Override
    @Transactional
    public Address update(Address address) {
        // same pattern as CustomerDAOImpl.update
        return em.merge(address);
    }

    @Override
    @Transactional
    public Boolean delete(Address address) {
        // same pattern as CustomerDAOImpl.delete - remove the MANAGED instance, not the passed one
        Address managed = em.find(Address.class, address.getId());

        if (managed != null) {
            em.remove(managed);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteById(long id) {
        // same pattern as CustomerDAOImpl.deleteById
        Address address = em.find(Address.class, id);

        if (address != null) {
            em.remove(address);
            return true;
        }
        return false;
    }

    @Override
    public List<Address> findByZipCode(String zipCode) {
        // TODO: exact, case-insensitive match on address zip code.
        // TIP (JPQL): SELECT a FROM Address a WHERE LOWER(a.zipCode) = LOWER(:zipCode)
        //      - exactly like CustomerDAOImpl.findByLastName.
        return null;
    }

    @Override
    public List<Address> findByCity(String city) {
        // TODO: exact, case-insensitive match on address city.
        // TIP (JPQL): SELECT a FROM Address a WHERE LOWER(a.city) = LOWER(:city)
        //      - like CustomerDAOImpl.findByCity (but without the c.address. prefix).
        return null;
    }

    @Override
    public List<Address> findByStreet(String street) {
        // TODO: exact match on street name.
        // TIP (JPQL): SELECT a FROM Address a WHERE LOWER(a.street) = LOWER(:street),
        //      typed as Address.class, .setParameter(...).getResultList().
        return null;
    }

    @Override
    public Long countByZipCode(String zipCode) {
        // TODO: counts CUSTOMERS living in the zip, so the query starts from Customer:
        //      SELECT COUNT(c) FROM Customer c WHERE c.address.zipCode = :zipCode
        //      typed as Long.class - the alias pattern is from CustomerDAOImpl.countByCity.
        return 0L;
    }

    @Override
    public List<Address> findByZipCodeStartingWith(String prefix) {
        // TODO: prefix match - bind ":prefix" as prefix + "%".
        // TIP (JPQL): SELECT a FROM Address a WHERE LOWER(a.zipCode) LIKE LOWER(:prefix)
        //      - mirror the contains-style binding from CustomerDAOImpl.findByEmailContaining.
        return null;
    }

    @Override
    public Boolean existByZipCode(String zipCode) {
        // TODO: SELECT COUNT(a) FROM Address a WHERE LOWER(a.zipCode) = LOWER(:zipCode)
        //      typed as Long.class, then return count > 0 -
        //      exactly the existByFirstName pattern in CustomerDAOImpl.
        return false;
    }

}