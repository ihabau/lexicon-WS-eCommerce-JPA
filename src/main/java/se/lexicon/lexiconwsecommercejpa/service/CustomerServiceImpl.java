package se.lexicon.lexiconwsecommercejpa.service;

import org.springframework.stereotype.Service;
import se.lexicon.lexiconwsecommercejpa.dto.*;
import se.lexicon.lexiconwsecommercejpa.entity.*;
import se.lexicon.lexiconwsecommercejpa.exception.*;   // ResourceNotFoundException
import se.lexicon.lexiconwsecommercejpa.mapper.CustomerMapper;
import se.lexicon.lexiconwsecommercejpa.repository.*;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
    this.customerRepository = customerRepository;
    this.customerMapper = customerMapper;
  }

  // "is this email FREE?" - existsByEmail answers yes/no and that is all this needs.
  //
  // "profile is missing" is correct, not an oversight: Customer.profile is the
  // OPTIONAL half of the one-to-one and CustomerRequest has no profile fields.
  //
  // No @Transactional: register is one save, and a single call cannot half-finish.
  // Only placeOrder spans several repository calls and needs it.
  @Override
  public CustomerResponse register(CustomerRequest request) {
    if (request == null) throw new IllegalArgumentException("CustomerRequest cannot be null");
    if ( customerRepository.existsByEmail(request.email())) throw new IllegalArgumentException("This email is used by another customer!");

    Customer customer = customerMapper.toEntity(request);
    Customer saved = customerRepository.save(customer);
    return customerMapper.toResponse(saved);

   }

 
  @Override
  public CustomerResponse findById(Long id) {

    Customer customer = customerRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException( "Customer not found with id " + id));

    return customerMapper.toResponse(customer);
  }




  // "does this email belong to SOMEONE ELSE?" - NOT existsByEmail, which only
  // answers yes/no and never "who owns it". A customer updating without changing
  // their email finds their OWN row, so a plain existsByEmail blocks both the
  // legitimate update AND the real thief. findByEmail returns the owner so ids
  // can be compared; the .filter is the self-exclusion.
  //
  // Do not move that filter onto findById(id) above: you just looked the customer
  // up BY id, so "is this not id?" is always false and the check could never
  // throw. A check that cannot fail is noise.
  //
  // updateEntity MUTATES the existing Address instead of replacing it - see
  // CustomerMapper. Customer.address has orphanRemoval, so swapping the object
  // would DELETE the old address row and change address_id for no reason.
  @Override
  public CustomerResponse update(Long id, CustomerRequest request) {

    if (request == null) throw new ResourceNotFoundException("CustomerRequest cannot be null");

    Customer customer = customerRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
    customerRepository.findByEmail(request.email())
        .filter(other -> !other.getId().equals(id))
        .ifPresent(other -> { throw new IllegalArgumentException("This email is used by another customer!"); });
    customerMapper.updateEntity(customer, request);
    return customerMapper.toResponse(customerRepository.save(customer));
  }

}
