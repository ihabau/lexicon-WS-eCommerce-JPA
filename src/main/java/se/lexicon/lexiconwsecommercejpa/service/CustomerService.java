package se.lexicon.lexiconwsecommercejpa.service;

import se.lexicon.lexiconwsecommercejpa.dto.*;

public interface CustomerService {

  // Not one entity type appears in any signature - no Customer, no Address, no
  // UserProfile, only records. That is the whole assignment in one line: the API
  // should not know about @Entity or database relationships. Add a column to the
  // customers table and no controller, client or JSON payload changes, because
  // the response record decides what goes out.
  //
  // register and update take the SAME request type on purpose: both are the same
  // question at two moments, and sharing the type is what lets them share the
  // validation annotations that live on the record.
  CustomerResponse register(CustomerRequest request);

  // CustomerResponse and not Optional<CustomerResponse>: the optionality is
  // already used up by the ResourceNotFoundException, so every caller would
  // otherwise re-handle the empty case. The choice about what "not found" means
  // is made here, once.
  CustomerResponse findById(Long id);

  // CustomerRequest has no `id` component, and that is a guard: if it did, a
  // client could send {"id": 7} to update an id the server never chose. The id
  // in the path is the only one the client gets to influence.
  CustomerResponse update(Long id, CustomerRequest request);

  // add more optional requests

}
