# DAO vs Spring Data JpaRepository — Customer as the example

This file compares the **manual DAO pattern** currently in the project against the **Spring Data
`JpaRepository` pattern** the workshop actually asks for (Part1.md:156-157, Part2.md:277-285).

Chosen example: the **Customer** queries. The same comparison applies to every entity.

---

## 1. What the documentation wants

> "Create the repository interfaces for the entities. Each interface should extend `JpaRepository`
> and include the specified query methods." — Part1.md:156-157

> "You are expected to use Spring Data JPA query method naming and, where appropriate, custom queries."
> — Part2.md:281

So the workshop wants **one interface per entity**, extending `JpaRepository`. No implementation class.

---

## 2. Current project: the DAO pattern (2 files)

### `dao/CustomerDAO.java` (interface)

```java
public interface CustomerDAO {

    Customer findById(long id);
    List<Customer> findByFullName(String name);
    List<Customer> findByFirstName(String firstName);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByCity(String city);
    List<Customer> findByEmail(String email);

    Customer save(Customer customer);
    List<Customer> findAll();
    Customer update(Customer customer);
    Boolean delete(Customer customer);
    Boolean deleteById(long id);
    Boolean deleteByFullName(String name);

    Boolean existByFullName(String name);
    Boolean existByFirstName(String firstName);
    Boolean existByLastName(String lastName);
    Boolean existByEmail(String email);

    int updateFirstNameByEmail(String firstName, String email);
    int updateLastNameByEmail(String lastName, String email);
    int updateFullNameByEmail(String firstName, String lastName, String email);

    List<Customer> findByEmailContaining(String keyword);
    List<Customer> findByCreatedAfter(Instant date);
    List<Customer> findByCreatedBetween(Instant start, Instant end);
    Long countByCity(String city);
}
```

### `dao/CostumerDAOImpl.java` (implementation, ~200 lines)

```java
@Repository
public class CostumerDAOImpl implements CustomerDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Customer findById(long id) {
        return em.find(Customer.class, id);
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        return em.merge(customer);
    }

    @Override
    public List<Customer> findByLastName(String lastName) {
        return em.createQuery("SELECT c from Customer c where LOWER(c.lastName) = LOWER(:lastName)", Customer.class)
            .setParameter("lastName", lastName.toLowerCase())
            .getResultList();
    }

    @Override
    public List<Customer> findByCity(String city) {
        return em.createQuery("SELECT c from Customer c where LOWER(c.address.city) = LOWER(:city)", Customer.class)
            .setParameter("city", city.toLowerCase())
            .getResultList();
    }

    @Override
    public List<Customer> findByEmail(String email) {
        return em.createQuery("SELECT c from Customer c where c.email = :email", Customer.class)
            .setParameter("email", email)
            .getResultList();
    }

    @Override
    public Boolean existByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(c) from Customer c where LOWER(c.email) = LOWER(:email)", Long.class)
            .setParameter("email", email.toLowerCase())
            .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Customer> findByEmailContaining(String keyword) {
        return em.createQuery("SELECT c FROM Customer c WHERE LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))", Customer.class)
            .setParameter("keyword", keyword)
            .getResultList();
    }

    @Override
    public Long countByCity(String city) {
        return em.createQuery("SELECT COUNT(c) FROM Customer c WHERE LOWER(c.address.city) = LOWER(:city)", Long.class)
            .setParameter("city", city.toLowerCase())
            .getSingleResult();
    }

    // ... findById, findAll, findByName, delete variants, bulk updates, etc.
}
```

---

## 3. What the workshop wants: Spring Data (1 small file)

### `repository/CustomerRepository.java`

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ---- required (Part1.md:169-173) ----
    Optional<Customer> findByEmail(String email);
    List<Customer> findByLastNameIgnoreCase(String lastName);
    List<Customer> findByAddressCityIgnoreCase(String city);

    // ---- optional / advanced (Part1.md:175-181) ----
    List<Customer> findByEmailContaining(String keyword);
    List<Customer> findByCreatedAtAfter(Instant date);
    List<Customer> findByCreatedAtBetween(Instant from, Instant to);
    Long countByAddressCity(String city);
    boolean existsByEmail(String email);

    // ---- custom JPQL only when a method name cannot express it ----
    @Modifying
    @Query("UPDATE Customer c SET c.firstName = :firstName WHERE c.email = :email")
    int updateFirstNameByEmail(@Param("firstName") String firstName,
                               @Param("email") String email);
}
```

**Done.** No `...DAOImpl`, no `EntityManager`, no JPQL strings for the derived queries.
Spring Data parses the method names into JPQL at startup.

For free (inherited from `JpaRepository`):
`findById`, `findAll`, `count`, `existsById`, `deleteById`, `delete`,
`save` (persist/merge), `saveAll`, `flush`.

---

## 4. Side-by-side summary

| Aspect                 | DAO pattern (current)                     | Spring Data `JpaRepository` (wanted) |
| ---------------------- | ----------------------------------------- | ------------------------------------ |
| Files per entity       | 2 (`DAO` + `DAOImpl`)                     | 1 (interface only)                   |
| Implementation code    | ~200 lines per entity, hand-written       | none — Spring generates it           |
| Query syntax           | Manual JPQL strings                       | Method name → derived query          |
| Case-insensitive query | `LOWER(...) = LOWER(:x)` by hand          | `findByLastNameIgnoreCase(...)`      |
| Nested property (city) | `c.address.city` in JPQL                  | `findByAddressCityIgnoreCase(...)`   |
| CRUD methods           | Written by hand                           | Inherited for free                   |
| Error risk             | Typo in JPQL / param name → runtime error | Derivations validated at startup     |
| Matches workshop spec? | No (README.md:134-135 admits it)          | Yes                                  |

---

## 5. What to do note

- The **entity classes stay unchanged** — the difference is only in the persistence/repository layer.
- The DAO pattern works but does not satisfy the assignment. Converting to `JpaRepository` deletes
  the entire `dao/` package and replaces it with one small interface per entity.
- Keeping both is optional (a `@Primary`/name clash can arise); the cleanest end state for this
  workshop is Spring Data only.
