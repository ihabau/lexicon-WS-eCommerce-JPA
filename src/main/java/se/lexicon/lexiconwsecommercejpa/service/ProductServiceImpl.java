package se.lexicon.lexiconwsecommercejpa.service;

import org.springframework.stereotype.Service;

import java.util.List;
import se.lexicon.lexiconwsecommercejpa.dto.*;
import se.lexicon.lexiconwsecommercejpa.entity.*;
import se.lexicon.lexiconwsecommercejpa.exception.ResourceNotFoundException;
import se.lexicon.lexiconwsecommercejpa.mapper.ProductMapper;
import se.lexicon.lexiconwsecommercejpa.repository.CategoryRepository;
import se.lexicon.lexiconwsecommercejpa.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  public ProductServiceImpl(
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      ProductMapper productMapper) {

    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.productMapper = productMapper;
  }

  // Product.category is @JoinColumn(nullable = false). Mapping first and
  // discovering the category is missing afterwards gives an opaque constraint
  // violation, so: look up, throw, THEN map.
  // ResourceNotFoundException carries @ResponseStatus(NOT_FOUND), so a bad
  // category is a 404 with a readable message instead of a 500.
  //
  // The mapper takes the FOUND category because it has no repository and cannot
  // resolve an id itself. Mappers do not fetch. Services do.
  @Override
  public ProductResponse create(ProductRequest request) {

    Category category = categoryRepository.findById(request.categoryId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Category not found with id " + request.categoryId()));

    Product product = productMapper.toEntity(request, category);
    // save() returns the managed instance - the one whose id is populated.
    Product saved = productRepository.save(product);
    return productMapper.toResponse(saved);
  }

  // @Transactional is NOT needed on any of these three: one save or one read, and
  // Spring Data already opens a transaction per repository call. It is for a
  // method that must be all-or-nothing ACROSS SEVERAL calls - only placeOrder.
  //
  // The LAZY category is handled in ProductRepository with @EntityGraph, not
  // here: toCategoryName reads category.getName(), the second hop, which needs
  // the row before the session closes.
  //
  // productMapper::toResponse is a method reference - shorthand for
  // .map(p -> productMapper.toResponse(p)) with no variable to name, so the
  // argument order cannot be wrong.
  @Override
  public List<ProductResponse> findAll() {
    return productRepository.findAll()
        .stream()
        .map(productMapper::toResponse)
        .toList();
  }

  @Override
  public List<ProductResponse> searchByName(String name) {
    return productRepository.findByNameContaining(name)
        .stream()
        .map(productMapper::toResponse)
        .toList();
  }

}
