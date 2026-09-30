# Application Layers

## Table of Contents

1. [Software Architectures Overview](#software-architectures-overview)
2. [Multilayered Architecture](#multilayered-architecture)
3. [The Service Layer (The "Brain")](#the-service-layer-the-brain)
4. [Data Transfer Objects (DTOs) & Java Records](#data-transfer-objects-dtos--java-records)
5. [The Event Application](#the-event-application)

---

## Software Architectures Overview

Software architecture describes the **high-level structure of a software system**. It explains how the main components are organized, how they communicate, and how responsibilities are divided.

Think of it as the Master Blueprint for a complex building. While a single room's design (code) is important, the architecture ensures that the plumbing, electricity, and structure all work together to keep the building standing and functional.

It defines:

- The Components: What are the different parts of the system?
- The Communication: How do these parts talk to each other?
- The Organization: How is everything structured to be reliable, easy to fix, and ready to grow?

Three common software architectures are:

- **Monolithic Architecture** — the entire application is built and deployed as one unit.
- **Layered Architecture** — the application is organized into separate layers, where each layer has a specific responsibility.
- **Microservices Architecture** — the application is divided into small, independent services that communicate with each other.

---

## 1. Monolithic Architecture

In a **Monolithic Architecture**, all parts of the application are combined into a single application.

The user interface, business logic, and data access logic are developed and deployed together.

### UML Component Diagram

```mermaid
flowchart TB
    U["👤 User"]

    subgraph APP["Monolithic Application"]
        UI["User Interface"]
        BL["Business Logic"]
        DA["Data Access"]
    end

    DB[("Database")]

    U --> UI
    UI --> BL
    BL --> DA
    DA --> DB
```

### How it works

The **User** interacts with the User Interface. The request is processed by the Business Logic, which uses the Data Access component to read or write information in the Database.

All components belong to the **same application** and are normally deployed together.

### Advantages

- Simple to develop and understand for small applications.
- Easy to deploy because there is only one application.
- Communication between components is straightforward.

### Disadvantages

- Can become difficult to maintain as the application grows.
- A small change may require redeploying the whole application.
- Scaling individual parts of the system is difficult.

---

## 2. Microservices Architecture

In a **Microservices Architecture**, the application is divided into multiple small and independent services.

Each service is responsible for a specific business function. For example, an online store could have separate services for users, products, orders, and payments.

### UML Component Diagram

```mermaid
flowchart TB
    U["👤 User"]
    API["API Gateway"]

    subgraph MS["Microservices System"]
        US["👤 User Service"]
        PS["📦 Product Service"]
        OS["🛒 Order Service"]
        PAY["💳 Payment Service"]
    end

    U --> API

    API --> US
    API --> PS
    API --> OS
    API --> PAY

    US --> UDB[("User DB")]
    PS --> PDB[("Product DB")]
    OS --> ODB[("Order DB")]
    PAY --> PAYDB[("Payment DB")]

    OS --> PS
    OS --> PAY
```

### How it works

The **User** sends a request to an API Gateway, which forwards the request to the correct microservice.

Each microservice has a specific responsibility:

- **User Service** — manages users and accounts.
- **Product Service** — manages products.
- **Order Service** — creates and manages orders.
- **Payment Service** — handles payments.

Services can also communicate with each other. For example, the Order Service may contact the Product Service to check a product and the Payment Service to process a payment.

Microservices commonly manage their own data, allowing services to be developed, deployed, and scaled more independently.

### Advantages

- Services can be developed and deployed independently.
- Individual services can be scaled when needed.
- Easier to divide a large system between multiple development teams.
- A change to one service does not always require redeploying the entire system.

### Disadvantages

- More complex than a monolithic application.
- Communication between services must be carefully managed.
- Requires more infrastructure, monitoring, and deployment management.
- Handling failures across multiple services can be challenging.

---

## 2. Layered Architecture

A **Layered Architecture** organizes an application into separate layers, where each layer has a specific responsibility.

Layered architecture is often implemented as a **monolithic application**. The difference is that a monolithic architecture describes the application as a **single deployable unit**, while layered architecture describes how the code inside that application is **organized and separated by responsibility**.

For example, a monolithic application can be internally divided into three main layers:

- **Presentation Layer** — handles the user interface and user requests.
- **Business Logic Layer** — contains the application's business rules and operations.
- **Data Access Layer** — handles communication with the database.

### UML Component Diagram

```mermaid
flowchart TB
    U["👤 User"]

    subgraph MONOLITH["Single Deployable Application (Monolith)"]
        PL["Presentation Layer<br/>User Interface"]
        BL["Business Logic Layer<br/>Application Rules"]
        DAL["Data Access Layer<br/>Database Operations"]

        PL --> BL
        BL --> DAL
    end

    DB[("Database")]

    U --> PL
    DAL --> DB
```

### How it works

The **Presentation Layer** receives requests from the user and displays the results.

The **Business Logic Layer** processes those requests according to the application's rules.

The **Data Access Layer** communicates with the database to read or store information.

Although these layers have separate responsibilities, they can still be part of the **same application and deployed together as one unit**.

Therefore, **layered architecture and monolithic architecture are not opposites**. A system can be both **monolithic and layered** at the same time:

**Monolithic** → describes **how the application is deployed**.

**Layered** → describes **how the application is organized internally**.

## Layered Architecture

Spring Boot applications are commonly structured into vertical layers, each with a distinct and isolated responsibility.
This architectural pattern, known as **"Layered Architecture"** or **"Multilayered Architecture"**, ensures that the
codebase remains organized, manageable, and easy to extend.

### The Core Principle: One-Way Traffic

The most important rule is that communication only happens in **one direction**:
`Presentation` -> `Service` -> `Persistence` -> `Database`

A layer can only talk to the one directly below it. The "Librarian" (Persistence) never talks to the "Waitstaff" (
Presentation) directly.

```mermaid
graph TD
    subgraph "Your Spring Boot App"
        P[Presentation Layer]
        S[Service Layer]
        L[Persistence Layer]
    end
    DB[(Database)]
    P -->|Uses| S
    S -->|Uses| L
    L -->|Talks to| DB
    style P fill: #1D3557, stroke: #1D3557, stroke-width: 2px, color: #fff
    style S fill: #F1FAEE, stroke: #1D3557, stroke-width: 2px, color: #000
    style L fill: #457B9D, stroke: #1D3557, stroke-width: 2px, color: #fff
    style DB fill: #E63946, stroke: #1D3557, stroke-width: 2px, color: #fff
```

### 1. Presentation Layer (The "Face")

This is the entry point of your application. It handles all incoming requests (orders).

- **In Spring Boot:** This is where your `@RestController` classes live.
- **Responsibilities:**
  - **Receiving Requests:** Taking in JSON data via HTTP (POST, GET, etc.).
  - **Validation:** Checking if the data makes sense (e.g., "Is the email formatted correctly?").
  - **Sending Responses:** Returning the correct status codes (like `201 Created` or `400 Bad Request`).
- **Key Files:** `UserController.java`, `EventController.java`.

### 2. Service Layer (The "Brain")

This is where the actual "work" happens. It coordinates the logic of your application.

- **In Spring Boot:** This is where your `@Service` classes live.
- **Responsibilities:**
  - **Business Rules:** Checking things like "Does this user already exist?" or "Is this venue available?".
  - **Transactions:** Ensuring that if a complex task fails halfway, everything is rolled back (using
    `@Transactional`).
  - **Coordination:** Asking the Persistence layer for data, and the Mapper to translate it.
- **Key Files:** `UserServiceImpl.java`, `EventServiceImpl.java`.

### 3. Persistence Layer (The "Librarian")

This layer is responsible for retrieving and storing data. It doesn't care about business rules; it just manages the "
records".

- **In Spring Boot:** This is where your `@Repository` interfaces live.
- **Responsibilities:**
  - **Database Access:** Running queries to find, save, or delete data.
  - **Entity Management:** Mapping the database rows to Java objects called **Entities**.
- **Key Files:** `UserRepository.java`, `EventRepository.java`.

### 4. Database Layer (The "Bookshelf")

The physical place where your data lives (MySQL, H2, PostgreSQL).

---

## The Service Layer (The "Brain")

### Why do we need a separate Service Layer?

The **Service Layer** acts as the central hub of an application. While it might seem easier to put all logic inside a
Controller, a separate Service Layer provides several key advantages:

- **Reusability:** The same logic can be used by different controllers (e.g., a Web Controller and a REST Controller).
- **Isolation:** The "Brain" doesn't need to know if the request came from a mobile app or a website. It only cares
  about the rules.
- **Testability:** Business rules can be tested independently without starting a web server or a database.
- **Clarity:** Keeping the "Face" (Controller) thin and the "Brain" (Service) focused makes the code much easier to read
  and maintain.

### Interfaces vs. Implementations

In professional Spring Boot development, a Service is typically split into two parts:

1. **The Interface (The "What"):** Defines the available operations without saying _how_ they work.
   - _Example:_ `UserService.java` defines a `register()` method.
2. **The Implementation (The "How"):** Contains the actual code and logic.
   - _Example:_ `UserServiceImpl.java` implements the `register()` method.

**Why do this?** This separation allows for "Loose Coupling." If the implementation needs to change (e.g., switching
from one email provider to another), the rest of the application remains untouched because it only depends on the
interface.

### The @Service Annotation

The `@Service` annotation tells Spring that a class is a **Service Component**.

- When the application starts, Spring scans for this annotation and creates an instance of the class (a "Bean").
- This allows other parts of the app (like the Controller) to "ask" for the service using **Dependency Injection**.
- It also makes the class eligible for advanced features like transaction management.

### Business Logic & Validation

This is where the application's unique "recipes" are stored. While the Controller handles simple data format checks, the
Service Layer handles **Business Rules**:

- **Verification:** "Does this email already exist in our system?"
- **Complex Rules:** "An event cannot be scheduled in the past."
- **Calculations:** "Calculate the total price for 5 tickets including a 10% discount."
- **Security Checks:** "Does this user have permission to cancel this event?"

### Managing Transactions (@Transactional)

One of the most critical jobs of the Service Layer is managing **Transactions**.

A transaction ensures that a series of steps are treated as a single "all-or-nothing" operation. Using the
`@Transactional` annotation:

- If all steps succeed, the changes are saved to the database (Commit).
- If any step fails (e.g., an error occurs halfway), all previous steps are undone (Rollback).

_Example:_ When creating an event and adding the creator as a participant, both steps must succeed. If the participant
cannot be added, the event should not be created at all.

### Coordination: Mappers & Repositories

The Service Layer acts as a "Conductor," coordinating between different specialists:

1. **Mappers (The Translators):** The Service asks the `EntityDtoMapper` to convert incoming "Shipping Boxes" (DTOs)
   into "Ingredients" (Entities).
2. **Repositories (The Librarians):** The Service tells the `UserRepository` or `EventRepository` to find or save data.
3. **Mappers Again:** Before sending a result back, the Service asks the Mapper to translate the "Finished Meal" (
   Entity) into a "Receipt Box" (Response DTO).

### Handling Business Exceptions

When a rule is broken, the Service Layer "raises its hand" by throwing a **Business Exception**.

- Instead of returning `null` or a generic error, the Service throws specific exceptions like `DataNotFoundException` or
  `DuplicateEntryException`.
- This provides clear feedback about what went wrong (e.g., "User not found with ID: 5").
- The Presentation Layer can then catch these exceptions and translate them into a helpful message and the correct HTTP
  status code for the user.

---

## Data Transfer Objects (DTOs) & Java Records

### What are DTOs?

**DTO** stands for **Data Transfer Object**. Think of it as a **Shipping Box**.

When you order something online, the item isn't just thrown into the mail truck; it's placed in a sturdy box designed
for transport. In Spring Boot, a DTO is that box. It carries data between the "Face" (API) and the "Brain" (Service).

**Why use DTOs?**

- **Security:** We don't want to expose our internal database structure (Entities) to the outside world. DTOs allow us
  to share only what is necessary.
- **Decoupling:** If we change our database table, our API doesn't have to break as long as our DTO remains the same.
- **Performance:** A DTO can combine data from multiple entities or skip unnecessary fields, making the response smaller
  and faster.

### Why use Java Records for DTOs?

In modern Spring Boot (Java 17+), we use **Records** to create DTOs. A Record is a special type of class that is perfect
for holding data.

- **Immutability:** Once a Record is created, its data cannot be changed. This makes our data flow safer and more
  predictable.
- **No Boilerplate:** You don't need to write getters, constructors, `equals()`, `hashCode()`, or `toString()`. Java
  does it all for you in one line!
- **Concise:** Instead of 50 lines of code, a DTO can be defined in a single line.

_Example:_

```java
public record UserResponseDTO(Long id, String email, String fullName) {
}
```

### Validation on Records

Records work perfectly with **Jakarta Bean Validation**. We can put "Rules" directly on the record components to ensure
the data is valid before it even reaches our logic.

- `@NotBlank`: Ensures a text field isn't empty.
- `@Email`: Checks for a valid email format.
- `@Size(min=2, max=50)`: Limits the length of a string.
- `@Positive`: Ensures a number is greater than zero.

### DTOs vs. Entities

It's important to understand the difference between these two:

| Feature        | DTO (Shipping Box)                  | Entity (Ingredient)                                  |
| :------------- | :---------------------------------- | :--------------------------------------------------- |
| **Purpose**    | Transporting data to/from the user. | Storing data in the database.                        |
| **Location**   | Presentation & Service Layers.      | Persistence & Service Layers.                        |
| **Mutability** | Usually Immutable (Records).        | Mutable (Regular Classes for JPA).                   |
| **Validation** | Format & Presence (e.g., `@Email`). | Database Constraints (e.g., `@Column(unique=true)`). |

**The Mapper's Job:** The `EntityDtoMapper` acts as the **Translator** that moves data between these two worlds.

---

## The Event Application

To tie everything together, here is a visual representation of the **Event Application** we have implemented. This diagram shows how the specific classes in each layer work together to handle users, their profiles, and the events they create.

```mermaid
graph TD
    subgraph PL ["Presentation Layer (The Face)"]
        UC[UserController]
        EC[EventController]
        UPC[UserProfileController]
        DTOs[Request & Response DTOs]
    end

    subgraph SL ["Service Layer (The Brain)"]
        US[UserServiceImpl]
        ES[EventServiceImpl]
        UPS[UserProfileServiceImpl]
        M[EntityDtoMapper]
    end

    subgraph DL ["Persistence Layer (The Librarian)"]
        UR[UserRepository]
        ER[EventRepository]
        UPR[UserProfileRepository]
    end

    DB[(Database)]

%% Vertical Flow Connections (Top to Bottom)
    PL --> SL
    SL --> DL
    DL --> DB

%% Internal Connections within Layers
    UC & EC & UPC --> DTOs
    US & ES & UPS <--> M

%% Styling
    style UC fill: #1D3557, stroke: #1D3557, stroke-width: 1px, color: #fff
    style EC fill: #1D3557, stroke: #1D3557, stroke-width: 1px, color: #fff
    style UPC fill: #1D3557, stroke: #1D3557, stroke-width: 1px, color: #fff
    style DTOs fill: #1D3557, stroke: #1D3557, stroke-width: 1px, color: #fff

    style US fill: #F1FAEE, stroke: #1D3557, stroke-width: 1px, color: #000
    style ES fill: #F1FAEE, stroke: #1D3557, stroke-width: 1px, color: #000
    style UPS fill: #F1FAEE, stroke: #1D3557, stroke-width: 1px, color: #000
    style M fill: #F1FAEE, stroke: #1D3557, stroke-width: 1px, color: #000

    style UR fill: #457B9D, stroke: #1D3557, stroke-width: 1px, color: #fff
    style ER fill: #457B9D, stroke: #1D3557, stroke-width: 1px, color: #fff
    style UPR fill: #457B9D, stroke: #1D3557, stroke-width: 1px, color: #fff

    style DB fill: #E63946, stroke: #1D3557, stroke-width: 2px, color: #fff
```

- **Separation:** Notice how the `UserController` never talks to the `UserRepository` directly. It always goes through the `UserService`.
- **Coordination:** The `EventServiceImpl` is the most complex "Brain" because it coordinates between the `EventRepository` (to save the event), the `UserRepository` (to find the creator), and the `EntityDtoMapper` (to translate the data).
- **Data Safety:** The Database only knows about **Entities**, while the outside world only knows about **DTOs**. The **Service Layer** ensures they are never mixed up.

## Further Reading

To go deeper on your own time, these are solid starting points:

- **Event-Driven Architecture**
  - [Martin Fowler — "What do you mean by 'Event-Driven'?"](https://martinfowler.com/articles/201701-event-driven.html)
  - [AWS — What is Event-Driven Architecture?](https://aws.amazon.com/event-driven-architecture/)
  - [Microsoft Azure Architecture Center — Event-driven architecture style](https://learn.microsoft.com/en-us/azure/architecture/guide/architecture-styles/event-driven)
  - [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream) — how Spring applications produce and consume events in practice
- **Serverless Architecture**
  - [Mike Roberts — "Serverless Architectures" (martinfowler.com)](https://martinfowler.com/articles/serverless.html)
  - [AWS — What is Serverless?](https://aws.amazon.com/serverless/)
  - [AWS Lambda](https://aws.amazon.com/lambda/) / [Azure Functions](https://azure.microsoft.com/en-us/products/functions) — the two most widely used serverless platforms

---
