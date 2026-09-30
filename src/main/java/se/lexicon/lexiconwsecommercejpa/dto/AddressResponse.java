package se.lexicon.lexiconwsecommercejpa.dto;

/**
 * AddressResponse - the customer's address as it appears in API responses. The
 * assignment requires CustomerResponse to include an "addressResponse" but never
 * names the class, so this record is implied rather than stated. It carries
 * street / city / zipCode, which is Address MINUS its id.
 *
 * WHY A SEPARATE RECORD? The entity is a database object: it has a surrogate
 * `id`, it can be a lazy Hibernate proxy once it comes back from a repository, and
 * it can be null. None of that belongs in a JSON payload. This record is the
 * flattened, already-resolved version - and dropping the id is a real decision,
 * not an omission.
 *
 * MAY BE NULL, AND THE MAPPER GUARDS IT: CustomerMapper.toAddressResponse returns
 * null when customer.getAddress() is null. The column is optional = false on
 * Customer, so a persisted customer always has an address and that branch only
 * fires for a hand-built object - but a comment is a claim about behaviour, so
 * keep the two in agreement.
 *
 * Records are immutable and have no setters, so every mapper that builds one must
 * use the CANONICAL CONSTRUCTOR - new AddressResponse(a, b, c) - there is no
 * setStreet() to call. The order is the contract: street, city, zipCode.
 *
 * No validation annotations, and that is the rule for every response record:
 * constraints are only evaluated on an inbound @Valid @RequestBody, so on
 * outgoing data they promise a check that never runs. What a response record MAY
 * legitimately carry is serialization configuration - @JsonProperty for a wire
 * name, @JsonInclude(NON_NULL) to drop nulls, @JsonFormat for date shape.
 *
 * This type has no @Size(max = ...) anywhere, matching the Address columns (the
 * assignment gave them no length). If you close that gap in CustomerRequest,
 * close it here too - the constraint belongs on the record that carries the
 * value, and this is the one that carries it OUT.
 *
 * @param street the street line
 * @param city   the city
 * @param zipCode the postal code
 */
public record AddressResponse(
    String street,
    String city,
    String zipCode
) {
}
