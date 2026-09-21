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
        // now correct - balanced parentheses, LOWER on each side of "=", params match
        return em.createQuery("SELECT a FROM Address a WHERE LOWER(a.zipCode) = LOWER(:zipCode)", Address.class)
            .setParameter("zipCode", zipCode.toLowerCase())
            .getResultList();
    }

    @Override
    public List<Address> findByCity(String city) {
        // now correct - balanced parentheses, LOWER on each side of "=", params match
        return em.createQuery("SELECT a FROM Address a WHERE LOWER(a.city) = LOWER(:city)", Address.class)
            .setParameter("city", city.toLowerCase())
            .getResultList();
    }

    @Override
    public List<Address> findByStreet(String street) {
        // now correct - balanced parentheses, LOWER on each side of "=", params match
        return em.createQuery("SELECT a FROM Address a WHERE LOWER(a.street) = LOWER(:street)", Address.class)
            .setParameter("street", street.toLowerCase())
            .getResultList();
    }

    @Override
    public Long countByZipCode(String zipCode) {
        // now correct - counts customers via c.address (Long.class, single result)
        Long count = em.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.address.zipCode = :zipCode", Long.class)
            .setParameter("zipCode", zipCode)
            .getSingleResult();

        return count;
    }

    @Override
    public List<Address> findByZipCodeStartingWith(String prefix) {
        // now correct - LIKE with CONCAT prefix, params match
        return em.createQuery("SELECT a FROM Address a WHERE LOWER(a.zipCode) LIKE LOWER(CONCAT( :prefix, '%' ))", Address.class)
            .setParameter("prefix", prefix)
            .getResultList();
    }

    @Override
    public Boolean existByZipCode(String zipCode) {
        // now correct - Long.class + count > 0, case-insensitive match
        Long count = em.createQuery("SELECT COUNT(a) FROM Address a WHERE LOWER(a.zipCode) = LOWER(:zipCode)", Long.class)
            .setParameter("zipCode", zipCode.toLowerCase())
            .getSingleResult();

        return count > 0;
    }

}
