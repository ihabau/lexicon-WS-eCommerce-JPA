package se.lexicon.lexiconwsecommercejpa.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.*;
import se.lexicon.lexiconwsecommercejpa.entity.Customer;

@Repository
public class CostumerDAOImpl implements CustomerDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Customer findById(long id) {
        return null;
    }

    @Override
    public List<Customer> findByFirstName(String firstName) {
        return null;
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            em.persist(customer);
            return customer;
        }
        return null;
    }

    @Override
    public List<Customer> findAll() {

        return em.createQuery("SELECT c from Customer c", Customer.class).getResultList();
    }

    @Override
    public List<Customer> findByName(String name) {
        return em.createQuery("SELECT c from Customer c where c.name = :name", Customer.class).setParameter("name", name).getResultList();
    }

    @Override
    public List<Customer> findByLastName(String lastName) {
        // TODO: fix this yourself - three problems:
        // 1. createNamedQuery(...) is only for queries pre-declared with @NamedQuery on the entity.
        //    For a JPQL string you write in code you must use em.createQuery(...).
        // 2. The workshop wants CASE-INSENSITIVE matching: think about LOWER(...) on both sides.
        // 3. Whatever placeholder you write in the JPQL (:lastName) must be bound with setParameter.
        return em.createNamedQuery("SELECT c from Customer c where c.lastName = :lastName", Customer.class).setParameter("lastName", lastName).getResultList();
    }

    @Override
    public List<Customer> findByEmail(String email) {
        // TODO: the JPQL declares a placeholder :email but it never gets bound.
        // Every placeholder in the query string must be bound with em.setParameter("email", ...)
        // before getResultList(), otherwise JPA throws IllegalArgumentException at runtime.
        return em.createQuery("SELECT c from Customer c where c.email = :email", Customer.class).getResultList();
    }

    @Override
    public List<Customer> findByCity(String city) {
        // TODO: two problems:
        // 1. The query filters on c.email but you bind ":city" - the names don't match.
        //    The placeholder in the JPQL and the setParameter name must always be the same.
        // 2. city doesn't live on Customer. Customers reference an Address, so to find customers
        //    in a city you must navigate through the association: c.address.city.
        return em.createQuery("SELECT c from Customer c where c.email = :email", Customer.class).setParameter("city", city).getResultList();
    }

    @Override
    public Customer update(Customer customer) {
        return em.merge(customer);
    }

    @Override
    public void delete(Customer customer) {

    }

    @Override
    public Boolean deleteById(long id) {
        return null;
    }

    @Override
    public Boolean deleteByName(String name) {
        return null;
    }

    @Override
    public Boolean existByName(String name) {
        return null;
    }

    @Override
    public Boolean existByEmail(String email) {
        return null;
    }

    @Override
    public int updateNameByEmail(String name, String email) {
        return 0;
    }

}
