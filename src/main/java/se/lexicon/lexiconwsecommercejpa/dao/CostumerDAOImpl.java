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
        // TODO: c.name does not exist on Customer - the fields are firstName and lastName.
        // JPQL must reference real entity fields, otherwise the query throws
        // IllegalArgumentException ("Cannot resolve attribute") when it runs.
        return em.createQuery("SELECT c from Customer c where c.name = :name", Customer.class).setParameter("name", name).getResultList();
    }

    @Override
    public List<Customer> findByLastName(String lastName) {
        return em.createQuery("SELECT c from Customer c where LOWER(c.lastName) = LOWER(:lastName)", Customer.class).setParameter("lastName", lastName.toLowerCase()).getResultList();
    }

    @Override
    public List<Customer> findByEmail(String email) {
        return em.createQuery("SELECT c from Customer c where c.email = :email", Customer.class).setParameter("email", email).getResultList();
    }

    @Override
    public List<Customer> findByCity(String city) {
        return em.createQuery("SELECT c from Customer c where LOWER(c.address.city) = LOWER(:city)", Customer.class).setParameter("city", city.toLowerCase()).getResultList();
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
