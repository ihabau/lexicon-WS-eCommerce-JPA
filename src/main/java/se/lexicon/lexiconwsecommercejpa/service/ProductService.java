package se.lexicon.lexiconwsecommercejpa.service;

import java.util.List;

import se.lexicon.lexiconwsecommercejpa.dto.ProductRequest;
import se.lexicon.lexiconwsecommercejpa.dto.ProductResponse;

public interface ProductService {

  // NOTE THE SHAPE OF ALL THREE METHODS: they speak ProductRequest /
  // ProductResponse, never Product. The entity never escapes this layer, so a
  // controller cannot save an entity it built itself, and a change to the
  // entity's columns cannot silently change the API's JSON.
  ProductResponse create(ProductRequest request);

  List<ProductResponse> findAll();

  // Named findByNameContaining in the repository on purpose: the service names
  // the business question, the repository names the query it can run, and that
  // translation is the service's job.
  List<ProductResponse> searchByName(String name);
}
