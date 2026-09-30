# Java Project Structures — Reference Trees

Six canonical folder trees: **Monolithic**, **Layered** and **Microservice**, each in a **vanilla Java**
and a **Spring Boot** flavour. Every one includes a custom error handler, because that is the piece
students most often get wrong (throwing raw `SQLException` / `NullPointerException` straight out of a
controller and hoping the framework copes).

Stuck on the vocabulary — *what a Hibernate proxy actually is, why an entity and a DTO are both
"just objects", or why orphan removal deletes your address row?* — jump to **§6**, which decodes the
jargon and walks the entity ↔ DTO round-trip step by step.

---

## 0. Read this first: the two axes are not the same thing

The three names above live on **two different axes**, and mixing them up is the usual source of
"why does my monolith have 40 packages".

| Axis | Question it answers | Options |
|---|---|---|
| **Deployment shape** | How many artifacts do I build and ship? | Monolith, Microservice |
| **Internal organisation** | How do classes talk to each other inside one artifact? | Flat, Layered, Hexagonal/Ports-and-Adapters |

So a **monolith can be layered** (that is the most common combination in the industry), and a
**microservice is nearly always layered or hexagonal internally** — because a service that does not
enforce its own boundaries will leak into its neighbours through shared tables and shared models.

What actually changes between the trees below:

| | Monolith | Layered | Microservice |
|---|---|---|---|
| Deployables | 1 | 1 | many |
| Build modules | 1 | 1 (or N for strict layering) | N + a shared lib |
| Own database | 1, shared | 1, shared | **1 per service** |
| Can bypass a layer? | yes | no (enforced by convention/tests) | no |
| Cross-cutting (errors, auth, logging) | in-process | in-process | duplicated or a shared lib |
| Fails at | runtime, in one process | runtime, in one process | network, partially |
| Best when | small team, unclear domain | same, but the code is growing | many teams, independently deployable features |

A third internal axis worth knowing: **package-by-layer** (`web/`, `service/`, `dao/`) versus
**package-by-feature** (`customer/`, `order/` each with their own web/service/dao). Feature packaging
scales better because a feature's code is in one place; the trees below use package-by-layer because
that is what the Lexicon workshop expects.

---

## 1. Monolithic

One codebase, one artifact, one process, one database. Everyone can call everything — the discipline
is social, not enforced by the compiler.

### 1.1 Monolithic — Vanilla Java

Deliberately few packages. No DI container, no annotations, wiring is explicit in `AppConfig`.

```
shop-monolith/
├── pom.xml                          # single module, no <modules>
├── README.md
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── se/
│   │   │       └── lexicon/
│   │   │           └── shop/
│   │   │               ├── Main.java                  # entry point: builds AppConfig, starts HttpServer
│   │   │               ├── config/
│   │   │               │   ├── AppConfig.java         # manual wiring — new CustomerService(new CustomerDao(ds))
│   │   │               │   └── DbConfig.java          # DataSource + Hikari pool
│   │   │               ├── model/                     # domain objects, plain Java
│   │   │               │   ├── Customer.java
│   │   │               │   ├── Product.java
│   │   │               │   ├── Order.java
│   │   │               │   ├── OrderItem.java
│   │   │               │   └── OrderStatus.java
│   │   │               ├── dao/                       # raw JDBC, one class per table
│   │   │               │   ├── Jdbc.java              # helper: <T> T query(String sql, RowMapper<T>)
│   │   │               │   ├── CustomerDao.java
│   │   │               │   ├── ProductDao.java
│   │   │               │   └── OrderDao.java
│   │   │               ├── service/                   # business rules
│   │   │               │   ├── CustomerService.java
│   │   │               │   ├── ProductService.java
│   │   │               │   └── OrderService.java
│   │   │               ├── web/                       # HTTP edge, uses com.sun.net.httpserver.HttpServer
│   │   │               │   ├── Router.java            # method + path pattern -> handler
│   │   │               │   ├── CustomerHandler.java
│   │   │               │   └── Json.java              # serialize/deserialize (Jackson or hand-rolled)
│   │   │               ├── exception/                # <-- the custom error handler
│   │   │               │   ├── AppException.java      # base: carries HTTP status + error code + message
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   ├── ValidationException.java
│   │   │               │   ├── ConflictException.java
│   │   │               │   ├── ErrorResponse.java     # record: timestamp, status, code, message, path
│   │   │               │   └── ErrorHandler.java      # exception -> ErrorResponse + status (see §4.1)
│   │   │               └── util/
│   │   │                   ├── Ids.java
│   │   │                   └── Clock.java              # injectable so tests can freeze time
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/V1__init.sql
│   └── test/
│       └── java/se/lexicon/shop/
│           ├── service/            # plain JUnit, no Spring, no context to start
│           └── integration/        # Testcontainers or an in-memory DB
├── Dockerfile
└── docker-compose.yml
```

**Request flow** — `Router` → `CustomerHandler` → `CustomerService` → `CustomerDao` → SQL.
`Router` catches everything and delegates to `ErrorHandler` (§4.1) before writing the response.

### 1.2 Monolithic — Spring Boot

Same shape, but the container does the wiring and `spring-boot-starter-web` replaces `Router`.

```
shop-monolith/
├── pom.xml                          # single module
├── Dockerfile
├── docker-compose.yml               # app + postgres
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── se/
│   │   │       └── lexicon/
│   │   │           └── shop/
│   │   │               ├── ShopApplication.java      # @SpringBootApplication
│   │   │               ├── config/
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── JacksonConfig.java
│   │   │               │   └── OpenApiConfig.java
│   │   │               ├── entity/                  # JPA
│   │   │               │   ├── Customer.java
│   │   │               │   ├── Product.java
│   │   │               │   ├── Order.java
│   │   │               │   ├── OrderItem.java
│   │   │               │   └── OrderStatus.java
│   │   │               ├── repository/              # Spring Data interfaces, no impl class
│   │   │               │   ├── CustomerRepository.java
│   │   │               │   ├── ProductRepository.java
│   │   │               │   └── OrderRepository.java
│   │   │               ├── service/                 # @Service, @Transactional lives here
│   │   │               │   ├── CustomerService.java
│   │   │               │   ├── ProductService.java
│   │   │               │   └── OrderService.java
│   │   │               ├── controller/              # @RestController — HTTP in, DTO out
│   │   │               │   ├── CustomerController.java
│   │   │               │   └── OrderController.java
│   │   │               ├── dto/                     # records, @Valid-friendly
│   │   │               │   ├── CustomerRequest.java
│   │   │               │   ├── CustomerResponse.java
│   │   │               │   └── ErrorResponse.java
│   │   │               ├── mapper/                  # @Component, entity <-> dto
│   │   │               │   └── CustomerMapper.java
│   │   │               ├── exception/               # <-- the custom error handler
│   │   │               │   ├── AppException.java
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   ├── GlobalExceptionHandler.java   # @RestControllerAdvice (see §4.2)
│   │   │               │   └── ErrorCode.java       # enum: code + default status
│   │   │               └── util/
│   │   │                   └── ClockConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── db/migration/V1__init.sql
│   └── test/
│       └── java/se/lexicon/shop/
│           ├── service/            # @SpringBootTest or @DataJpaTest slices
│           └── controller/         # @WebMvcTest + MockMvc
└── README.md
```

This is the shape the workshop project is heading towards, and the one the teacher expects to see:
`controller → service → repository`, with `dto` + `mapper` in between and `exception` catching
everything at the edge.

**Layer dependency rule (enforce it or it rots):** `controller` may import `service`, `dto`,
`exception`. `service` may import `repository`, `mapper`, `entity`, `dto`, `exception`. `repository`
may import `entity` and nothing else. A single `ArchUnit` test enforces this in three lines and saves
you an hour of review arguments:

```java
noClasses().that().resideInAPackage("..controller..")
        .should().dependOnClassesThat().resideInAPackage("..repository..");
```

---

## 2. Layered

Still one artifact, but dependencies point in **one direction only** and a DTO/mapper firewall stops
the persistence model from leaking upward. The trade-off: more files, and every field change touches
four classes.

### 2.1 Layered — Vanilla Java

```
shop-layered/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── se/
│   │   │       └── lexicon/
│   │   │           └── shop/
│   │   │               ├── Main.java
│   │   │               ├── config/
│   │   │               │   ├── AppConfig.java
│   │   │               │   └── DbConfig.java
│   │   │               ├── presentation/            # LAYER 1 — HTTP only
│   │   │               │   ├── Router.java
│   │   │               │   ├── CustomerController.java
│   │   │               │   ├── OrderController.java
│   │   │               │   └── dto/
│   │   │               │       ├── CustomerRequest.java
│   │   │               │       ├── CustomerResponse.java
│   │   │               │       ├── OrderRequest.java
│   │   │               │       ├── OrderResponse.java
│   │   │               │       └── ErrorResponse.java
│   │   │               ├── application/             # LAYER 2 — business rules, no HTTP, no SQL
│   │   │               │   ├── service/
│   │   │               │   │   ├── CustomerService.java      # interface
│   │   │               │   │   ├── OrderService.java
│   │   │               │   │   └── impl/
│   │   │               │   │       ├── CustomerServiceImpl.java
│   │   │               │   │       └── OrderServiceImpl.java
│   │   │               │   ├── mapper/
│   │   │               │   │   ├── CustomerMapper.java
│   │   │               │   │   └── OrderMapper.java
│   │   │               │   ├── port/                        # interfaces the core needs
│   │   │               │   │   ├── CustomerRepository.java   # NOT a JDBC class — a port
│   │   │               │   │   └── OrderRepository.java
│   │   │               │   └── exception/
│   │   │               │       ├── AppException.java
│   │   │               │       └── ResourceNotFoundException.java
│   │   │               ├── persistence/           # LAYER 3 — implements the ports
│   │   │               │   ├── jdbc/
│   │   │               │   │   ├── JdbcCustomerRepository.java   # implements application.port.CustomerRepository
│   │   │               │   │   └── JdbcOrderRepository.java
│   │   │               │   ├── model/                     # row/entity objects, persistence-flavoured
│   │   │               │   │   ├── CustomerRow.java
│   │   │               │   │   └── OrderRow.java
│   │   │               │   └── RowMapper.java
│   │   │               ├── error/                 # cross-cutting, callable from any layer
│   │   │               │   ├── ErrorHandler.java
│   │   │               │   ├── ErrorResponseFactory.java
│   │   │               │   └── GlobalExceptionLogger.java
│   │   │               └── util/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/se/lexicon/shop/
│           ├── application/        # fake in-memory ports — no DB needed
│           └── integration/
└── README.md
```

**Request flow**
`Router` → `CustomerController` (unmarshals `CustomerRequest`) → `CustomerService` →
`CustomerMapper.toEntity` → `CustomerRepository` **port** → `JdbcCustomerRepository` →
`CustomerMapper.toResponse` → `CustomerResponse` → JSON.
Errors bubble to `ErrorHandler` (`error/`), which any layer can call.

Note the split: `application/port/CustomerRepository` is an **interface owned by the business layer**,
`persistence/jdbc/JdbcCustomerRepository` is the implementation. That inversion is what lets you unit
test the service against an in-memory fake with zero mocking framework.

### 2.2 Layered — Spring Boot

Same three layers, Spring-flavoured. This is the target shape for the eCommerce workshop.

```
shop-layered/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── se/
│   │   │       └── lexicon/
│   │   │           └── shop/
│   │   │               ├── ShopApplication.java
│   │   │               ├── config/
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── JacksonConfig.java
│   │   │               │   ├── AsyncConfig.java
│   │   │               │   └── OpenApiConfig.java
│   │   │               ├── controller/           # LAYER 1
│   │   │               │   ├── CustomerController.java
│   │   │               │   ├── ProductController.java
│   │   │               │   ├── OrderController.java
│   │   │               │   └── GlobalExceptionHandler.java   # @RestControllerAdvice — see §4.2
│   │   │               ├── dto/                  # records + @Valid constraints
│   │   │               │   ├── CustomerRequest.java
│   │   │               │   ├── CustomerResponse.java
│   │   │               │   ├── ProductRequest.java
│   │   │               │   ├── ProductResponse.java
│   │   │               │   ├── OrderRequest.java
│   │   │               │   ├── OrderResponse.java
│   │   │               │   ├── OrderItemRequest.java
│   │   │               │   ├── OrderItemResponse.java
│   │   │               │   ├── CategoryResponse.java
│   │   │               │   └── ErrorResponse.java
│   │   │               ├── service/              # LAYER 2 — @Service, @Transactional
│   │   │               │   ├── CustomerService.java
│   │   │               │   ├── ProductService.java
│   │   │               │   ├── OrderService.java
│   │   │               │   └── impl/
│   │   │               │       ├── CustomerServiceImpl.java
│   │   │               │       ├── ProductServiceImpl.java
│   │   │               │       └── OrderServiceImpl.java
│   │   │               ├── mapper/               # @Component
│   │   │               │   ├── CustomerMapper.java
│   │   │               │   ├── ProductMapper.java
│   │   │               │   └── OrderMapper.java
│   │   │               ├── repository/           # LAYER 3 — Spring Data, interfaces only
│   │   │               │   ├── CustomerRepository.java
│   │   │               │   ├── ProductRepository.java
│   │   │               │   ├── CategoryRepository.java
│   │   │               │   ├── OrderRepository.java
│   │   │               │   ├── OrderItemRepository.java
│   │   │               │   └── PromotionRepository.java
│   │   │               ├── entity/               # @Entity — Layer 3 only
│   │   │               │   ├── Customer.java
│   │   │               │   ├── Address.java
│   │   │               │   ├── UserProfile.java
│   │   │               │   ├── Product.java
│   │   │               │   ├── Category.java
│   │   │               │   ├── Promotion.java
│   │   │               │   ├── Order.java
│   │   │               │   ├── OrderItem.java
│   │   │               │   └── OrderStatus.java
│   │   │               └── exception/
│   │   │                   ├── AppException.java
│   │   │                   ├── ResourceNotFoundException.java
│   │   │                   ├── DuplicateEmailException.java
│   │   │                   ├── BusinessRuleException.java
│   │   │                   └── ErrorCode.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/V1__init.sql
│   └── test/
│       └── java/se/lexicon/shop/
│           ├── unit/                # plain JUnit + Mockito, no Spring context
│           ├── integration/         # @SpringBootTest + Testcontainers
│           ├── slice/               # @DataJpaTest, @WebMvcTest
│           └── architecture/        # ArchUnit rules — the layer boundaries
├── Dockerfile
├── docker-compose.yml
└── README.md
```

**Request flow**
`POST /api/customers` → `CustomerController` (`@Valid @RequestBody CustomerRequest`) →
`CustomerService.register` (`@Transactional`) → `CustomerMapper.toEntity` →
`CustomerRepository.save` → `CustomerMapper.toResponse` → `CustomerResponse` → 201.
Any exception → `GlobalExceptionHandler` → `ProblemDetail` JSON with the right status.

**The entity/dto split is the whole point.** `entity/` is allowed to be ugly — lazy proxies,
`@JsonIgnore`, bidirectional back-references, `CascadeType`, `@PrePersist`. `dto/` is flat, immutable,
documented and safe to hand to a client. Without the split you end up either exposing your schema or
writing `@JsonIgnore` on half the entity.

---

## 3. Microservice

Many artifacts, **one database per service**, no shared table writes. Communication is over the
network (REST/gRPC) or a broker. Everything in §1 and §2 lives *inside* each service; the new
concerns are discovery, config, tracing, and what to do when the network is down.

### 3.1 Microservice — Vanilla Java

```
shop-platform/
├── pom.xml                          # parent, packaging=pom, <modules>
├── docker-compose.yml
├── k8s/
│   ├── namespace.yaml
│   ├── order-service-deployment.yaml
│   └── order-service-service.yaml
├── common-lib/                      # the ONLY shared code: primitives, error contract, logging
│   ├── pom.xml
│   └── src/main/java/se/lexicon/common/
│       ├── error/
│       │   ├── AppException.java
│       │   ├── ErrorCode.java
│       │   ├── ErrorResponse.java   # the wire contract every service must honour
│       │   └── ErrorHandler.java
│       ├── http/
│       │   ├── RestClient.java      # tiny wrapper over java.net.http.HttpClient
│       │   └── Retry.java
│       ├── json/
│       │   └── Json.java
│       └── observability/
│           ├── TraceContext.java    # W3C traceparent propagation
│           └── Metrics.java
├── gateway/                         # single entry point: routing, rate limit, auth
│   ├── pom.xml
│   └── src/main/java/se/lexicon/gateway/
│       ├── GatewayMain.java
│       ├── Router.java
│       └── filter/
│           ├── CorrelationIdFilter.java
│           ├── RateLimitFilter.java
│           └── AuthFilter.java
├── order-service/                   # <-- same internal shape as the layered tree
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/se/lexicon/order/
│       │   │   ├── OrderServiceMain.java
│       │   │   ├── config/
│       │   │   │   ├── AppConfig.java
│       │   │   │   └── HttpConfig.java
│       │   │   ├── web/            # LAYER 1
│       │   │   │   ├── Router.java
│       │   │   │   ├── OrderController.java
│       │   │   │   └── LocalExceptionHandler.java   # uses common-lib ErrorHandler
│       │   │   ├── application/     # LAYER 2
│       │   │   │   ├── service/OrderService.java
│       │   │   │   ├── port/InventoryPort.java       # calls inventory-service over HTTP
│       │   │   │   └── event/OrderEventPublisher.java
│       │   │   ├── persistence/     # LAYER 3 — order-service DB ONLY
│       │   │   │   ├── OrderDao.java
│       │   │   │   └── OutboxDao.java
│       │   │   └── model/           # Order, OrderItem, OrderStatus — not shared with anyone
│       │   └── resources/
│       │       └── application.properties
│       └── test/
├── inventory-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/se/lexicon/inventory/ ...     # its own web/application/persistence
├── customer-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/se/lexicon/customer/ ...
└── notification-service/            # consumer only, no public API
    ├── pom.xml
    ├── Dockerfile
    └── src/main/java/se/lexicon/notification/
        ├── NotificationMain.java
        ├── consumer/OrderEventConsumer.java
        └── channel/EmailChannel.java
```

### 3.2 Microservice — Spring Boot

The realistic version. Every service is a full Spring Boot app with its own DB, its own pipeline, and
its own copy of the cross-cutting concerns (or imports them from `common`).

```
shop-platform/
├── pom.xml                          # parent aggregator
├── docker-compose.yml               # local dev: all services + a DB per service
├── .github/workflows/
│   └── deploy.yml                   # build + test + push image per service
├── k8s/
│   ├── base/                        # shared manifests (kustomize)
│   │   ├── kustomization.yaml
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   └── overlays/
│       ├── dev/
│       ├── staging/
│       └── prod/
├── common/                          # published once, consumed by all services
│   ├── pom.xml
│   └── src/main/java/se/lexicon/common/
│       ├── error/
│       │   ├── AppException.java
│       │   ├── ErrorCode.java
│       │   ├── ErrorResponse.java
│       │   └── GlobalErrorWebConfig.java   # shared @RestControllerAdvice base class
│       ├── event/
│       │   ├── OrderEvent.java             # the integration contract (schema, not code)
│       │   └── EventEnvelope.java
│       └── web/
│           ├── CorrelationIdFilter.java
│           └── PageResponse.java
├── gateway/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/se/lexicon/gateway/
│       │   ├── GatewayApplication.java
│       │   ├── config/SecurityConfig.java
│       │   └── filter/{CorrelationIdFilter,RateLimitFilter,AuthFilter}.java
│       └── resources/application.yml
├── order-service/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src/main/
│   │   ├── java/se/lexicon/order/
│   │   │   ├── OrderServiceApplication.java
│   │   │   ├── config/               # @EnableFeignClients, @EnableKafka, resilience config
│   │   │   ├── controller/           # LAYER 1
│   │   │   │   ├── OrderController.java
│   │   │   │   └── OrderExceptionHandler.java     # extends common GlobalErrorWebConfig
│   │   │   ├── dto/
│   │   │   │   ├── PlaceOrderRequest.java
│   │   │   │   ├── OrderResponse.java
│   │   │   │   └── OrderItemResponse.java
│   │   │   ├── service/              # LAYER 2
│   │   │   │   ├── OrderService.java
│   │   │   │   └── OrderEventPublisher.java
│   │   │   ├── client/               # outbound: inventory + payment
│   │   │   │   ├── InventoryClient.java         # @FeignClient
│   │   │   │   ├── PaymentClient.java
│   │   │   │   └── dto/ReserveStockRequest.java
│   │   │   ├── repository/           # LAYER 3 — order DB only
│   │   │   │   ├── OrderRepository.java
│   │   │   │   └── OutboxRepository.java        # transactional outbox for reliable events
│   │   │   ├── entity/
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── OutboxEvent.java
│   │   │   │   └── OrderStatus.java
│   │   │   ├── messaging/
│   │   │   │   ├── OrderEventPublisher.java
│   │   │   │   └── OutboxRelay.java            # polls outbox -> Kafka
│   │   │   └── exception/
│   │   │       ├── OrderNotFoundException.java
│   │   │       └── InsufficientStockException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   ├── src/test/
│   └── src/test/resources/
│       └── contracts/                # Pact consumer-driven contract tests
├── inventory-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/se/lexicon/inventory/ ...
├── customer-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/se/lexicon/customer/ ...
├── payment-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/se/lexicon/payment/ ...
├── notification-service/            # consumer only
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/se/lexicon/notification/
│       ├── NotificationServiceApplication.java
│       ├── consumer/OrderEventConsumer.java     # @KafkaListener
│       └── channel/{EmailChannel,SmsChannel}.java
└── observability/
    ├── docker-compose.yml           # Prometheus + Grafana + Loki + Jaeger/Tempo
    └── prometheus.yml
```

**Request flow (place an order)**
`POST /api/orders` → **gateway** (auth, rate limit, correlation id) → `OrderController` →
`OrderService` (`@Transactional`, writes Order + OutboxEvent to the **order** DB) → returns 202.
`OutboxRelay` publishes `OrderEvent` to Kafka → `inventory-service` and `notification-service` consume
independently. A partial failure means the order exists but stock is not yet reserved — which is why
the outbox exists.

---

## 4. The custom error handler

Same job in all six trees: **turn an exception into a response the client can act on**, in one place,
without leaking stack traces, SQL text or framework internals.

Naming trap: this is *not* `java.util.logging.ErrorHandler`. Pick another name
(`ErrorMapper`, `ApiErrorHandler`) if you use `java.util.logging`, or the imports will be a mess.

### 4.1 Vanilla Java — exceptions + one mapper

```java
// AppException.java — every exception you throw deliberately extends this
public class AppException extends RuntimeException {
    private final ErrorCode code;          // enum: DUPLICATE_EMAIL(409), NOT_FOUND(404), ...
    private final HttpStatus status;

    public AppException(ErrorCode code, String message) {
        super(message);
        this.code = code;
        this.status = code.defaultStatus();
    }
}

// ErrorResponse.java — what the client sees. Same shape in every service.
public record ErrorResponse(
        String timestamp, String traceId, int status, String code,
        String message, String path, List<FieldError> fieldErrors) {}

// ErrorHandler.java — the single place exceptions become responses
public final class ErrorHandler {
    private final Logger log = Logger.getLogger(ErrorHandler.class.getName());

    public void handle(HttpExchange exchange, Throwable ex) {
        if (ex instanceof AppException app) {                 // expected, our fault to describe
            respond(exchange, app.getStatus(), app.getCode().name(), app.getMessage());
        } else if (ex instanceof ValidationException v) {     // field-level detail
            respond(exchange, 400, "VALIDATION_FAILED", "Request is invalid", v.getFieldErrors());
        } else {                                              // unexpected: log detail, reveal nothing
            log.log(Level.SEVERE, "Unhandled exception on " + exchange.getRequestURI(), ex);
            respond(exchange, 500, "INTERNAL_ERROR", "Something went wrong. Quote the traceId.");
        }
    }
}
```

Two rules that matter more than the code:

- **Log the cause, return the effect.** The stack trace goes to your log with a `traceId`; the client
  gets the `traceId` and nothing else. Never `ex.getMessage()` a raw `SQLException` to a user.
- **Expected vs unexpected is the only branch that matters.** `AppException` → 4xx with a real
  message. Everything else → 500, generic message, full detail logged. Everything else is noise.

### 4.2 Spring Boot — `@RestControllerAdvice` + `ProblemDetail`

Spring 6+ has [RFC 9457 `ProblemDetail`](https://datatracker.ietf.org/doc/html/rfc9457) built in, so
you do not need a custom error body class — you get the media type and the standard fields for free.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ProblemDetail handleApp(AppException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        pd.setTitle(ex.getCode().name());
        pd.setProperty("code", ex.getCode().name());
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)   // @Valid failed on a @RequestBody
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setTitle("VALIDATION_FAILED");
        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        pd.setProperty("fieldErrors", errors);
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)   // unique constraint, FK violation
    public ProblemDetail handleIntegrity(DataIntegrityViolationException ex) {
        // log ex.getMostSpecificCause() — never return it
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Request conflicts with existing data");
    }

    @ExceptionHandler(Exception.class)                          // the catch-all safety net
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {}", req.getRequestURI(), ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error. Contact support with the trace id.");
    }
}
```

`@RestControllerAdvice` is global, so **one** class replaces all the try/catch you were about to
write in every controller. Returns 400 responses like:

```json
{
  "type": "about:blank",
  "title": "VALIDATION_FAILED",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/customers",
  "fieldErrors": [
    { "field": "email", "message": "Invalid email format!" }
  ]
}
```

Two gotchas:

- The catch-all `Exception` handler does **not** cover errors thrown *before* the dispatcher servlet
  (bad JSON body, 404 for an unmapped URL). Set `server.error.include-message=always` or add an
  `ErrorController` if you need those shaped too.
- `spring-boot-starter-validation` is what makes `MethodArgumentNotValidException` possible — it is
  already in this project's `pom.xml`.

### 4.3 Status code cheat sheet

| Code | Meaning | Typical cause | Exception |
|---|---|---|---|
| 400 | Bad request | validation failed, malformed body | `MethodArgumentNotValidException`, `ValidationException` |
| 401 | Unauthenticated | no/invalid token | Spring Security, or your filter |
| 403 | Forbidden | authenticated but not allowed | Spring Security `@PreAuthorize` |
| 404 | Not found | `findById` returned empty | `ResourceNotFoundException` |
| 409 | Conflict | duplicate email, unique constraint, stale version | `ConflictException`, `DataIntegrityViolationException` |
| 422 | Unprocessable | syntactically fine, semantically impossible | `BusinessRuleException` |
| 429 | Too many requests | rate limit | gateway filter |
| 500 | Server error | a bug, `NullPointerException`, DB down | catch-all, log it |
| 503 | Unavailable | downstream service down | resilience4j fallback, `CircuitBreaker` |

The 404 pattern the workshop wants, and the reason it belongs in the service and not the controller:

```java
Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
```

`findById` returns `Optional<Customer>` — never `null`, never a `boolean`. The repository stays honest;
the service decides that "absent" is a client error; the handler decides what that looks like on the
wire. Three layers, three different jobs.

---

## 5. Which one should you build?

| Situation | Pick |
|---|---|
| Learning, one developer, < ~6 months of runway | Vanilla monolith (§1.1) |
| Learning Java/Spring properly, or a real CRUD product | Spring Boot layered (§2.2) |
| Domain is still moving and you do not know the module boundaries yet | Monolith — you cannot split what you have not named |
| Two teams, independently deployable features, clear seams | Microservice (§3.2) |
| You need the database to be the integration boundary | Microservice |
| You cannot operate containers, service discovery, tracing and message brokers | **Monolith, unconditionally** |

Microservices trade a code problem (a big class) for an **operations** problem (a distributed system
that fails in ways you did not write tests for). That trade is only worth it when you have the team
and the tooling to pay for it. For a workshop project, §2.2 is the right answer every time.

---

## 6. Vocabulary: JPA, Hibernate, and the entity ↔ DTO round-trip

The trees above assume you are fluent in "entity", "DTO", "proxy", "cascade", "flush". This section
decodes the words, and answers the question that trips up almost everyone: *if an entity and a DTO
are both plain Java objects, why convert between them at all?*

### 6.1 They really are all just objects — and that is not the problem

Java has exactly one object model. `Customer`, `CustomerRequest` and `CustomerResponse` are all
classes with fields; there is no special "DTO" type at runtime. So the instinct "they look the same,
what's the difference?" is correct, and it is the right question to be asking.

The difference is never **what** an object is. It is **who is allowed to touch it** and **how long it
lives**.

| | `Customer` (entity) | `CustomerRequest` | `CustomerResponse` |
|---|---|---|---|
| Question it answers | what a **row** is | what the **client sent** | what we're **willing to show** |
| Lives for | the whole application | one request | one response |
| Mutable? | yes — setters, dirty checking | no — a record | no — a record |
| Has `id`? | yes, the database assigns it | **no, never** | yes, we choose to hand it out |
| Knows a database exists? | yes | no | no |
| Knows about JSON / HTTP? | no | validation only | yes, this is the wire format |
| Changes when the schema changes? | **yes** | no | no |

That last row is the entire reason the layer exists. If a controller accepted a `Customer` directly,
then adding a `password_hash` column would change your API and dropping `created_at` would break a
client. The DTO is the shock absorber between the schema and the contract.

### 6.2 Why you cannot skip the conversion

**Reason 1 — it is a security boundary.** Look at what the request record does *not* have: an `id`.

```
POST /api/customers   {"id": 7, "email": "attacker@evil.com", ...}
```

Bind that body straight onto a `Customer` entity and the caller has just overwritten row 7. This is
called **mass assignment**, and it is how a great many real-world breaches started. Because
`CustomerRequest` has no `id` field, the client *physically cannot express that request*. Conversion is
a permission check, not boilerplate.

**Reason 2 — the two shapes genuinely disagree, and always will.**

| | Entity / storage shape | DTO / API shape |
|---|---|---|
| Name | `first_name`, `last_name` (two columns) | `fullName` (one string) |
| Address | nested `Address` object with its own id | flat `street` / `city` / `zipCode` |
| Identifiers | surrogate `id`, natural key `email` | only what the client needs |
| Missing data | referential integrity, cascades | nothing it cannot justify |

Storage shape is chosen so the database can sort, index and enforce things. API shape is chosen so a
human client can read it. These are independent decisions that will never fully agree, so *something*
has to translate between them. That something is the mapper.

**Reason 3 — it makes the mapping testable.** `new CustomerMapper()` needs no database, no Spring
context and no mocks. `CustomerRepository` needs all three. Keeping the translation in one plain class
means the fiddly part is verifiable in milliseconds.

### 6.3 The mapper in one trace

`register(request)` — four objects, three shapes, two directions:

```
1. request              CustomerRequest    street="Storgatan", city="Stockholm", no id
        |  customerMapper.toEntity(request)
        v
2. customer + address   Customer / Address id = null   -> Hibernate sees "new row", queues INSERT
        |  customerRepository.save(customer)
        v
3. saved                Customer            id = 7      -> the row now exists
        |  customerMapper.toResponse(saved)
        v
4. response             CustomerResponse    id = 7, fullName = "Ihab Lexicon", no password column
```

Steps 2 and 3 are the **same class**, which is the part that feels odd. But object 2 means "the
customer I am about to create" and object 3 means "the customer that exists in the database". Same
shape, different **status**. That is exactly why `save()` hands you back a *new reference* rather than
filling in your `id` in place: it persists, then returns the managed instance.

Two rules the trace makes visible:

- `createdAt` is never set by the mapper — `@PrePersist` does it. Timestamps are the database's job.
- `password` is dropped by the mapper, because there is no column to put it in. Silently discarding a
  field the client sent is a smell; in a real system you would hash it and store it, never echo it back.

### 6.4 Identity vs value — the distinction that makes it click

| | Entity | DTO |
|---|---|---|
| Compared by | **identity** — is this the same row? | **value** — do these hold the same data? |
| `equals` means | row 7 is row 7 | two identical responses are interchangeable |
| Two of them can be equal but different objects? | **yes** (two Java objects, one row) | no, if equal they are interchangeable |

This is why entities are dangerous to expose and DTOs are not. An entity is a *handle on a database
row*; a DTO is a *value that crossed a wire*. When you see `Customer` you should be thinking "a row
with an identity". When you see `CustomerResponse` you should be thinking "a snapshot I am sending to
someone".

### 6.5 Hibernate's vocabulary, decoded

Hibernate is the framework doing the SQL. JPA is the *specification* it implements — the annotations
and the `EntityManager` interface. Same relationship as JDBC (the spec) and the MySQL driver (an
implementation). When people say "Hibernate does X", they mean "JPA defines X, and Hibernate is the
thing actually on the classpath".

| Word | What it actually means | In this project |
|---|---|---|
| **Entity** | a POJO (Plain Old Java Object — no framework annotations) *plus* `@Entity` | `Customer`, `Product`, `Order` |
| **POJO** | a class with no framework annotations at all. An entity is a POJO that earned `@Entity` | — |
| **Proxy** | a fake subclass Hibernate hands you that loads the real row the first time you call a method on it | why `getAddress()` works without you noticing a query |
| **Persistence context** / session | Hibernate's "unit of work": the bag of objects it tracks so it can work out what changed | opened per repository call |
| **Managed** | in that bag, so Hibernate will save it for you | inside a `@Transactional` method |
| **Detached** | out of the bag, so you must call `save()` yourself | **this is why `update` needs `save()`** |
| **Flush** | Hibernate writing its queued changes to the database | happens at commit, or on an explicit `flush()` |
| **Dirty checking** | at flush, compare each managed object to the row and `UPDATE` whatever differs | why you often see no explicit update query |
| **Cascade** | "if the parent is saved, save the children too" | `cascade = ALL` on `Customer.address` |
| **Orphan removal** | "if a child loses its parent, delete the child" | the trap: a `new Address()` on update deletes the old row |
| **Lazy** | do not load it until somebody actually asks | `FetchType.LAZY` on `Order.items` |
| **Eager** | load it now, always | `@OneToOne` defaults to this, which is why `getAddress()` is safe |

The three that actually change how you write code:

1. **Detached vs managed** decides whether you need `save()`. `findById` opens its own read-only
   transaction which *closes when the method returns*, so the entity you get back is already detached.
   Dirty checking will not save it for you. That is the whole reason `CustomerServiceImpl.update`
   calls `customerRepository.save(customer)` after mutating the entity.
2. **Cascade** means you never insert a child directly. `register` builds an `Address`, hands it to
   `Customer.setAddress(...)`, saves the customer, and the address row appears as a side effect.
3. **Orphan removal** punishes you for creating a fresh child on an update. See §6.6.

### 6.6 The orphan-removal trap, in full

This one causes real bugs, so it is worth spelling out. `Customer.address` is:

```java
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
@JoinColumn(name = "address_id")
private Address address;
```

Two separate tables are involved: an `addresses` row with its own `id`, and a `customers` row holding
the foreign key `address_id`. `Customer` is the **owner** — it stores the pointer.

**What happens if `update` does this:**

```java
Address address = new Address();              // id == null  ->  Hibernate calls this TRANSIENT
address.setStreet(request.street());
customer.setAddress(address);                 // points at a brand new row
customerRepository.save(customer);
```

```sql
INSERT INTO addresses (street, city, zip_code) VALUES (...)   -- new row, gets id = 8
UPDATE customers SET address_id = 8, ... WHERE id = 3
DELETE FROM addresses WHERE id = 7                            -- the old one, now an ORPHAN
```

Hibernate inserts the new address, repoints the customer, and then — because of `orphanRemoval` —
deletes address 7, which is no longer referenced. It technically works, but every single profile
update churns a row and **changes the customer's address id**, so any future table pointing at
`address_id = 7` breaks.

**What it should do** — mutate the row that is already there:

```java
Address address = customer.getAddress();      // managed, already has an id
address.setStreet(request.street());
customer.setAddress(address);                 // now redundant, but harmless
customerRepository.save(customer);
```

```sql
UPDATE addresses SET street = ?, city = ?, zip_code = ? WHERE id = 7
UPDATE customers SET first_name = ?, ... WHERE id = 3
```

No insert, no delete, id stays 7. `@OneToOne` defaults to `FetchType.EAGER`, so `getAddress()`
returns a real object — no `LazyInitializationException`. And `CascadeType.ALL` includes `MERGE`, so
`save(customer)` cascades the changed address down.

This is why `CustomerMapper` has two request-to-entity methods instead of one. `toEntity` is the
create path and legitimately makes a `new Address()`; `updateEntity` is the update path and must
mutate. Putting the rule inside the mapper means it cannot be forgotten by the next service method.

### 6.7 Naming traps

- `ErrorHandler` is also the name of a JDK interface in `java.util.logging`. If you use
  `java.util.logging`, name yours `ApiErrorHandler` or `ErrorMapper` or the imports become a mess.
- `Order` is a **reserved word in SQL**, so the table is `orders` and the class is `Order` — never
  `order`.
- Package names are lowercase by convention: `dto`, not `DTO`. Mixed case works on Windows
  (case-insensitive filesystem) and then breaks the moment the project is built on Linux CI.
- A record is not a "class with getters" — it is a class with a generated `equals`, `hashCode`,
  `toString` and **no mutability**. Two fields with the same values are the same record, which is why
  records are safe to return from a service and entities are not.
