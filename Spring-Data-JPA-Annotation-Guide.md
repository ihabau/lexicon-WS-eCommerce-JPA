# Spring Boot Data JPA - Annotation Guide

---

### 1. Entity & Table Setup
These annotations tell Spring that a Java class represents a table in your database.

*   **`@Entity`**: Marks the class as a JPA entity. It tells Spring, "Hey, please map this class to a database table!"
*   **`@Table(name = "users")`**: Specifies the exact name of the table in the database. If you don't use this, Spring will just use the class name.
*   **`@Id`**: Marks a field as the **Primary Key** (the unique ID for each row).
*   **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Tells the database to automatically generate the ID (like an auto-incrementing number).
*   **`@Column`**: Used to customize a database column.
    *   `unique = true`: No two rows can have the same value (e.g., email).
    *   `nullable = false`: This field is required (cannot be empty).
    *   `length = 100`: Sets the maximum character limit.
    *   `updatable = false`: Once saved, this value can never be changed (great for `createDate`).
*   **`@Lob`**: Marks a field as a "Large Object" (used for images or very long text).

---

### 2. Relationships (Connecting Tables)
How we link different entities together.

*   **`@OneToOne`**: A 1-to-1 link (e.g., one User has exactly one UserProfile).
    *   `mappedBy = "user"`: Used on the "secondary" side to point back to the owner of the relationship.
    *   `cascade = CascadeType.ALL`: If you save/delete the User, automatically save/delete their Profile too!
*   **`@ManyToOne`**: Many things belong to one (e.g., many Events created by one User).
    *   `fetch = FetchType.LAZY`: Only load the data from the database when we actually ask for it (saves memory/performance).
*   **`@ManyToMany`**: Many to many (e.g., many Users attending many Events).
    *   **`@JoinTable`**: Creates a "hidden" middle table to manage this complex relationship.
*   **`@JoinColumn`**: Defines the name of the "Foreign Key" column that links the tables.

---

### 3. Enumerations & Lifecycle Hooks
*   **`@Enumerated(EnumType.STRING)`**: Tells Spring to store an Enum (like `EventStatus.PLANNED`) as a readable word ("PLANNED") in the database instead of a number.
*   **`@PrePersist`**: A "lifecycle hook." It runs automatically right before the object is saved to the database for the first time (perfect for setting a `createDate`).

---

### 4. Repository & Queries
Repositories are the "engines" that talk to the database.

*   **`JpaRepository<User, Long>`**: By extending this interface, you get all standard database operations (Save, Find, Delete) for free!
*   **`@Query`**: Allows you to write your own custom SQL/JPQL queries when "method names" aren't enough.
*   **`@Param`**: Links your Java method parameters to the variables inside your `@Query`.
*   **`@Modifying`**: Must be used with `@Query` if you are performing an `UPDATE` or `DELETE` instead of a `SELECT`.

---

### 5. Testing
*   **`@DataJpaTest`**: A specialized test annotation. it:
    *   Configures an "in-memory" database (so your real data is safe).
    *   Scans only for `@Entity` and Spring Data JPA repositories.
    *   Wraps every test in a **Transaction** (it rolls back all changes after each test, so the database stays clean).
*   **`@ActiveProfiles("test")`**: Tells Spring to use the configuration from `application-test.properties` during tests.

---

### 6. Lombok (Boilerplate Reducer)
These aren't JPA annotations, but they make our code much cleaner!

*   **`@Getter` / `@Setter`**: Automatically creates your get/set methods.
*   **`@NoArgsConstructor` / `@AllArgsConstructor`**: Generates constructors.
*   **`@ToString` / `@EqualsAndHashCode`**: Generates standard Java methods.
    *   `exclude = {"..."}`: Prevents "infinite loops" in relationships by telling Lombok NOT to look at certain fields when printing or comparing.


