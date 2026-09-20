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
        return em.find(Address.class, id);
    }

    @Override
    @Transactional
    public Address save(Address address) {
        // TIP: merge() upserts - works for both new (id == null) and existing addresses.
        return em.merge(address);
    }

    @Override
    public List<Address> findAll() {
        return em.createQuery("SELECT a FROM Address a", Address.class)
            .getResultList();
    }

    @Override
    @Transactional
    public Address update(Address address) {
        return em.merge(address);
    }

    @Override
    @Transactional
    public Boolean delete(Address address) {
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
        Address address = em.find(Address.class, id);

        if (address != null) {
            em.remove(address);
            return true;
        }
        return false;
    }

    @Override
    public List<Address> findByZipCode(String zipCode) {
        // TODO: exact match (case-insensitive):
        //      em.createQuery("SELECT a FROM Address a WHERE LOWER(a.zipCode) = LOWER(:zipCode)", Address.class)
        //        .setParameter("zipCode", zipCode.toLowerCase())
        //        .getResultList();
        return null;
    }

    @Override
    public List<Address> findByCity(String city) {
        // TODO: exact match (case-insensitive):
        //      em.createQuery("SELECT a FROM Address a WHERE LOWER(a.city) = LOWER(:city)", Address.class)
        //        .setParameter("city", city.toLowerCase())
        //        .getResultList();
        return null;
    }

    @Override
    public List<Address> findByStreet(String street) {
        // TODO: exact match:
        //      em.createQuery("SELECT a FROM Address a WHERE LOWER(a.street) = LOWER(:street)", Address.class)
        //        .setParameter("street", street.toLowerCase())
        //        .getResultList();
        return null;
    }

    @Override
    public Long countByZipCode(String zipCode) {
        // TODO: counts CUSTOMERS living in the zip, so the query starts from Customer:
        //      em.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.address.zipCode = :zipCode", Long.class)
        //        .setParameter("zipCode", zipCode)
        //        .getSingleResult();
        return 0L;
    }

    @Override
    public List<Address> findByZipCodeStartingWith(String prefix) {
        // TODO: prefix match - bind ":prefix" as prefix + "%":
        //      em.createQuery("SELECT a FROM Address a WHERE LOWER(a.zipCode) LIKE LOWER(:prefix)", Address.class)
        //        .setParameter("prefix", prefix.toLowerCase() + "%")
        //        .getResultList();
        return null;
    }

    @Override
    public Boolean existByZipCode(String zipCode) {
        // TODO: SELECT COUNT(a) FROM Address a WHERE LOWER(a.zipCode) = LOWER(:zipCode)
        //      with Long.class, then return count > 0.
        return false;
    }

}