package se.lexicon.lexiconwsecommercejpa.mapper;

import java.util.Objects;

import org.springframework.stereotype.Component;

import se.lexicon.lexiconwsecommercejpa.dto.AddressResponse;
import se.lexicon.lexiconwsecommercejpa.dto.CustomerRequest;
import se.lexicon.lexiconwsecommercejpa.dto.CustomerResponse;
import se.lexicon.lexiconwsecommercejpa.entity.Address;
import se.lexicon.lexiconwsecommercejpa.entity.Customer;

/*
 * Stateless translator: entity <-> DTO. @Component so a service can take it as a
 * constructor argument.
 *
 * Four methods, not two. updateEntity is the third public one: toEntity builds a
 * NEW Customer for a registration, but update must modify an EXISTING one, and
 * that is a different operation, not a variation - the loaded entity already has
 * an id and a createdAt, so building a fresh one and saving it would INSERT.
 *
 * ARGUMENT ORDER IS THE MAIN HAZARD HERE. CustomerResponse is a record, so this
 * is its only constructor. Three of the four arguments are similar types, so
 * swapping two COMPILES and puts the full name in the email field. There are no
 * setters to catch it - re-read the component order when you change anything.
 */
@Component
public class CustomerMapper {

  // Done by AI
  // what is important to understand is from where to where and what is the structure the object has to take!
  // ==========================================================================
  // ENTITY -> DTO
  // ==========================================================================

  public CustomerResponse toResponse(Customer customer) {
    Objects.requireNonNull(customer, "customer must not be null when building a CustomerResponse");

    return new CustomerResponse(
        customer.getId(),                 // the id we did not have before save()
        customer.getFirstName() + " " + customer.getLastName(),
        customer.getEmail(),
        toAddressResponse(customer)        // entity -> nested record, NOT the entity itself
    );
  }

  // AddressResponse is a deliberately SMALLER type than Address - no id. Putting
  // the entity in the payload would drag its id and its own associations along.
  // The null guard only fires for a hand-built customer, since the column is
  // nullable = false; a guard that cannot fire is a boundary, not dead code.
  private AddressResponse toAddressResponse(Customer customer) {
    Address address = customer.getAddress();

    if (address == null) {
      return null;
    }

    return new AddressResponse(
        address.getStreet(),
        address.getCity(),
        address.getZipCode()
    );
  }

  // ==========================================================================
  // DTO -> ENTITY
  // ==========================================================================

  public Customer toEntity(CustomerRequest request) {
    Customer customer = new Customer();
    applyToCustomer(customer, request);
    customer.setAddress(newAddress(request));
    return customer;
  }

  // MUTATES the existing Address instead of replacing it, and that is the point.
  // Customer.address carries orphanRemoval = true, so
  //     customer.setAddress(newAddress(request));
  // does not "replace" anything - it detaches the old Address (issuing a DELETE
  // against its row) and inserts a new one, changing address_id. For a pure
  // profile edit that is a delete plus an insert where none was needed.
  public void updateEntity(Customer customer, CustomerRequest request) {
    applyToCustomer(customer, request);

    Address address = customer.getAddress();
    if (address == null) {
      // Defensive: a customer row always has an address, but if one ever appears without it,
      // create it rather than throwing a NullPointerException three lines later.
      customer.setAddress(newAddress(request));
    } else {
      applyToAddress(address, request);
    }
  }

  // ==========================================================================
  // PRIVATE HELPERS - shared by toEntity and updateEntity so a field added to
  // one cannot be forgotten in the other.
  // ==========================================================================

  private void applyToCustomer(Customer customer, CustomerRequest request) {
    customer.setFirstName(request.firstName());
    customer.setLastName(request.lastName());
    customer.setEmail(request.email());
  }

  private Address newAddress(CustomerRequest request) {
    Address address = new Address();
    applyToAddress(address, request);
    return address;
  }

  private void applyToAddress(Address address, CustomerRequest request) {
    address.setStreet(request.street());
    address.setCity(request.city());
    address.setZipCode(request.zipCode());
  }
}
