package se.lexicon.lexiconwsecommercejpa.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.TypedQuery; // Optional: If you assign the query to a variable
import org.springframework.stereotype.Repository;
import java.util.*;
import se.lexicon.lexiconwsecommercejpa.entity.Customer;
import java.time.Instant;

@Repository
public class CostumerDAOImpl implements CustomerDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Customer findById(long id) {
        return em.find(Customer.class, id);
    }

    @Override
    public List<Customer> findByFirstName(String firstName) {
        return em.createQuery("SELECT c from Customer c WHERE LOWER(c.firstName) = LOWER(:firstName)", Customer.class)
            .setParameter("firstName", firstName).getResultList();
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        return em.merge(customer);
    }

    @Override
    public List<Customer> findAll() {
        return em.createQuery("SELECT c from Customer c", Customer.class)
            .getResultList();
    }

    @Override
    public List<Customer> findByFullName(String name) {
        return em.createQuery("SELECT c from Customer c where LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:name)", Customer.class)
            .setParameter("name", name.toLowerCase())
            .getResultList();
    }

    @Override
    public List<Customer> findByLastName(String lastName) {
        return em.createQuery("SELECT c from Customer c where LOWER(c.lastName) = LOWER(:lastName)", Customer.class)
            .setParameter("lastName", lastName.toLowerCase())
            .getResultList();
    }

    @Override
    public List<Customer> findByEmail(String email) {
        return em.createQuery("SELECT c from Customer c where c.email = :email", Customer.class)
            .setParameter("email", email)
            .getResultList();
    }

    @Override
    public List<Customer> findByCity(String city) {
        return em.createQuery("SELECT c from Customer c where LOWER(c.address.city) = LOWER(:city)", Customer.class)
            .setParameter("city", city.toLowerCase())
            .getResultList();
    }

    @Override
    @Transactional
    public Customer update(Customer customer) {
        return em.merge(customer);
    }

    @Override
    @Transactional
    public Boolean delete(Customer customer) {
        Customer c = em.find(Customer.class, customer.getId());

        if (c != null) {
            em.remove(c);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteById(long id) {
        Customer customer = em.find(Customer.class, id);

        if (customer != null) {
            em.remove(customer);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteByFullName(String name) {
        // TIP: this is a bulk delete - it removes EVERY customer with that exact full name.
        int deletedRows = em.createQuery("DELETE FROM Customer c where LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:name)")
            .setParameter("name", name.toLowerCase())
            .executeUpdate();

        return deletedRows > 0;
    }

    @Override
    public Boolean existByFirstName(String firstName) {
        Long count = em.createQuery("SELECT COUNT(c) FROM Customer c WHERE LOWER(c.firstName) = LOWER(:firstName)", Long.class)
            .setParameter("firstName", firstName.toLowerCase())
            .getSingleResult();

        return count > 0;
    }

    @Override
    public Boolean existByLastName(String lastName) {
        Long count = em.createQuery("SELECT COUNT(c) FROM Customer c WHERE LOWER(c.lastName) = LOWER(:lastName)", Long.class)
            .setParameter("lastName", lastName.toLowerCase())
            .getSingleResult();

        return count > 0;
    }

    @Override
    public Boolean existByFullName(String name) {
        // TIP: the full-name check is exact (no wildcards) - binds LOWER(:name) directly.
        Long count = em.createQuery("SELECT COUNT(c) from Customer c where LOWER(CONCAT(c.firstName, ' ', c.lastName)) = LOWER(:name)", Long.class)
            .setParameter("name", name.toLowerCase())
            .getSingleResult();

        return count > 0;
    }

    @Override
    public Boolean existByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(c) from Customer c where LOWER(c.email) = LOWER(:email)", Long.class)
            .setParameter("email", email.toLowerCase())
            .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional
    public int updateFirstNameByEmail(String firstName, String email) {
        return em.createQuery("UPDATE Customer c SET c.firstName = :firstName WHERE LOWER(c.email) = LOWER(:email)")
            .setParameter("firstName", firstName)
            .setParameter("email", email.toLowerCase())
            .executeUpdate();
    }

    @Override
    @Transactional
    public int updateLastNameByEmail(String lastName, String email) {
        return em.createQuery("UPDATE Customer c SET c.lastName = :lastName WHERE LOWER(c.email) = LOWER(:email)")
            .setParameter("lastName", lastName)
            .setParameter("email", email.toLowerCase())
            .executeUpdate();
    }

    @Override
    @Transactional
    public int updateFullNameByEmail(String firstName, String lastName, String email) {
        return em.createQuery("UPDATE Customer c SET c.firstName = :firstName, c.lastName = :lastName WHERE LOWER(c.email) = LOWER(:email)")
            .setParameter("firstName", firstName)
            .setParameter("lastName", lastName)
            .setParameter("email", email.toLowerCase())
            .executeUpdate();
    }

    @Override
    public List<Customer> findByEmailContaining(String keyword) {
        return em.createQuery("SELECT c FROM Customer c WHERE LOWER(c.email) LIKE LOWER(CONCAT( '%', :keyword, '%' ))", Customer.class)
            .setParameter("keyword", keyword)
            .getResultList();

    }

    @Override
    public List<Customer> findByCreatedAfter(Instant date) {
        return em.createQuery("SELECT c FROM Customer c WHERE c.createdAt > :date", Customer.class)
            .setParameter("date", date)
            .getResultList();
    }

    @Override
    public List<Customer> findByCreatedBetween(Instant start, Instant end) {
        return em.createQuery("SELECT c FROM Customer c WHERE c.createdAt BETWEEN :start AND :end", Customer.class)
            .setParameter("start", start)
            .setParameter("end", end)
            .getResultList();
    }

    @Override
    public Long countByCity(String city) {
        return em.createQuery("SELECT COUNT(c) FROM Customer c WHERE LOWER(c.address.city) = LOWER(:city)", Long.class)
            .setParameter("city", city.toLowerCase())
            .getSingleResult();
    }

}
