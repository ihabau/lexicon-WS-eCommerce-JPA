package se.lexicon.lexiconwsecommercejpa.service;

import java.util.List;

import se.lexicon.lexiconwsecommercejpa.dto.CategoryResponse;

/*
 * Optional in Part 3, but Part 4's diagram wires CategoryController to this, so
 * it has to exist before CategoryController can be written.
 *
 * NO CategoryMapper ON PURPOSE. CategoryResponse is two fields - id and name -
 * and the mapping is `new CategoryResponse(c.getId(), c.getName())`. A mapper
 * class for that would be a class whose only job is one line of constructor
 * call, and it would need to be a @Component injected into a service that has
 * one other dependency. Every other mapper earns its place because the entity
 * and the DTO genuinely disagree (CustomerRequest flattens an embedded
 * Address; OrderMapper builds a whole collection of OrderItems and captures
 * priceAtPurchase). There is no disagreement here to translate.
 *
 * create takes a plain String, not a request record, because a category has
 * exactly one field. A CategoryRequest with a single component would be a
 * record nobody needs to send - the client posts the name as the body.
 */
public interface CategoryService {

  // Create a category after checking the name is not already taken.
  // existsByName in the repository is the check; the UNIQUE constraint on
  // name is what actually guarantees it. The check only turns a constraint
  // violation into a message a human can read.
  CategoryResponse create(String name);

  // JpaRepository already gives the repository a findAll(), so this method has
  // no query to declare - it only has to translate each Category into a
  // CategoryResponse. Returning the entities instead would drag `products`
  // into the JSON and recurse (see Category's javadoc).
  List<CategoryResponse> findAll();
}
