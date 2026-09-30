# Diagram Types & A Full Example App

## 1. Diagram types

### A. UML (Unified Modeling Language) — the classic software diagrams

Split into **structural** (the static shape of a system) and **behavioral** (what happens over time).

#### Structural

| Type | What it shows | Good for |
|---|---|---|
| **Class diagram** | Classes, fields, methods, relationships (inheritance, association, composition) | The OOP design / entities + services |
| **Object diagram** | A snapshot of *instances* at a moment in time | Debugging a specific scenario |
| **Component diagram** | Logical units (controllers, services, DB) and their interfaces/ports | Layered architecture |
| **Deployment diagram** | Physical hardware/nodes and where software runs | Infra, cloud topology |
| **Package diagram** | Groupings of classes into namespaces | Module structure |
| **Composite structure** | Internal parts of one class at runtime | Complex internals |

#### Behavioral

| Type | What it shows | Good for |
|---|---|---|
| **Use case diagram** | Actors + what they can do with the system | Requirements, scope |
| **Sequence diagram** | Messages between objects *over time* (top→bottom) | A single flow end-to-end |
| **Activity diagram** | Flowchart of steps, decisions, parallelism | Business processes, algorithms |
| **State machine diagram** | States of one object + allowed transitions | Lifecycles (Order! Payment!) |
| **Communication diagram** | Sequence without time axis, shows links | Same as sequence, object-centric |
| **Timing diagram** | State of objects across a time axis | Real-time systems |
| **Interaction overview** | A big box of small activity+sequence diagrams | Orchestration of parts |

### B. ERD (Entity-Relationship Diagram) — not UML, but the one you know from JPA

Tables, columns, keys, and cardinality (1:N, N:M). This is what the workshop entities map to.

### C. Mermaid — not a new *kind* of diagram, a **syntax** to write diagrams as text

Mermaid can render many of the above + a few extra project-planning ones.

```mermaid
graph LR
  A[Start] --> B{Branch}
  B -->|yes| C[Work]
  B -->|no| D[Exit]
```

Flowchart — logic, steps, decisions.

```mermaid
sequenceDiagram
  User->>App: login()
  App->>DB: SELECT user
  DB-->>App: row
  App-->>User: token
```

Sequence — the sequence diagram above.

```mermaid
classDiagram
  class Book {
    +String title
    +BigDecimal price
    +findByTitle() List
  }
  Book "1" -- "*" OrderItem
```

Class diagram.

```mermaid
erDiagram
  BOOK ||--o{ ORDER_ITEM : has
  ORDER ||--|{ ORDER_ITEM : contains
```

ER diagram.

```mermaid
stateDiagram-v2
  [*] --> New
  New --> Paid
  Paid --> Shipped
```

State diagram.

**Mermaid-only extras** (no UML equivalent): `gantt` (sprint plan), `gitGraph` (branch history), `pie` (statistics), `mindmap`, `timeline`, `quadrantChart`, `sankey-beta`, `xychart-beta`, `journey` (UX survey).

---

## 2. A small theoretical app: "Bookstore"

A full-stack webshop: **Angular/plain HTML frontend → Spring Boot REST backend → MySQL**. Customer browses books, places an order (gets confirmation), admin manages inventory.

### Use case diagram (requirements / scope)

```mermaid
flowchart LR
  subgraph Actors
    C["Customer"]
    A["Admin"]
  end
  subgraph Bookstore
    UC1["Browse books"]
    UC2["Search by title"]
    UC3["Place order"]
    UC4["Pay with card"]
    UC5["Manage inventory"]
    UC6["View reports"]
  end
  C --- UC1 & UC2 & UC3
  UC3 <--> UC4
  A --- UC5 & UC6
```

### Class diagram (the JPA entities + layers)

```mermaid
classDiagram
  class Book {
    +Long id
    +String title
    +BigDecimal price
    +List~Author~ authors
  }
  class Author {
    +Long id
    +String name
  }
  class Customer {
    +Long id
    +String email
  }
  class Order {
    +Long id
    +Instant orderDate
    +OrderStatus status
  }
  class OrderItem {
    +int quantity
    +BigDecimal priceAtPurchase
  }
  class BookController {
    +getAll() List
    +findByTitle(title) List
  }
  class BookService {
    +findAll()
  }
  class BookRepository {
    +findByTitleContaining()
  }

  Book "1" -- "*" BookController : uses
  BookController --> BookService
  BookService --> BookRepository
  Book "1" -- "*" OrderItem
  Order "1" -- "*" OrderItem
  Order "*" --> "1" Customer
  Book "*" *-- "*" Author
  Order ..> OrderStatus : uses
```

JPA-flavored rules: `OrderItem` copies `priceAtPurchase` (don't read the live price), `Book↔Author` is N:M, `Order` owns its items via cascade.

### ERD (what SQL Workbench actually shows)

```mermaid
erDiagram
  AUTHORS ||--o{ BOOK_AUTHOR : writes
  BOOKS ||--o{ BOOK_AUTHOR : has
  BOOKS ||--o{ ORDER_ITEMS : appears_in
  ORDERS ||--|{ ORDER_ITEMS : contains
  ORDERS }o--|| CUSTOMERS : placed_by

  BOOKS {
    bigint id PK
    varchar(100) title
    decimal(10,2) price
  }
  CUSTOMERS {
    bigint id PK
    varchar(150) email UK
  }
  ORDERS {
    bigint id PK
    datetime order_date
    varchar(10) status
  }
  ORDER_ITEMS {
    bigint id PK
    int quantity
    decimal(10,2) price_at_purchase
  }
```

### Sequence diagram (the core flow: customer places an order)

```mermaid
sequenceDiagram
  participant UI as Frontend
  participant C as OrderController
  participant S as OrderService
  participant R as OrderRepository
  participant DB as MySQL

  UI->>C: POST /api/orders {items, customerId}
  C->>S: placeOrder(request)
  S->>R: save(order)
  R->>DB: INSERT orders + order_items
  DB-->>R: generated id
  R-->>S: order with id
  S-->>C: 201 Created + order json
  C-->>UI: order confirmation
```

### State machine diagram (Order lifecycle)

```mermaid
stateDiagram-v2
  [*] --> NEW
  NEW --> PAID : payment ok
  NEW --> CANCELLED : timeout / user cancels
  PAID --> SHIPPED : warehouse scan
  SHIPPED --> DELIVERED : courier confirms
  DELIVERED --> [*]
  SHIPPED --> REFUNDED : return request
  PAID --> REFUNDED
```

### Activity diagram (order processing as a pipeline)

```mermaid
flowchart TB
  Start([Order placed]) --> Check{Stock available?}
  Check -- no --> Notify("Notify customer") --> End2([Order cancelled])
  Check -- yes --> Reserve["Reserve stock"]
  Reserve --> Charge["Charge card"]
  Charge --> Confirm["Send confirmation email"]
  Confirm --> End1([Done])
```

### Deployment diagram (where things physically run)

```mermaid
flowchart TB
  subgraph Browser["Customer Browser"]
    UI["HTML/JS frontend"]
  end
  subgraph Server["Linux server :443"]
    NGINX["Nginx reverse proxy"]
    APP["Spring Boot (bookstore-api.jar)"]
    DB["MySQL 8"]
  end
  Browser -- "HTTPS :443" --> NGINX
  NGINX -- ":8080" --> APP
  APP -- "JDBC :3306" --> DB
```

### Gantt (planning the build — Mermaid-native)

```mermaid
gantt
  title Bookstore MVP
  dateFormat YYYY-MM-DD
  section Backend
  Entities + JPA        :a1, 2026-01-05, 4d
  Repositories          :a2, after a1, 2d
  REST controllers      :a3, after a2, 3d
    section Frontend
    Catalog page          :b1, 2026-01-10, 5d
    Checkout              :b2, after b1, 4d
```

---

## 3. The real project: full UML, Part 1 → Part 3

Diagrams of the **actual** `lexicon-ws-ecommerce-jpa` codebase, not the Bookstore example above.
Read them in order: Part 1 builds the customer, Part 2 bolts the shop onto it, Part 3 wraps both in
a service layer. Mermaid `classDiagram` source is in every fenced block, so you can paste it straight
into a `.md`, GitHub, or the [Mermaid live editor](https://mermaid.live).

Legend: `*--` composition (owns), `o--` aggregation, `<|--` implements/extends, `..>` uses
(dependency), `-->` plain reference. `1` / `0..1` / `0..*` are multiplicities.

### 3.1 Part 1 — Customer, Address, UserProfile

```mermaid
classDiagram
    class Address {
        -Long id
        -String street
        -String city
        -String zipCode
    }

    class UserProfile {
        -Long id
        -String nickName
        -String phoneNumber
        -String bio
        -Customer customer
    }

    class Customer {
        -Long id
        -String firstName
        -String lastName
        -String email
        -Instant createdAt
        -Address address
        -UserProfile profile
        -onCreate() void
    }

    Customer "1" *-- "1" Address : address_id
    Customer "0..1" *-- "0..1" UserProfile : profile_id
    UserProfile --> "0..1" Customer : customer

    note for Customer "OWNER of both relations.<br/>Both foreign keys (address_id,<br/>profile_id) live in the customers table.<br/>@PrePersist sets createdAt."
    note for Address "Standalone - holds no back-reference,<br/>which is why it is the ONLY entity<br/>allowed @ToString / @EqualsAndHashCode."
    note for UserProfile "INVERSE side (mappedBy = profile).<br/>profile is OPTIONAL: a customer<br/>may have no profile."
```

**The two relationships that make this part interesting**

| | `Customer.address` | `Customer.profile` |
|---|---|---|
| Cardinality | one-to-one, **mandatory** | one-to-one, **optional** |
| Owner | `Customer` (`@JoinColumn`) | `Customer` (`@JoinColumn`) |
| Inverse side | none — `Address` is standalone | `UserProfile` (`mappedBy = "profile"`) |
| Cascade | `ALL` + `orphanRemoval` | `ALL` + `orphanRemoval` |
| Bidirectional? | No | Yes |
| Deleting the customer | deletes the address row | deletes the profile row |

### 3.2 Part 1 — Repositories

```mermaid
classDiagram
    class JpaRepository~T, ID~ {
        <<interface>>
        +save(T entity) T
        +findAll() List~T~
        +findById(ID id) Optional~T~
        +existsById(ID id) boolean
        +existsByEmail(String email) boolean
        +count() long
        +deleteById(ID id) void
    }

    class CustomerRepository {
        <<interface>>
        +findByEmail(String) Optional~Customer~
        +findByLastNameIgnoreCase(String) List~Customer~
        +findByAddressCityIgnoreCase(String) List~Customer~
        +findByEmailContaining(String) List~Customer~
        +findByCreatedAtAfter(Instant) List~Customer~
        +findByCreatedAtBetween(Instant, Instant) List~Customer~
        +countByAddressCity(String) Long
        +existsByEmail(String) boolean
        +existsByFullName(String) boolean
    }

    class UserProfileRepository {
        <<interface>>
        +findByNickName(String) List~UserProfile~
        +findByPhoneNumberContaining(String) List~UserProfile~
        +findByBioIsNotNull() List~UserProfile~
        +findByNickNameStartingWith(String) List~UserProfile~
        +countByPhoneNumberStartingWith(String) Long
    }

    class AddressRepository {
        <<interface>>
        +findByZipCode(String) List~Address~
        +findByCity(String) List~Address~
        +countCustomersByZipCode(String) Long
    }

    JpaRepository~T, ID~ <|-- CustomerRepository
    JpaRepository~T, ID~ <|-- UserProfileRepository
    JpaRepository~T, ID~ <|-- AddressRepository

    note for JpaRepository "Every repository is an INTERFACE with<br/>no implementation class. Spring Data<br/>generates the proxy at startup and<br/>derives the SQL from the method name."
```

### 3.3 Part 2 — Catalog and orders

```mermaid
classDiagram
    class Category {
        -Long id
        -String name
        -List~Product~ products
    }

    class Product {
        -Long id
        -String name
        -BigDecimal price
        -List~String~ imageUrls
        -Category category
        -List~Promotion~ promotions
    }

    class Promotion {
        -Long id
        -String code
        -LocalDate startDate
        -LocalDate endDate
        -List~Product~ products
    }

    class Order {
        -Long id
        -Instant orderDate
        -OrderStatus status
        -Customer customer
        -List~OrderItem~ items
        -onCreate() void
    }

    class OrderItem {
        -Long id
        -int quantity
        -BigDecimal priceAtPurchase
        -Order order
        -Product product
    }

    class OrderStatus {
        <<enumeration>>
        CREATED
        PAID
        SHIPPED
        CANCELLED
    }

    Category "1" <-- "0..*" Product : category_id
    Product "1" *-- "0..*" String : imageUrls
    Product "1" <-- "0..*" OrderItem : product_id
    Customer "1" <-- "0..*" Order : customer_id
    Order "1" *-- "1..*" OrderItem : order_id
    Order --> OrderStatus : status
    Product "*" -- "*" Promotion : products_promotions

    note for Product "OWNER of the M:N with Promotion<br/>(join table products_promotions).<br/>NO cascade here: promotions outlive products,<br/>so deleting a product must not delete a promotion."
    note for Order "INVERSE side of items, but owns their lifecycle:<br/>cascade ALL + orphanRemoval.<br/>@PrePersist rejects an order with no items."
    note for OrderItem "priceAtPurchase is a SNAPSHOT copy of the<br/>product price at the moment of ordering.<br/>Reading the live price later would rewrite history."
    note for Product "imageUrls is an @ElementCollection:<br/>a separate product_images table holding<br/>nothing but strings - no id, no entity."
```

**Ownership and cascade — the table to memorise**

| Relationship | Type | Owner (holds the FK) | Inverse side | Cascade | Fetch |
|---|---|---|---|---|---|
| `Customer` → `Address` | 1:1 | `Customer` | — | `ALL` + orphanRemoval | EAGER (default) |
| `Customer` → `UserProfile` | 1:1 | `Customer` | `UserProfile` | `ALL` + orphanRemoval | EAGER (default) |
| `Category` → `Product` | 1:N | `Product` | `Category` | none | LAZY |
| `Product` → `imageUrls` | element collection | `Product` | — | default | LAZY |
| `Product` ↔ `Promotion` | M:N | `Product` (join table) | `Promotion` | **none, deliberately** | LAZY |
| `Customer` → `Order` | 1:N | `Order` | — | none | LAZY |
| `Order` → `OrderItem` | 1:N | `Order` (inverse, but owns lifecycle) | `Order` `mappedBy` | `ALL` + orphanRemoval | LAZY |
| `OrderItem` → `Product` | N:1 | `OrderItem` | — | none | LAZY |

### 3.4 Part 2 — Repositories

```mermaid
classDiagram
    class JpaRepository~T, ID~ {
        <<interface>>
    }

    class CategoryRepository {
        <<interface>>
        +findByNameIgnoreCase(String) Optional~Category~
        +existsByName(String) boolean
        +findByNameContaining(String) List~Category~
    }

    class ProductRepository {
        <<interface>>
        +findByCategoryNameIgnoreCase(String) List~Product~
        +findByPriceBetween(BigDecimal, BigDecimal) List~Product~
        +findByNameContaining(String) List~Product~
        +findByPriceLessThan(BigDecimal) List~Product~
        +countByCategoryNameIgnoreCase(String) Long
        +findAllByOrderByPriceAsc() List~Product~
        +findAllByOrderByPriceDesc() List~Product~
        +findByCategoryId(Long) List~Product~
    }

    class PromotionRepository {
        <<interface>>
        +findByCode(String) Optional~Promotion~
        +findByStartDateAfter(LocalDate) List~Promotion~
        +findByEndDateBefore(LocalDate) List~Promotion~
        +findByEndDateIsNull() List~Promotion~
        +findActivePromo(LocalDate) List~Promotion~
    }

    class OrderRepository {
        <<interface>>
        +findByCustomerId(Long) List~Order~
        +findByStatus(OrderStatus) List~Order~
        +findByOrderDateAfter(Instant) List~Order~
        +findByOrderDateBetween(Instant, Instant) List~Order~
        +findByProduct(Product) List~Order~
        +countByStatus(OrderStatus) Long
        +findByCustomerIdAndStatus(Long, OrderStatus) List~Order~
    }

    class OrderItemRepository {
        <<interface>>
        +findByOrderId(Long) List~OrderItem~
        +findByProduct(Product) List~OrderItem~
        +findByQuantityGreaterThan(int) List~OrderItem~
    }

    JpaRepository <|-- CategoryRepository
    JpaRepository <|-- ProductRepository
    JpaRepository <|-- PromotionRepository
    JpaRepository <|-- OrderRepository
    JpaRepository <|-- OrderItemRepository

    note for OrderRepository "findByStatus carries @EntityGraph(items)<br/>to load the collection in the same query.<br/>The ONLY query in the project that needs it."
    note for PromotionRepository "findActivePromo needs a @Query:<br/>a method name cannot express<br/>OR + IS NULL."
```

### 3.5 Part 3 — Service layer (DTOs, mappers, services)

This is the diagram the teacher checks. Note that the **controller layer does not exist yet** — Part 4.

```mermaid
classDiagram
    class CustomerRequest {
        <<record>>
        +String firstName
        +String lastName
        +String email
        +String password
        +String street
        +String city
        +String zipCode
    }

    class CustomerResponse {
        <<record>>
        +Long id
        +String fullName
        +String email
        +AddressResponse addressResponse
    }

    class AddressResponse {
        <<record>>
        +String street
        +String city
        +String zipCode
    }

    class CustomerMapper {
        +toEntity(CustomerRequest) Customer
        +updateEntity(Customer, CustomerRequest) void
        +toResponse(Customer) CustomerResponse
        -toAddressResponse(Customer) AddressResponse
        -applyToCustomer(Customer, CustomerRequest) void
        -newAddress(CustomerRequest) Address
        -applyToAddress(Address, CustomerRequest) void
    }

    class CustomerService {
        <<interface>>
        +register(CustomerRequest) CustomerResponse
        +findById(Long) CustomerResponse
        +update(Long, CustomerRequest) CustomerResponse
    }

    class CustomerServiceImpl {
        -CustomerRepository customerRepository
        -CustomerMapper customerMapper
        +register(CustomerRequest) CustomerResponse
        +findById(Long) CustomerResponse
        +update(Long, CustomerRequest) CustomerResponse
    }

    class ResourceNotFoundException {
        -String message
    }

    class ProductService {
        <<interface>>
    }

    class OrderService {
        <<interface>>
    }

    CustomerService <|.. CustomerServiceImpl : implements
    CustomerServiceImpl ..> CustomerRepository : queries
    CustomerServiceImpl ..> CustomerMapper : delegates
    CustomerServiceImpl ..> ResourceNotFoundException : throws
    CustomerMapper ..> CustomerRequest : reads
    CustomerMapper ..> CustomerResponse : builds
    CustomerMapper ..> AddressResponse : builds
    CustomerMapper ..> Customer : builds
    CustomerResponse *-- AddressResponse : nested

    note for CustomerRequest "Java record: immutable, no setters,<br/>canonical constructor only.<br/>Has NO id field on purpose - that is<br/>the mass-assignment guard."
    note for CustomerResponse "fullName does not exist in the<br/>database. It is derived in the<br/>mapper from first + last name."
    note for CustomerMapper "@Component - Spring injects it.<br/>updateEntity mutates the EXISTING<br/>Address; creating a new one would<br/>trigger orphanRemoval deletion."
    note for CustomerServiceImpl "constructor injection,<br/>no @Autowired field.<br/>No controller exists yet."
```

### 3.6 The whole system, all three parts

```mermaid
classDiagram
    direction TB

    namespace PART-3-SERVICE {
        class CustomerController {
            <<not yet>>
        }
        class CustomerService {
            <<interface>>
        }
        class CustomerServiceImpl {
        }
        class CustomerMapper {
        }
        class ProductService {
            <<interface>>
        }
        class OrderService {
            <<interface>>
        }
    }

    namespace PART-3-DTO {
        class CustomerRequest {
            <<record>>
        }
        class CustomerResponse {
            <<record>>
        }
        class AddressResponse {
            <<record>>
        }
    }

    namespace PART-2-DOMAIN {
        class Category {
        }
        class Product {
        }
        class Promotion {
        }
        class Order {
        }
        class OrderItem {
        }
        class OrderStatus {
            <<enumeration>>
        }
    }

    namespace PART-1-DOMAIN {
        class Customer {
        }
        class Address {
        }
        class UserProfile {
        }
    }

    namespace REPOSITORIES {
        class CustomerRepository {
            <<interface>>
        }
        class ProductRepository {
            <<interface>>
        }
        class OrderRepository {
            <<interface>>
        }
        class CategoryRepository {
            <<interface>>
        }
        class PromotionRepository {
            <<interface>>
        }
    }

    CustomerController ..> CustomerService : HTTP
    CustomerService <|.. CustomerServiceImpl
    CustomerServiceImpl ..> CustomerMapper
    CustomerServiceImpl ..> CustomerRepository
    ProductService ..> ProductRepository
    OrderService ..> OrderRepository

    CustomerMapper ..> CustomerRequest
    CustomerMapper ..> CustomerResponse
    CustomerResponse *-- AddressResponse

    CustomerRepository ..> Customer
    ProductRepository ..> Product
    OrderRepository ..> Order
    CategoryRepository ..> Category
    PromotionRepository ..> Promotion

    Order "1" *-- "1..*" OrderItem : order_id
    OrderItem --> Product : product_id
    Order --> OrderStatus
    Product --> Category : category_id
    Product "*" -- "*" Promotion
    Customer "1" *-- "1" Address
    Customer "0..1" *-- "0..1" UserProfile
    Customer "1" <-- "0..*" Order : customer_id
```

### 3.7 Things the diagrams deliberately do not show

- **Lombok-generated members.** `@Getter`, `@Setter`, `@AllArgsConstructor` and `@NoArgsConstructor`
  give every entity ~30 members. Drawing them makes the diagram unreadable and teaches nothing.
- **`toString` / `equals` / `hashCode`.** Removed from every entity except `Address`, because the
  bidirectional relations (`Customer` ↔ `UserProfile`, `Order` ↔ `OrderItem`, `Product` ↔ `Category`,
  `Product` ↔ `Promotion`) would recurse forever. Equality for a managed entity is its `id`.
- **Collection classes.** `List<OrderItem> items` is drawn as `OrderItem`, not
  `List<OrderItem>`, because the multiplicity (`"1..*"`) already says it is a collection.
- **The `JpaRepository` implementation.** Spring Data generates a proxy at startup; there is no
  `CustomerRepositoryImpl` file to draw.
- **`ResourceNotFoundException` living in `data.exception`.** It should be a top-level
  `exception` package — the diagram shows where it belongs, not where it currently is.