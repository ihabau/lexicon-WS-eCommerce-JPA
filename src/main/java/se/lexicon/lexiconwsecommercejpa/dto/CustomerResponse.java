package se.lexicon.lexiconwsecommercejpa.dto;

/**
 * CustomerResponse - what a Customer looks like on the way OUT of the API.
 *
 * WHY NOT RETURN THE ENTITY? The entity is a database object carrying things a
 * client must never see or set: a surrogate `id` we expose on the way out but
 * must NOT accept on the way in, `createdAt` (a database concern), and
 * associations that serialise badly or recurse. Returning a record means the
 * HTTP contract is written down explicitly: adding a column to `customers` does
 * not silently change the API, and removing one does not break a client.
 *
 * NOT EVERY ASSOCIATION IS LAZY. Customer.address is a @OneToOne declared
 * without a fetch attribute, and JPA's default for @OneToOne is EAGER - not LAZY,
 * which is the default for @OneToMany/@ManyToMany. So the address arrives loaded
 * with the customer and toResponse() needs no session to read it. The genuinely
 * lazy associations in this project are Order.items, Product.category and
 * OrderItem.product - those are the ones the response mappers must handle.
 *
 * Records are immutable and have no setters, so the mapper must use the
 * CANONICAL CONSTRUCTOR - new CustomerResponse(a, b, c, d) - not field-by-field
 * assignment. And note the ARGUMENT ORDER is a contract: id, fullName, email,
 * addressResponse, in that sequence.
 *
 * NO VALIDATION ANNOTATIONS, AND THAT IS THE RULE FOR RESPONSES: constraints are
 * only evaluated for an inbound @Valid @RequestBody, so on outgoing data they
 * promise a check that never runs. Constraints belong on CustomerRequest.
 *
 * `addressResponse` is a choice, not a rule. The type is already called
 * AddressResponse, so the suffix duplicates the type name in the JSON, and a
 * component named `address` would read better - but Jackson publishes the
 * COMPONENT name, so whatever you pick becomes part of the wire format.
 *
 * `fullName` is LOSSY, which is the honest cost of a derived field: the client
 * cannot recover the original two values (a two-part surname, sorting by last
 * name). That is an acceptable price for a read model AS LONG AS IT IS A
 * DECISION - and it is, because the entity still stores both columns. "Full
 * name" is a PRESENTATION decision, not a storage one: the database correctly
 * keeps two columns because they sort and search differently, but the client
 * wants one string. The mapper is where that decision lives, so both sides can
 * disagree without either being wrong.
 *
 * @param id              the database id, assigned on save
 * @param fullName        firstName + " " + lastName, composed here and nowhere else
 * @param email           unique, used as the login identity
 * @param addressResponse the nested address, flattened to a record rather than an entity
 */
public record CustomerResponse(
    Long id,
    String fullName,
    String email,
    AddressResponse addressResponse
) {
}
